package no.miles.services.consultants.integrations.models;

import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;

import java.util.List;

public record SearchCVsRequest(
        List<String> office_ids,
        Integer offset,
        Integer size,
        List<Search> must
) {

    private static final int OFFSET = 0;
    private static final int SIZE = 500;

    public SearchCVsRequest(Office office, Role role) {
        this(
                office != null ? List.of(office.officeId()) : null,
                OFFSET,
                SIZE,
                role != null ? List.of(new Search(
                        new Exact(
                                "custom_tag_id",
                                role.getRoleId()
                        )
                )) : null
        );
    }

    public record Search(
            Exact exact
    ) {
    }

    public record Exact(
            String field,
            String value
    ) {
    }
}
