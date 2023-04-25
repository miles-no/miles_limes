package no.miles.services.consultants.controllers;

import no.miles.services.consultants.CachedConsultantService;
import org.openapi.quarkus.consultants_yaml.model.GetConsultantResponse;
import org.openapi.quarkus.consultants_yaml.model.GetOfficesResponse;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static no.miles.services.consultants.controllers.mappers.ToGeneratedMapper.toGeneratedConsultant;
import static no.miles.services.consultants.controllers.mappers.ToGeneratedMapper.toGeneratedOffice;

@Path("/consultants")
@Consumes({"application/json"})
@Produces({"application/json"})
@RequestScoped
@RolesAllowed({"User"})
public class ConsultantsController {

    private final CachedConsultantService cachedConsultantService;

    public ConsultantsController(CachedConsultantService cachedConsultantService) {
        this.cachedConsultantService = cachedConsultantService;
    }

    @GET
    @Path("/")
    public GetConsultantResponse getConsultants(
            @Context SecurityContext ctx,
            @QueryParam("officeId") List<String> officeId,
            @QueryParam("role") List<String> role
    ) {
        var consultants = cachedConsultantService.getConsultants(officeId, role);

        return new GetConsultantResponse()._list(toGeneratedConsultant(consultants));
    }

    @GET
    @Path("/offices")
    public GetOfficesResponse getOffices(
            @Context SecurityContext ctx
    ) {
        var offices = cachedConsultantService.getOffices();

        return new GetOfficesResponse()._list(toGeneratedOffice(offices));
    }
}
