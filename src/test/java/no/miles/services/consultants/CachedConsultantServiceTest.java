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
    void preHeatConsultantsCache_should_getFromService_when_cacheMiss () {
        var roleOne = new Role("roleOne", "roleOneId");
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(roleOne), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        cachedConsultantService.preHeatConsultantsCache();

        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void preHeatConsultantsCache_should_getFromCa_when_cacheMiss () {
        var roleOne = new Role("roleOne", "roleOneId");
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(roleOne), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        cachedConsultantService.preHeatConsultantsCache();

        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void getConsultants_should_getConsultantCollectionFromService_when_cacheMIss() {
        var roleOne = new Role("roleOne", "roleOneId");
        var office = new Office("officeId", "name", "country");

        var consultants = List.of(
                makeConsultant("consultantWithRole", List.of(roleOne), office)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants();

        assertEquals(consultants, result);
        verify(consultantService, times(1)).getConsultants();
    }

    @Test
    void getConsultants_should_returnCachedConsultants_when_cacheHit() {
        var consultants = List.of(new Consultant());
        consultantsCache.get(consultantsCache.getDefaultKey(), key -> ConsultantCollection.from(consultants)).await().indefinitely();

        var result = cachedConsultantService.getConsultants();

        assertEquals(consultants, result);
    }

    @Test
    void getConsultantsWithFilter_should_returnConsultantWithTwoRolesFromService_when_consultantHasTwoRoles() {
        var roleOne = new Role("roleOne", "roleOneId");
        var roleTwo = new Role("roleTwo", "roleTwoId");
        var office = new Office("officeId", "name", "country");
        var consultants = List.of(
                makeConsultant("consultantWithOneRole", List.of(roleOne), office),
                makeConsultant("consultantWithTwoRoles", List.of(roleOne, roleTwo), office)
        );

        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(null, List.of(roleTwo.id()));

        assertEquals(1, result.size(), "wrong number of consultants after filtering");

        var consultantWithTwoRoles = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithTwoRoles"))
                .findFirst();
        assertTrue(consultantWithTwoRoles.isPresent());
    }

    @Test
    void getConsultantsWithFilter_should_returnConsultantWithCorrectOffice_when_officeFilterSet() {
        var roleOne = new Role("roleOne", "roleOneId");
        var roleTwo = new Role("roleTwo", "roleTwoId");
        var officeOne = new Office("officeOne", "officeOne", "country");
        var officeTwo = new Office("officeTwo", "officeTwo", "country");
        var consultants = List.of(
                makeConsultant("consultantWithOneRole", List.of(roleOne), officeOne),
                makeConsultant("consultantWithTwoRoles", List.of(roleOne, roleTwo), officeTwo)
        );
        when(consultantService.getConsultants()).thenReturn(ConsultantCollection.from(consultants));

        var result = cachedConsultantService.getConsultants(List.of("officeTwo"), null);

        assertEquals(1, result.size(), "wrong number of consultants after filtering");

        var consultantWithTwoRoles = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithTwoRoles"))
                .findFirst();
        assertTrue(consultantWithTwoRoles.isPresent());
        assertEquals(officeTwo, consultantWithTwoRoles.get().office());
        assertEquals(2, consultantWithTwoRoles.get().roles().size());
        assertEquals(Set.of(roleOne, roleTwo), consultantWithTwoRoles.get().roles());
    }
}