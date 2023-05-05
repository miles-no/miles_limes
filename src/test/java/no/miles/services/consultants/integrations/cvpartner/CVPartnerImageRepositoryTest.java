package no.miles.services.consultants.integrations.cvpartner;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import no.miles.services.consultants.MockConsultantsProfile;
import no.miles.services.consultants.domain.Consultant;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;

@QuarkusTest
@TestProfile(MockConsultantsProfile.class)
@Disabled
/*
  integration tests to be run against cv partner. no test env for cv partner, so these are disabled by default.
 */
class CVPartnerImageRepositoryTest {

    @Inject
    CVPartnerImageRepository cvPartnerImageRepository;

    @Test
    void getImage() {
        var consultant = new Consultant().imageUrl(URI.create("replace me manually"));

        var image = cvPartnerImageRepository.getImage(consultant).await().indefinitely();

        System.out.println("consultants = " + image.exists());
    }
}
