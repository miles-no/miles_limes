package no.miles.services.consultants.domain;

import java.util.Objects;

public record Office(
        String officeId,
        String name,
        String country
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Office office = (Office) o;
        return Objects.equals(officeId, office.officeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(officeId);
    }
}
