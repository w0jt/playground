package pl.pwpw.playground.application;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

interface ApplicationRepository extends CrudRepository<Application, Long> {

    default Optional<Application> findByApplicationNumber(String applicationNumber) {
        return findByApplicationNumberApplicationNumber(applicationNumber);
    }

    default Stream<Application> streamByEmail(String email) {
        return streamByContactDetailsEmailAddressEmailAddressIgnoreCase(email);
    }

    Optional<Application> findByApplicationNumberApplicationNumber(String applicationNumber);

    Stream<Application> streamByContactDetailsEmailAddressEmailAddressIgnoreCase(String emailAddress);
}
