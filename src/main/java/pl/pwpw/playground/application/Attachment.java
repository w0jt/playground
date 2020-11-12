package pl.pwpw.playground.application;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PACKAGE)
@Getter
class Attachment {

    enum Type {
        JPG, PDF
    }

    private static final String GENERATOR_NAME = "attachment_id_seq";

    @Id
    @SequenceGenerator(name = GENERATOR_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "application_fkey"), nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Lob
    @Column(columnDefinition = "BLOB", nullable = false)
    private byte[] contents;

    @CreatedDate
    private LocalDateTime createdDate;

    Attachment(Application application, Type type, byte[] contents) {
        this.application = application;
        this.type = type;
        this.contents = contents;
    }
}
