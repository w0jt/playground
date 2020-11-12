package pl.pwpw.playground.application;

import org.springframework.data.repository.CrudRepository;

interface AttachmentRepository extends CrudRepository<Attachment, Long> {
}
