package no.miles.services.consultants;

import no.miles.services.consultants.vo.Consultant;
import no.miles.services.consultants.vo.ListWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/consultants")
public class ConsultantsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListWrapper<Consultant> getConsultants(){
        return null;
    }
}
