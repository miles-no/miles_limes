package no.miles.services.consultants.integrations.cvpartner.models;

import no.miles.services.consultants.domain.Office;

import java.util.List;

public record Country(
        String _id,
        List<CountryOffice> offices
) {
    public record CountryOffice(
            String _id,
            String name,
            String num_users,
            String country_code
    ) {
        public Office toOffice() {
            return new Office(
                    _id,
                    name,
                    country_code
            );
        }
    }
}
