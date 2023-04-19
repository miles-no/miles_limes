package no.miles.services.consultants.integrations.models;

public record Image(
        String url,
        WrappedUrl thumb,
        WrappedUrl fit_thumb,
        WrappedUrl large,
        WrappedUrl small_thumb
) {
    public record WrappedUrl(String url) {

    }
}
