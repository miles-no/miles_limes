package no.miles.services.consultants.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import no.miles.services.consultants.models.Role;

import java.util.List;

@ConfigMapping(prefix = "app.services.consultants")
public interface ConsultantsConfig {
    @WithName("roles")
    List<ConfigRole> configRoles();

    default List<Role> roles() {
        return configRoles().stream().map(configRole -> new Role(configRole.name(), configRole.id())).toList();
    }

    interface ConfigRole {
        String name();

        String id();
    }
}
