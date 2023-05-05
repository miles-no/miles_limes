package no.miles.services.consultants.integrations.cvpartner;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;

@Produces("image/x")
@RegisterRestClient(configKey = "cv-partner-image-api")
@Path("")
public interface CVPartnerImageClient {

    @GET
    Uni<File> getImage();
}
