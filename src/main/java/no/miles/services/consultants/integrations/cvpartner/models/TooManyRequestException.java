package no.miles.services.consultants.integrations.cvpartner.models;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException(String s) {
        super(s != null ? s : "Too many requests");
    }
}
