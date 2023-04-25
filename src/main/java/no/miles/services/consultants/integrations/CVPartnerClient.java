package no.miles.services.consultants.integrations;

import no.miles.services.consultants.integrations.models.CVPartnerConsultants;
import no.miles.services.consultants.integrations.models.Country;
import no.miles.services.consultants.integrations.models.SearchCVsRequest;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Consumes("application/json")
@Produces("application/json")
@Path("/")
@RegisterRestClient(configKey = "cv-partner-api")
@ClientHeaderParam(name = "Authorization", value = "${integrations.cv-partner.apikey}")
public interface CVPartnerClient {

    @POST
    @Path("v4/search")
    CVPartnerConsultants searchCVs(SearchCVsRequest searchCVsRequest);

    @GET
    @Path("v1/countries")
    List<Country> getCountries();
}
