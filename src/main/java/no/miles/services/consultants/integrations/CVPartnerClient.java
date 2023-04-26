package no.miles.services.consultants.integrations;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import no.miles.services.consultants.integrations.models.CVPartnerConsultants;
import no.miles.services.consultants.integrations.models.Country;
import no.miles.services.consultants.integrations.models.SearchCVsRequest;
import no.miles.services.consultants.integrations.models.TooManyRequestException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.TOO_MANY_REQUESTS;

@Consumes("application/json")
@Produces("application/json")
@Path("/")
@Retry(delay = 1000, retryOn = TooManyRequestException.class)
@ExponentialBackoff
@RegisterRestClient(configKey = "cv-partner-api")
@ClientHeaderParam(name = "Authorization", value = "${integrations.cv-partner.apikey}")
public interface CVPartnerClient {

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatusInfo().equals(TOO_MANY_REQUESTS)) {
            return new TooManyRequestException(response.readEntity(String.class));
        }
        return null;
    }

    @POST
    @Path("v4/search")
    CVPartnerConsultants searchCVs(SearchCVsRequest searchCVsRequest);

    @GET
    @Path("v1/countries")
    List<Country> getCountries();
}
