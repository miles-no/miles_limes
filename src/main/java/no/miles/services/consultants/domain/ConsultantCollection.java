package no.miles.services.consultants.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultantCollection {
    private final Map<Consultant, Consultant> consultants = new HashMap<>();

    public ConsultantCollection() {
    }

    public static ConsultantCollection from(List<Consultant> consultants) {
        return new ConsultantCollection().merge(consultants);
    }

    public ConsultantCollection merge(List<Consultant> moreConsultants) {
        moreConsultants.forEach(this::addToConsultantsMap);

        return this;
    }

    private void addToConsultantsMap(Consultant consultant) {
        var consultantFromCollection = consultants.get(consultant);
        if (consultantFromCollection == null) {
            consultants.put(consultant, consultant);
        } else {
            consultantFromCollection.merge(consultant);
        }
    }

    public List<Consultant> toList() {
        return consultants.keySet().stream().toList();
    }

    public ConsultantCollection filterRoles(List<Role> roles) {
        var filteredConsultants = toList();
        if (roles != null && !roles.isEmpty()) {
            filteredConsultants = toList().stream()
                    .filter(consultant -> consultant.roles().stream().anyMatch(roles::contains))
                    .toList();
        }
        return ConsultantCollection.from(filteredConsultants);
    }

    public ConsultantCollection filterOffices(List<String> officeIds) {
        var filteredConsultants = toList();
        if (officeIds != null && !officeIds.isEmpty()) {
            var offices = officeIds.stream().map(id -> new Office(id, null, null)).toList();
            filteredConsultants = filteredConsultants.stream()
                    .filter(consultant -> offices.contains(consultant.office()))
                    .toList();
        }
        return ConsultantCollection.from(filteredConsultants);
    }

    public ConsultantCollection filterEmails(List<String> emails) {
        var filteredConsultants = toList();
        if (emails != null && !emails.isEmpty()) {
            filteredConsultants = filteredConsultants.stream()
                    .filter(consultant -> emails.contains(consultant.email()))
                    .toList();
        }
        return ConsultantCollection.from(filteredConsultants);
    }
}
