package no.miles.services.consultants.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Accessors(fluent = true)
public final class Consultant implements Comparable<Consultant> {
    private String name;
    private String consultantId;
    private String title;
    private Set<Role> roles = new HashSet<>();
    private String telephone;
    private Office office;
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

    public Consultant merge(Consultant c) {
        if (c.roles() != null) {
            roles.addAll(c.roles());
        }
        if (c.office() != null) {
            office = c.office();
        }

        return this;
    }

    @Override
    public int compareTo(Consultant o) {
        if (this.equals(o)) {
            return 0;
        } else {
            return name.compareTo(o.name);
        }
    }
}
