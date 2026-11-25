package com.tiendaonline.clientes.config;

import com.statsig.sdk.Statsig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatsigInitializer {

    @Value("${statsig.server-secret-key:}")
    private String serverSecretKey;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        if (serverSecretKey == null || serverSecretKey.isBlank()) {
            log.warn("Statsig no inicializado: no se configuro statsig.server-secret-key");
            return;
        }
        try {
            Statsig.initializeAsync(serverSecretKey).get();
            initialized = true;
            log.info("Statsig inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar Statsig", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (initialized) {
            try {
                Statsig.shutdown();
            } catch (Exception e) {
                log.warn("Error al apagar Statsig: {}", e.getMessage());
            }
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
