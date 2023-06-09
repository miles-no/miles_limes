package no.miles.services.consultants.domain;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import no.miles.services.consultants.FakeConsultant;
import no.miles.services.consultants.MockConsultantsProfile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@QuarkusTest
@TestProfile(MockConsultantsProfile.class)
class ConsultantCollectionTest {

    @Test
    void merge_should_containGivenConsultants_when_wasEmpty() {
        var consultantWithRole = FakeConsultant.makeConsultant("with role", null, null);
        var consultantWithoutRole = FakeConsultant.makeConsultant("without role", null, null);
        var consultantWithMultipleRoles = FakeConsultant.makeConsultant("with multiple roles", null, null);
        var consultants = List.of(
                consultantWithRole,
                consultantWithoutRole,
                consultantWithMultipleRoles
        );

        var result = new ConsultantCollection().merge(consultants);

        assertIterableEquals(List.of(consultantWithMultipleRoles, consultantWithRole, consultantWithoutRole), result.toList());
    }

    @Test
    void merge_should_updateConsultantsWithOffice_when_givenConsultantWithOfficeAndConsultantAlreadyInCollection() {
        var office = new Office("officeId", "office", "no");
        var consultantWithoutOffice = FakeConsultant.makeConsultant("consultant", List.of(Role.DEVELOPMENT), null);
        var cc = ConsultantCollection.from(List.of(consultantWithoutOffice));

        var sameConsultantWithOffice = FakeConsultant.makeConsultant("consultant", null, office);

        cc.merge(List.of(sameConsultantWithOffice));

        assertEquals(1, cc.toList().size(), "duplicate or missing consultant");
        assertEquals("consultant", cc.toList().get(0).consultantId());
        assertEquals(office, cc.toList().get(0).office());
        assertEquals(Set.of(Role.DEVELOPMENT), cc.toList().get(0).roles());
    }

    @Test
    void merge_should_updateConsultantsWithRole_when_givenConsultantWithRoleAndConsultantAlreadyInCollection() {
        var office = new Office("officeId", "office", "no");
        var consultantWithoutRole = FakeConsultant.makeConsultant("consultant", null, office);
        var cc = ConsultantCollection.from(List.of(consultantWithoutRole));

        var sameConsultantWithRole = FakeConsultant.makeConsultant("consultant", List.of(Role.DEVELOPMENT), null);

        cc.merge(List.of(sameConsultantWithRole));

        assertEquals(1, cc.toList().size(), "duplicate or missing consultant");
        assertEquals("consultant", cc.toList().get(0).consultantId());
        assertEquals(office, cc.toList().get(0).office());
        assertEquals(Set.of(Role.DEVELOPMENT), cc.toList().get(0).roles());
    }

    @Test
    void filterRoles_should_ReturnOnlyConsultantsWithRole_when_roleGiven() {
        var office = new Office("officeId", "office", "no");

        var consultantWithRole = FakeConsultant.makeConsultant("with role", List.of(Role.DEVELOPMENT), office);
        var consultantWithoutRole = FakeConsultant.makeConsultant("without role", List.of(Role.DESIGN), office);
        var consultantWithMultipleRoles = FakeConsultant.makeConsultant("with multiple roles", List.of(Role.DEVELOPMENT, Role.DESIGN), office);

        var cc = ConsultantCollection.from(List.of(
                consultantWithRole,
                consultantWithoutRole,
                consultantWithMultipleRoles
        ));

        var result = cc.filterRoles(List.of(Role.DEVELOPMENT));

        assertEquals(2, result.toList().size(), "wrong number of consultants");
        assertEquals(List.of(consultantWithMultipleRoles, consultantWithRole), result.toList());
    }

    @Test
    void filterOffices_should_ReturnOnlyConsultantsWithCorrectOffice_when_officeGiven() {
        var wantedOffice = new Office("wantedOfficeId", "wantedOffice", "no");
        var unwantedOffice = new Office("unwantedOfficeId", "unwantedOffice", "no");

        var consultantWithWantedOffice = FakeConsultant.makeConsultant("with office", List.of(Role.DEVELOPMENT), wantedOffice);
        var consultantWithoutWantedOffice = FakeConsultant.makeConsultant("with wrong office", List.of(Role.DEVELOPMENT), unwantedOffice);
        var consultantWithoutOffice = FakeConsultant.makeConsultant("without office", List.of(Role.DEVELOPMENT), null);

        var cc = ConsultantCollection.from(List.of(
                consultantWithWantedOffice,
                consultantWithoutWantedOffice,
                consultantWithoutOffice
        ));

        var result = cc.filterOffices(List.of(wantedOffice.officeId()));

        assertEquals(1, result.toList().size(), "wrong number of consultants");
        assertEquals(List.of(consultantWithWantedOffice), result.toList());
    }

    @Test
    void toList_should_beSortedByName_when_multipleConsultants() {
        var first = FakeConsultant.makeConsultant("first", "aaFirst aaLast", null, null);
        var second = FakeConsultant.makeConsultant("third", "aaFirst baLast", null, null);
        var third = FakeConsultant.makeConsultant("fourth", "aaFirst waLast", null, null);
        var fourth = FakeConsultant.makeConsultant("second", "waFirst aaLast", null, null);

        var cc = ConsultantCollection.from(List.of(
                second,
                first,
                fourth,
                third
        ));

        var result = cc.toList();

        assertEquals(4, result.size());
        assertIterableEquals(List.of(first, second, third, fourth), result);
    }
}