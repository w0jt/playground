package pl.pwpw.playground.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pwpw.playground.common.EntityNotFoundException;

import javax.transaction.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
@RequiredArgsConstructor
class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AttachmentRepository attachmentRepository;

    long addAttachment(Long applicationId, Attachment.Type type, byte[] contents) {
        checkNotNull(applicationId);
        checkNotNull(type);
        checkArgument(contents != null && contents.length > 0);

        Application application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find application id = %s", applicationId))
        );
        Attachment attachment = new Attachment(application, type, contents);
        attachmentRepository.save(attachment);
        return attachment.getId();
    }
}
