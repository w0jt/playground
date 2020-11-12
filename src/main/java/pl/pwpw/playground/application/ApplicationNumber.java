package pl.pwpw.playground.application;

import lombok.Getter;

import javax.persistence.Embeddable;

/**
 *
 */
@Embeddable
@Getter
class ApplicationNumber {
    private String applicationNumber;
}
