package no.miles.services.consultants.services;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.constraint.NotNull;
import no.miles.services.consultants.config.ConsultantsConfig;
import no.miles.services.consultants.integrations.CVPartnerRepository;
import no.miles.services.consultants.models.Consultant;
import no.miles.services.consultants.models.Office;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConsultantsService {

    private final CVPartnerRepository cvPartnerRepository;

    private final ConsultantsConfig consultantsConfig;

    public ConsultantsService(CVPartnerRepository cvPartnerRepository, ConsultantsConfig consultantsConfig) {
        this.cvPartnerRepository = cvPartnerRepository;
        this.consultantsConfig = consultantsConfig;
    }

    @Scheduled(every = "10m")
    void preHeatConsultantsCache() {
        var consultants = getConsultants_internal();

        updateCache(consultants);
    }

    @CachePut("consultants")
    private List<Consultant> updateCache(List<Consultant> consultants) {
        return consultants;
    }

    @NotNull
    @Cacheable("consultants")
    public List<Consultant> getConsultants() {
        return getConsultants_internal();
    }

    public List<Consultant> getConsultants_internal() {
        var offices = getOffices();

        var consultants = offices.stream()
                .flatMap(office -> cvPartnerRepository.searchConsultants(office, null).stream())
                .toList();

        var consultantMap = consultants.stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()));

        var consultantsWithRoles = consultantsConfig.roles().stream()
                .map(role -> cvPartnerRepository.searchConsultants(null, role))
                .flatMap(List::stream)
                .toList();

        addRolesToConsultants(consultantMap, consultantsWithRoles);

        return consultants;
    }

    private void addRolesToConsultants(Map<Consultant, Consultant> allConsultants, List<Consultant> consultantsWithRoles) {
        consultantsWithRoles.forEach(consultantWithRole -> {
            var consultant = allConsultants.get(consultantWithRole);
            if (consultant != null) {
                consultant.roles().addAll(consultantWithRole.roles());
            }
        });
    }

    @NotNull
    public List<Consultant> getConsultants(List<String> offices, List<String> roles) {
        var consultants = getConsultants();

        if (offices != null && !offices.isEmpty()) {
            consultants = consultants.stream()
                    .filter(consultant -> offices.contains(consultant.officeId()))
                    .toList();
        }
        if (roles != null && !roles.isEmpty()) {
            consultants = consultants.stream()
                    .filter(consultant -> consultant.roles().stream().anyMatch(r -> roles.contains(r.id())))
                    .toList();
        }

        return consultants;
    }

    @NotNull
    @Cacheable("offices")
    public List<Office> getOffices() {
        return cvPartnerRepository.getOffices();
    }
}
