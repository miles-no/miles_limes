package no.miles.services.consultants.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(fluent = true)
public final class Consultant {
    private String name;
    private String consultantId;
    private String title;
    private List<Role> roles = new ArrayList<>();
    private String telephone;
    private String office;
    private String officeId;
    private String country;
    private String email;
    private String imageUrlThumbnail;
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consultant that = (Consultant) o;
        return Objects.equals(consultantId, that.consultantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consultantId);
    }
}
