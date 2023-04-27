package no.miles.services.consultants.controllers.mappers;

import org.openapi.quarkus.consultants_yaml.model.Consultant;
import org.openapi.quarkus.consultants_yaml.model.Office;
import org.openapi.quarkus.consultants_yaml.model.Role;

import java.net.URI;
import java.util.List;

public class ToGeneratedMapper {
    public static List<Consultant> toGeneratedConsultant(List<no.miles.services.consultants.domain.Consultant> consultants) {
        return consultants.stream()
                .map(c -> new Consultant()
                        .name(c.name())
                        .title(c.title())
                        .roles(c.roles().stream().map(r -> Role.fromValue(r.name())).toList())
                        .telephone(c.telephone())
                        .office(c.office().name())
                        .officeId(c.office().officeId())
                        .country(c.office().country())
                        .email(c.email())
                        .imageUrlThumbnail(c.imageUrlThumbnail() != null ? URI.create(c.imageUrlThumbnail()) : null)
                        .imageUrl(c.imageUrl() != null ? URI.create(c.imageUrl()) : null)
                )
                .toList();
    }

    public static List<Office> toGeneratedOffice(List<no.miles.services.consultants.domain.Office> offices) {
        return offices.stream()
                .map(o -> new Office()
                        .officeId(o.officeId())
                        .name(o.name())
                        .country(o.country())
                )
                .toList();
    }
}
