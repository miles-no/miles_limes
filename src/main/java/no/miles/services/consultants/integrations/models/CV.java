package no.miles.services.consultants.integrations.models;

import no.miles.services.consultants.models.Consultant;

public record CV(
        Image image,
        String title,
        String telephone,
        String email,
        String name,
        Boolean is_external,
        Boolean is_deactivated,
        String user_id
) {

    public Consultant toConsultant() {
        return new Consultant()
                .name(name)
                .consultantId(user_id)
                .title(title)
                .telephone(telephone)
                .email(email)
                .imageUrl(image != null ? image.url() : null)
                .imageUrlThumbnail(image != null ? image.thumb().url() : null);
    }
}
