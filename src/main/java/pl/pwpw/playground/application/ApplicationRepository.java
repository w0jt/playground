package pl.pwpw.playground.application;

import org.springframework.data.repository.CrudRepository;

interface ApplicationRepository extends CrudRepository<Application, Long> {
}
