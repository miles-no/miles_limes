package no.miles.services.consultants;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import no.miles.services.consultants.domain.Consultant;
import no.miles.services.consultants.domain.ConsultantCollection;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static no.miles.services.consultants.FakeConsultant.makeConsultant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(MockConsultantsProfile.class)
class CachedConsultantServiceTest {

    @Inject
    CachedConsultantService cachedConsultantService;

    @InjectMock
    ConsultantService consultantService;
    @CacheName("consultants")
    @Inject
    Cache consultantsCache;
    @CacheName("consultants-offices")
    @Inject
    Cache officeCache;

    @BeforeEach
    void setup() {
        consultantsCache.invalidateAll().await().indefinitely();
        officeCache.invalidateAll().await().indefinitely();
    }

    @Test
    void preHeatConsultantsCache_should_getFromService_when_cacheMiss() {
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(Role.DEVELOPMENT), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        cachedConsultantService.preHeatConsultantsCache();

        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void preHeatConsultantsCache_should_getFromCa_when_cacheMiss() {
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(Role.DEVELOPMENT), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        cachedConsultantService.preHeatConsultantsCache();

        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void getConsultants_should_getConsultantCollectionFromService_when_cacheMIss() {
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(Role.DEVELOPMENT), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(null, null, null);

        assertEquals(consultants, result);
        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void getConsultants_should_returnCachedConsultants_when_cacheHit() {
        var consultants = List.of(new Consultant());
        consultantsCache.get(consultantsCache.getDefaultKey(), key -> ConsultantCollection.from(consultants)).await().indefinitely();

        var result = cachedConsultantService.getConsultants(null, null, null);

        assertEquals(consultants, result);
    }

    @Test
    void getConsultantsWithFilter_should_returnConsultantWithTwoRolesFromService_when_consultantHasTwoRoles() {
        var office = new Office("officeId", "name", "country");
        var consultants = List.of(
                makeConsultant("consultantWithOneRole", List.of(Role.DEVELOPMENT), office),
                makeConsultant("consultantWithTwoRoles", List.of(Role.DEVELOPMENT, Role.DESIGN), office)
        );

        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(null, List.of(Role.DESIGN), null);

        assertEquals(1, result.size(), "wrong number of consultants after filtering");

        var consultantWithTwoRoles = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithTwoRoles"))
                .findFirst();
        assertTrue(consultantWithTwoRoles.isPresent());
    }

    @Test
    void getConsultantsWithFilter_should_returnConsultantWithCorrectOffice_when_officeFilterSet() {
        var officeOne = new Office("officeOne", "officeOne", "country");
        var officeTwo = new Office("officeTwo", "officeTwo", "country");
        var consultants = List.of(
                makeConsultant("consultantWithOneRole", List.of(Role.DEVELOPMENT), officeOne),
                makeConsultant("consultantWithTwoRoles", List.of(Role.DEVELOPMENT, Role.DESIGN), officeTwo)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(List.of("officeTwo"), null, null);

        assertEquals(1, result.size(), "wrong number of consultants after filtering");

        var consultantWithTwoRoles = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithTwoRoles"))
                .findFirst();
        assertTrue(consultantWithTwoRoles.isPresent());
        assertEquals(officeTwo, consultantWithTwoRoles.get().office());
        assertEquals(2, consultantWithTwoRoles.get().roles().size());
        assertEquals(Set.of(Role.DEVELOPMENT, Role.DESIGN), consultantWithTwoRoles.get().roles());
    }

    @Test
    void getConsultantsWithFilter_should_returnConsultantWithCorrectEmail_when_emailFilterSet() {
        var consultants = List.of(
                makeConsultant("consultantWithOneRole", List.of(Role.DEVELOPMENT), null).email("correct"),
                makeConsultant("consultantWithTwoRoles", List.of(Role.DEVELOPMENT, Role.DESIGN), null).email("wrong")
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(null, null, List.of("correct"));

        assertEquals(1, result.size(), "wrong number of consultants after filtering");
        assertEquals(result.get(0).email(), "correct", "wrong consultant after filtering");
    }
}