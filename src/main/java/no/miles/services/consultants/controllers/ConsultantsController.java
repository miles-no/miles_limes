package no.miles.services.consultants.controllers;

import no.miles.services.consultants.CachedConsultantService;
import no.miles.services.consultants.controllers.mappers.FromGeneratedMapper;
import no.miles.services.consultants.domain.Consultant;
import org.openapi.quarkus.consultants_yaml.model.GetConsultantResponse;
import org.openapi.quarkus.consultants_yaml.model.GetOfficesResponse;
import org.openapi.quarkus.consultants_yaml.model.Role;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
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
            @QueryParam("officeId") List<String> officeIds,
            @QueryParam("role") List<Role> queryRoles,
            @QueryParam("email") List<String> emails
    ) {

        var roles = queryRoles.stream().map(FromGeneratedMapper::fromGeneratedRole).toList();
        var consultants = cachedConsultantService.getConsultants(officeIds, roles, emails);

        return new GetConsultantResponse()._list(toGeneratedConsultant(consultants));
    }

    @GET
    @Path("/offices")
    public GetOfficesResponse getOffices() {
        var offices = cachedConsultantService.getOffices();

        return new GetOfficesResponse()._list(toGeneratedOffice(offices));
    }

    @GET
    @Produces("image/png")
    @PermitAll
    @Path("/{consultantEmail}/image")
    public File getImage(@PathParam("consultantEmail") String consultantEmail) {
        var consultant = cachedConsultantService.getConsultants(null, null, List.of(consultantEmail))
                .stream()
                .findFirst()
                .map(Consultant::image);

        return consultant.orElseThrow(NotFoundException::new);
    }

    @GET
    @Produces("image/png")
    @PermitAll
    @Path("/{consultantEmail}/imagethumbnail")
    public File getImageThumbnail(@PathParam("consultantEmail") String consultantEmail) {
        var consultant = cachedConsultantService.getConsultants(null, null, List.of(consultantEmail))
                .stream()
                .findFirst()
                .map(Consultant::imageThumbnail);

        return consultant.orElseThrow(NotFoundException::new);
    }
}
