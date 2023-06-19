package no.miles.services.consultants.domain;

import java.util.*;
import java.util.stream.Collectors;

public class ConsultantCollection {
    private final Map<Consultant, Consultant> consultants = new TreeMap<>();

    public ConsultantCollection() {
    }

    public static ConsultantCollection from(Collection<Consultant> consultants) {
        return new ConsultantCollection().merge(consultants);
    }

    public ConsultantCollection merge(Collection<Consultant> moreConsultants) {
        moreConsultants.forEach(c -> consultants.merge(c, c, Consultant::merge));

        return this;
    }

    public List<Consultant> toList() {
        return consultants.values().stream().toList();
    }

    public ConsultantCollection filterRoles(List<Role> roles) {
        var filteredConsultants = consultants.keySet();
        if (roles != null && !roles.isEmpty()) {
            filteredConsultants = filteredConsultants.stream()
                    .filter(consultant -> consultant.roles().stream().anyMatch(roles::contains))
                    .collect(Collectors.toSet());
        }
        return ConsultantCollection.from(filteredConsultants);
    }

    public ConsultantCollection filterHiddenConsultants() {
        var filteredConsultants = consultants.keySet().stream()
                .filter(consultant -> !consultant.roles().contains(Role.HIDDEN_CONSULTANT))
                .collect(Collectors.toSet());
        return ConsultantCollection.from(filteredConsultants);
    }

    public ConsultantCollection filterOffices(List<String> officeIds) {
        var filteredConsultants = consultants.keySet();
        if (officeIds != null && !officeIds.isEmpty()) {
            var offices = officeIds.stream().map(id -> new Office(id, null, null)).toList();
            filteredConsultants = filteredConsultants.stream()
                    .filter(consultant -> offices.contains(consultant.office()))
                    .collect(Collectors.toSet());
        }
        return ConsultantCollection.from(filteredConsultants);
    }

    public ConsultantCollection filterEmails(List<String> emails) {
        var filteredConsultants = consultants.keySet();
        if (emails != null && !emails.isEmpty()) {
            filteredConsultants = filteredConsultants.stream()
                    .filter(consultant -> emails.contains(consultant.email()))
                    .collect(Collectors.toSet());
        }
        return ConsultantCollection.from(filteredConsultants);
    }

}
