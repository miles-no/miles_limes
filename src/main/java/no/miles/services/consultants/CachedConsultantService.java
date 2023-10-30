package no.miles.services.consultants;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.constraint.NotNull;
import no.miles.services.consultants.domain.Consultant;
import no.miles.services.consultants.domain.ConsultantCollection;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class CachedConsultantService {

    private static final String CONSULTANTS_OFFICES_CACHE_NAME = "consultants-offices";
    private static final String CONSULTANTS_CACHE_NAME = "consultants";
    private final ConsultantService consultantService;
    private final CaffeineCache consultantCache;

    public CachedConsultantService(ConsultantService consultantService,
                                   @CacheName(CONSULTANTS_CACHE_NAME) Cache consultantCache) {
        this.consultantService = consultantService;
        this.consultantCache = consultantCache.as(CaffeineCache.class);
    }

    @Scheduled(every = "${app.services.consultants.cache-refresh}")
    void preHeatConsultantsCache() {
        var defaultKey = consultantCache.getDefaultKey();
        CompletableFuture<List<Consultant>> cachedConsultants = consultantCache.getIfPresent(defaultKey);

        if (cachedConsultants == null) {
            consultantCache.get(
                    defaultKey,
                    key -> consultantService.getConsultants()
            ).await().indefinitely();
        } else {
            var consultants = consultantService.getConsultants();

            consultantCache.put(defaultKey, CompletableFuture.completedFuture(consultants));
        }
    }

    @Scheduled(every = "${app.services.consultants.cache-invalidate}", delay = 120)
    void invalidateConsultantsCacheEntries() {
        // Need to invalidate cache entries based on X-Amz-Expires value of Image S3 Pre-Signed URL
        if (consultantCache != null) consultantCache.invalidateAll().await().indefinitely();
    }

    @NotNull
    public List<Consultant> getConsultants(List<String> officeIds, List<Role> roles, List<String> emails) {
        return getConsultantsInternal()
                .filterOffices(officeIds)
                .filterRoles(roles)
                .filterEmails(emails)
                .toList();
    }

    private ConsultantCollection getConsultantsInternal() {
        CompletableFuture<ConsultantCollection> consultants = consultantCache.getIfPresent(consultantCache.getDefaultKey());

        if (consultants == null) {
            return consultantCache.get(
                    consultantCache.getDefaultKey(),
                    key -> consultantService.getConsultants()
            ).await().indefinitely();
        }

        try {
            return consultants.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to retrieve consultants from cache");
        }
    }

    @NotNull
    @CacheResult(cacheName = CONSULTANTS_OFFICES_CACHE_NAME)
    public List<Office> getOffices() {
        return consultantService.getOffices();
    }
}
