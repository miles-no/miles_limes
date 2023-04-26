package no.miles.services.consultants.integrations;

import no.miles.services.consultants.integrations.models.CV;
import no.miles.services.consultants.integrations.models.CVMetaData;
import no.miles.services.consultants.integrations.models.Country;
import no.miles.services.consultants.integrations.models.SearchCVsRequest;
import no.miles.services.consultants.domain.Consultant;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;

/**
 * Integration to CVPartner API.
 * Docs: <a href="https://docs.cvpartner.com/Introduction">https://docs.cvpartner.com/Introduction</a>
 */
@ApplicationScoped
public class CVPartnerRepository {

    private final CVPartnerClient cvPartnerClient;

    public CVPartnerRepository(@RestClient CVPartnerClient cvPartnerClient) {
        this.cvPartnerClient = cvPartnerClient;
    }

    public List<Consultant> searchConsultants(
            Office office,
            Role role
    ) {
        var consultants = cvPartnerClient.searchCVs(new SearchCVsRequest(office, role)).cvs().stream()
                .map(CVMetaData::cv)
                .map(CV::toConsultant)
                .toList();

        if (office != null) {
            consultants.forEach(consultant -> consultant.office(office));
        }

        if (role != null) {
            consultants.forEach(consultant -> consultant
                    .roles(Set.of(role))
            );
        }

        return consultants;
    }

    public List<Office> getOffices() {
        return cvPartnerClient.getCountries().stream()
                .flatMap(c -> c.offices().stream())
                .map(Country.CountryOffice::toOffice)
                .toList();
    }
}

