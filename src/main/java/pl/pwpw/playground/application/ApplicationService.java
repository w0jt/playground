package pl.pwpw.playground.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import pl.pwpw.playground.common.EntityNotFoundException;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
@RequiredArgsConstructor
class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AttachmentRepository attachmentRepository;

    long addAttachment(long applicationId, Attachment.Type type, byte[] contents) {
        checkNotNull(type);
        checkArgument(contents != null && contents.length > 0);

        Application application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find application id = %s", applicationId))
        );
        Attachment attachment = new Attachment(application, type, contents);
        attachmentRepository.save(attachment);
        return attachment.getId();
    }

    ContactDetailsDto getContactDetails(String applicationNumber) {
        Application application = applicationRepository.findByApplicationNumber(applicationNumber).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find application with number = %s", applicationNumber))
        );
        return ContactDetailsDto.from(application.getContactDetails());
    }

    public Set<ApplicationSearchResult> searchByEmail(String email) {
        return applicationRepository.streamByEmail(email)
                .map(ApplicationSearchResult::from)
                .collect(Collectors.toSet());
    }

    @Value
    static class ApplicationSearchResult {
        ApplicationType type;
        String number;
        String lastName;

        static ApplicationSearchResult from(Application application) {
            return new ApplicationSearchResult(
                    application.getApplicationType(),
                    application.getApplicationNumber().getApplicationNumber(),
                    application.getLastName()
            );
        }
    }

    @Value
    static class ContactDetailsDto {
        String emailAddress;
        String phoneNumber;

        static ContactDetailsDto from(ContactDetails details) {
            return new ContactDetailsDto(details.getEmailAddress().getEmailAddress(), details.getPhoneNumber().getPhoneNumber());
        }
    }
}
