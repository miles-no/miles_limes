package no.miles.services.consultants;

import no.miles.services.consultants.domain.Consultant;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;

import java.util.HashSet;
import java.util.List;

public class FakeConsultant {
    public static Consultant makeConsultant(String id, List<Role> roles, Office office) {
        var consultant = new Consultant()
                .consultantId(id)
                .name("name " + id);

        if (roles != null) {
            consultant.roles(new HashSet<>(roles));
        }
        if (office != null) {
            consultant.office(office);
        }

        return consultant;
    }
}
