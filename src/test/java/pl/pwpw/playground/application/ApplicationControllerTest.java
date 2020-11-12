package pl.pwpw.playground.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ApplicationControllerTest {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldAddAttachmentToApplication() throws Exception {
        // given
        Application application = new Application();
        application.setFirstName("Jan");
        Application savedApplication = applicationRepository.save(application);
        Long appId = savedApplication.getAppId();

        // when && then
        mvc
                .perform(
                        MockMvcRequestBuilders
                                .multipart("/api/applications/{appId}/attachments", appId)
                                .file("file", new byte[]{1, 2, 3})
                                .param("type", Attachment.Type.PDF.name())
                )
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void shouldGetContactDetails() throws Exception {
        // given
        Application application = new Application();
        String number = "PL/2020-10-05/1";
        application.setApplicationNumber(new ApplicationNumber(number));
        application.setContactDetails(new ContactDetails(new EmailAddress("foo@bar"), new PhoneNumber("12345")));
        applicationRepository.save(application);

        // when && then
        mvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/applications/{number}/contact-details", number)
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.emailAddress").value("foo@bar"))
                .andExpect(jsonPath("$.phoneNumber").value("12345"));
    }

    @Test
    void shouldFindApplicationByEmail() throws Exception {
        // given
        Application application0 = new Application();
        application0.setApplicationType(ApplicationType.APPLICATION_A);
        application0.setApplicationNumber(new ApplicationNumber("abc"));
        application0.setLastName("whatever0");
        application0.setContactDetails(new ContactDetails(new EmailAddress("foo@bar"), new PhoneNumber("123")));
        Application application1 = new Application();
        application1.setApplicationType(ApplicationType.APPLICATION_A);
        application1.setApplicationNumber(new ApplicationNumber("abc"));
        application1.setLastName("whatever1");
        application1.setContactDetails(new ContactDetails(new EmailAddress("sample@bar"), new PhoneNumber("456")));
        applicationRepository.saveAll(Arrays.asList(application0, application1));

        // when && then
        mvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/api/applications?email=sample@bar")
                )
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].lastName").value("whatever1"));
    }
}