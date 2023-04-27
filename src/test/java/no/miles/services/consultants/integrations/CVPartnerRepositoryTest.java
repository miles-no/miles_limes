package no.miles.services.consultants.integrations;

import io.quarkus.test.junit.QuarkusTest;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
@Disabled
/*
  integration tests to be run against cv partner. no test env for cv partner, so these are disabled by default.
 */
class CVPartnerRepositoryTest {

    @Inject
    CVPartnerRepository cvPartnerRepository;

    @Test
    void getConsultantsOffice() {
        var trd = new Office("5d5d02f05b734b0ea79e02a6", "trondheim", "NO");

        var consultants = cvPartnerRepository.searchConsultants(trd, null);

        System.out.println("consultants = " + consultants);
    }

    @Test
    void getConsultantsRole() {
        var consultants = cvPartnerRepository.searchConsultants(null, Role.FRONTEND);

        System.out.println("consultants = " + consultants);
    }

    @Test
    void getOffices() {
        var offices = cvPartnerRepository.getOffices();

        System.out.println("offices = " + offices);
    }
}