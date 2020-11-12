package pl.pwpw.playground.application;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@Entity
class Application {
    @Id
    @SequenceGenerator(name = "app_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_id_seq")
    private Long appId;
    @Embedded
    private ApplicationNumber applicationNumber;
    private String firstName;
    private String lastName;
    @Embedded
    private ContactDetails contactDetails;
    @Enumerated(EnumType.STRING)
    private ApplicationType applicationType;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        for (Attachment attachment : attachments) {
            attachment.setApplication(this);
        }
    }

    void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setApplication(this);
    }
}
