package no.miles.services.consultants.services;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.constraint.NotNull;
import no.miles.services.consultants.config.ConsultantsConfig;
import no.miles.services.consultants.integrations.CVPartnerRepository;
import no.miles.services.consultants.models.Consultant;
import no.miles.services.consultants.models.Office;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConsultantsService {

    private static final String CONSULTANTS_OFFICES_CACHE_NAME = "consultants-offices";
    private static final String CONSULTANTS_CACHE_NAME = "consultants";
    private final CVPartnerRepository cvPartnerRepository;

    private final ConsultantsConfig consultantsConfig;

    private final CaffeineCache consultantCache;

    public ConsultantsService(CVPartnerRepository cvPartnerRepository, ConsultantsConfig consultantsConfig,
                              @CacheName(CONSULTANTS_CACHE_NAME) Cache consultantCache) {
        this.cvPartnerRepository = cvPartnerRepository;
        this.consultantsConfig = consultantsConfig;
        this.consultantCache = consultantCache.as(CaffeineCache.class);
    }

    @Scheduled(every = "${app.services.consultants.cache-refresh}")
    void preHeatConsultantsCache() {
        var defaultKey = consultantCache.getDefaultKey();
        CompletableFuture<List<Consultant>> cachedConsultants = consultantCache.getIfPresent(defaultKey);

        if (cachedConsultants == null) {
            consultantCache.get(
                    defaultKey,
                    key -> getConsultants_internal()
            ).await().indefinitely();
        } else {
            var consultants = getConsultants_internal();

            consultantCache.put(defaultKey, CompletableFuture.completedFuture(consultants));
        }
    }

    @NotNull
    public List<Consultant> getConsultants() {
        CompletableFuture<List<Consultant>> consultants = consultantCache.getIfPresent(consultantCache.getDefaultKey());

        if (consultants == null) {
            return consultantCache.get(
                    consultantCache.getDefaultKey(),
                    key -> getConsultants_internal()
            ).await().indefinitely();
        }

        try {
            return consultants.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to retrieve consultants from cache");
        }
    }

    private List<Consultant> getConsultants_internal() {
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

    private void addRolesToConsultants(Map<Consultant, Consultant> allConsultants,
                                       List<Consultant> consultantsWithRoles) {
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
    @CacheResult(cacheName = CONSULTANTS_OFFICES_CACHE_NAME)
    public List<Office> getOffices() {
        return cvPartnerRepository.getOffices();
    }
}
