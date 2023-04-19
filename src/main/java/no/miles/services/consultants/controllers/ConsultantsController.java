package no.miles.services.consultants.controllers;

import no.miles.services.consultants.services.ConsultantsService;
import org.openapi.quarkus.consultants_yaml.model.GetConsultantResponse;
import org.openapi.quarkus.consultants_yaml.model.GetOfficesResponse;

import javax.annotation.security.PermitAll;
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
public class ConsultantsController {

    private final ConsultantsService consultantsService;

    public ConsultantsController(ConsultantsService consultantsService) {
        this.consultantsService = consultantsService;
    }

    @GET
    @Path("/")
    @RolesAllowed({"User"})
    public GetConsultantResponse getConsultants(
            @Context SecurityContext ctx,
            @QueryParam("officeId") List<String> officeId,
            @QueryParam("role") List<String> role
    ) {
        var consultants = consultantsService.getConsultants(officeId, role);

        return new GetConsultantResponse()._list(toGeneratedConsultant(consultants));
    }

    @GET
    @Path("/offices")
    @PermitAll
    public GetOfficesResponse getOffices(
            @Context SecurityContext ctx
    ) {
        var offices = consultantsService.getOffices();

        return new GetOfficesResponse()._list(toGeneratedOffice(offices));
    }
}
