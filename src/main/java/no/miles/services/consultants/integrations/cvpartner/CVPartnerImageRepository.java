package no.miles.services.consultants.integrations.cvpartner;


import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import no.miles.services.consultants.domain.Consultant;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;

@ApplicationScoped
public class CVPartnerImageRepository {

    @SneakyThrows
    public Uni<File> getImage(Consultant consultant) {
        var client = RestClientBuilder.newBuilder()
                .baseUri(consultant.imageUrl())
                .build(CVPartnerImageClient.class);

        return client.getImage();
    }

    public Uni<File> getImageThumbnail(Consultant consultant) {
        var client = RestClientBuilder.newBuilder()
                .baseUri(consultant.imageUrlThumbnail())
                .build(CVPartnerImageClient.class);

        return client.getImage();
    }
}
