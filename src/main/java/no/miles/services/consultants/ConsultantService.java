package no.miles.services.consultants;

import no.miles.services.consultants.domain.ConsultantCollection;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.integrations.CVPartnerRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConsultantService {
    private final CVPartnerRepository cvPartnerRepository;

    private final ConsultantsConfig consultantsConfig;

    public ConsultantService(CVPartnerRepository cvPartnerRepository, ConsultantsConfig consultantsConfig) {
        this.cvPartnerRepository = cvPartnerRepository;
        this.consultantsConfig = consultantsConfig;
    }

    public ConsultantCollection getConsultants() {
        var consultants = getOffices().stream()
                .flatMap(office -> cvPartnerRepository.searchConsultants(office, null).stream())
                .toList();

        var consultantsWithRoles = consultantsConfig.roles().stream()
                .flatMap(role -> cvPartnerRepository.searchConsultants(null, role).stream())
                .toList();

        return ConsultantCollection.from(consultants).merge(consultantsWithRoles);
    }

    public List<Office> getOffices() {
        return cvPartnerRepository.getOffices();
    }
}
