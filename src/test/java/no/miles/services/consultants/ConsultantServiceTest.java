package no.miles.services.consultants;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import no.miles.services.consultants.integrations.CVPartnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static no.miles.services.consultants.FakeConsultant.makeConsultant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(MockConsultantsProfile.class)
class ConsultantServiceTest {
    @Inject
    ConsultantService consultantService;
    @InjectMock
    CVPartnerRepository cvPartnerRepository;

    @Test
    void getConsultants_should_returnConsultantWithOneRoleFromService_when_cacheMIssAndConsultantHasOneRole() {
        var office = new Office("officeId", "name", "country");

        when(cvPartnerRepository.searchConsultants(isNull(), eq(Role.DEVELOPMENT)))
                .thenReturn(new ArrayList<>(List.of(
                        makeConsultant("consultantWithRole", List.of(Role.DEVELOPMENT), null)
                )));
        when(cvPartnerRepository.searchConsultants(eq(office), isNull()))
                .thenReturn(new ArrayList<>(List.of(
                        makeConsultant("consultantWithRole", null, office)
                )));

        when(cvPartnerRepository.getOffices()).thenReturn(List.of(office));

        var result = consultantService.getConsultants().toList();

        assertEquals(1, result.size());
        var consultantWithRole = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithRole"))
                .findFirst();
        Assertions.assertTrue(consultantWithRole.isPresent());
        assertEquals(office, consultantWithRole.get().office());
        assertEquals(1, consultantWithRole.get().roles().size());
        assertIterableEquals(List.of(Role.DEVELOPMENT), consultantWithRole.get().roles());

        verify(cvPartnerRepository, times(10)).searchConsultants(any(), any());
        verify(cvPartnerRepository, times(1)).getOffices();
    }

    @Test
    void getConsultants_should_returnConsultantWithTwoRolesFromService_when_cacheMIssAndConsultantHasTwoRoles() {
        var office = new Office("officeId", "name", "country");

        when(cvPartnerRepository.searchConsultants(isNull(), eq(Role.DEVELOPMENT)))
                .thenReturn(new ArrayList<>(List.of(
                        makeConsultant("consultantWithTwoRoles", List.of(Role.DEVELOPMENT), null)
                )));
        when(cvPartnerRepository.searchConsultants(isNull(), eq(Role.DESIGN)))
                .thenReturn(new ArrayList<>(List.of(
                        makeConsultant("consultantWithTwoRoles", List.of(Role.DESIGN), null)
                )));
        when(cvPartnerRepository.searchConsultants(eq(office), isNull()))
                .thenReturn(new ArrayList<>(List.of(
                        makeConsultant("consultantWithTwoRoles", null, office)
                )));

        when(cvPartnerRepository.getOffices()).thenReturn(List.of(office));

        var result = consultantService.getConsultants().toList();

        assertEquals(1, result.size());

        var consultantWithTwoRoles = result.stream()
                .filter(c -> c.consultantId().equals("consultantWithTwoRoles"))
                .findFirst();
        Assertions.assertTrue(consultantWithTwoRoles.isPresent());
        assertEquals(office, consultantWithTwoRoles.get().office());
        assertEquals(2, consultantWithTwoRoles.get().roles().size());
        assertEquals(Set.of(Role.DEVELOPMENT, Role.DESIGN), consultantWithTwoRoles.get().roles());

        verify(cvPartnerRepository, times(10)).searchConsultants(any(), any());
        verify(cvPartnerRepository, times(1)).getOffices();
    }
}