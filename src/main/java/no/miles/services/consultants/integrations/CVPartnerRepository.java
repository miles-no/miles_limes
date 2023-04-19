package no.miles.services.consultants.integrations;

import no.miles.services.consultants.integrations.models.CV;
import no.miles.services.consultants.integrations.models.CVMetaData;
import no.miles.services.consultants.integrations.models.Country;
import no.miles.services.consultants.integrations.models.SearchCVsRequest;
import no.miles.services.consultants.models.Consultant;
import no.miles.services.consultants.models.Office;
import no.miles.services.consultants.models.Role;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

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
            consultants.forEach(consultant -> consultant
                    .office(office.name())
                    .officeId(office.officeId())
                    .country(office.country())
            );
        }

        if (role != null) {
            consultants.forEach(consultant -> consultant
                    .roles(List.of(role))
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

