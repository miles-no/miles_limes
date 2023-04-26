package no.miles.services.consultants.integrations.models;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException(String s) {
        super(s != null ? s : "Too many requests");
    }
}
