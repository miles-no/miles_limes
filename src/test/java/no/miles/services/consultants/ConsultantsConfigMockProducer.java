package no.miles.services.consultants;

import io.smallrye.config.SmallRyeConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;

public class ConsultantsConfigMockProducer {

    @Inject
    SmallRyeConfig config;

    @Produces
    @ApplicationScoped
    @io.quarkus.test.Mock
    ConsultantsConfig consultantsConfig() {
        return config.unwrap(SmallRyeConfig.class).getConfigMapping(ConsultantsConfig.class);
    }
}
