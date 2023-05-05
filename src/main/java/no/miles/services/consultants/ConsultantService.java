package no.miles.services.consultants;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import no.miles.services.consultants.domain.ConsultantCollection;
import no.miles.services.consultants.domain.Office;
import no.miles.services.consultants.domain.Role;
import no.miles.services.consultants.integrations.cvpartner.CVPartnerImageRepository;
import no.miles.services.consultants.integrations.cvpartner.CVPartnerRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConsultantService {
    private final CVPartnerRepository cvPartnerRepository;
    private final CVPartnerImageRepository cvPartnerImageRepository;

    public ConsultantService(CVPartnerRepository cvPartnerRepository, CVPartnerImageRepository cvPartnerImageRepository) {
        this.cvPartnerRepository = cvPartnerRepository;
        this.cvPartnerImageRepository = cvPartnerImageRepository;
    }

    public ConsultantCollection getConsultants() {
        var consultants = getOffices().stream()
                .flatMap(office -> cvPartnerRepository.searchConsultants(office, null).stream())
                .toList();

        Multi.createFrom().items(consultants.stream()).onItem()
                .transformToUniAndMerge(c -> {
                    if (c.imageUrl() == null) {
                        return Uni.createFrom().nullItem();
                    }
                    return cvPartnerImageRepository.getImage(c)
                            .onFailure().recoverWithNull()
                            .map(c::image)
                            .chain(cvPartnerImageRepository::getImageThumbnail)
                            .onFailure().recoverWithNull()
                            .map(c::imageThumbnail);
                })
                .collect().asList().await().indefinitely();

        var consultantsWithRoles = Role.values.stream()
                .flatMap(role -> cvPartnerRepository.searchConsultants(null, role).stream())
                .toList();

        return ConsultantCollection.from(consultants).merge(consultantsWithRoles);
    }

    public List<Office> getOffices() {
        return cvPartnerRepository.getOffices();
    }
}
