package no.miles.services.consultants.integrations.cvpartner.models;

import java.net.URI;

public record Image(
        URI url,
        WrappedUrl thumb,
        WrappedUrl fit_thumb,
        WrappedUrl large,
        WrappedUrl small_thumb
) {
    public record WrappedUrl(URI url) {

    }
}
