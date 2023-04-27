package no.miles.services.consultants.controllers.mappers;

import no.miles.services.consultants.domain.Role;

public class FromGeneratedMapper {
    public static Role fromGeneratedRole(org.openapi.quarkus.consultants_yaml.model.Role roles) {
        return Role.valueOf(roles.name());
    }
}
