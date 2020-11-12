package pl.pwpw.playground.application;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.pwpw.playground.application.ApplicationService.ApplicationSearchResult;
import pl.pwpw.playground.application.ApplicationService.ContactDetailsDto;
import pl.pwpw.playground.common.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/applications", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    @PostMapping(value = "/{applicationId}/attachments")
    public ResponseEntity<Void> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @PathVariable("applicationId") Long applicationId,
            Attachment.Type type) {

        // to improve this we could:
        // - use the annotation-based validation (javax.validation.constraints)
        // - use something like Apache Tika to recognize the actual file type
        if (type == null) {
            throw new BadRequestException("Unknown attachment type");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new BadRequestException("Empty file contents");
            }
        } catch (IOException e) {
            throw new BadRequestException("Cannot read file contents");
        }
        long attachmentId = service.addAttachment(applicationId, type, bytes);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/" + attachmentId)
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/**/contact-details")
    public ResponseEntity<ContactDetailsDto> getContactDetails(HttpServletRequest request) {
        String pathWithinPattern = extractPathWithinPattern(request);
        if (pathWithinPattern.equals("contact-details")) {
            throw new BadRequestException("Empty application number");
        }
        String number = StringUtils.substringBeforeLast(pathWithinPattern, "/contact-details");
        ContactDetailsDto details = service.getContactDetails(number);
        return ResponseEntity.ok(details);
    }

    private String extractPathWithinPattern(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    @GetMapping(params = "email")
    public ResponseEntity<Set<ApplicationSearchResult>> searchByEmail(@RequestParam("email") String email) {
        Set<ApplicationSearchResult> results = service.searchByEmail(email);
        return ResponseEntity.ok(results);
    }
}
