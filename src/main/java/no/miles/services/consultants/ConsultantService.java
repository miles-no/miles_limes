package no.miles.services.consultants;

import no.miles.services.consultants.domain.ConsultantCollection;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import no.miles.services.consultants.integrations.CVPartnerRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConsultantService {
    private final CVPartnerRepository cvPartnerRepository;

    public ConsultantService(CVPartnerRepository cvPartnerRepository) {
        this.cvPartnerRepository = cvPartnerRepository;
    }

    public ConsultantCollection getConsultants() {
        var consultants = getOffices().stream()
                .flatMap(office -> cvPartnerRepository.searchConsultants(office, null).stream())
                .toList();

        var consultantsWithRoles = Role.values.stream()
                .flatMap(role -> cvPartnerRepository.searchConsultants(null, role).stream())
                .toList();

        return ConsultantCollection.from(consultants).merge(consultantsWithRoles);
    }

    public List<Office> getOffices() {
        return cvPartnerRepository.getOffices();
    }
}
