package pl.pwpw.playground.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.pwpw.playground.common.BadRequestException;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(value = "/{applicationId}/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
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
        long attachmentId = applicationService.addAttachment(applicationId, type, bytes);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/" + attachmentId)
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
