package com.tiendaonline.clientes.service;

import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigUser;
import com.tiendaonline.clientes.config.StatsigInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PiiMaskingService {

    private static final String GATE_NAME = "mask-pii";

    private final StatsigInitializer statsigInitializer;

    public boolean isMaskPiiEnabled(UUID clienteId) {
        if (!statsigInitializer.isInitialized()) {
            return false;
        }
        try {
            StatsigUser user = new StatsigUser(clienteId.toString());
            return Statsig.checkGateSync(user, GATE_NAME);
        } catch (Exception e) {
            log.warn("Error al evaluar gate {}: {}", GATE_NAME, e.getMessage());
            return false;
        }
    }

    public String maskName(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return nombre;
        }
        String[] partes = nombre.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            if (i > 0) sb.append(" ");
            String parte = partes[i];
            if (parte.isEmpty()) continue;
            sb.append(parte.charAt(0)).append("***");
        }
        return sb.toString();
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int at = email.indexOf('@');
        String dominio = email.substring(at + 1);
        int dot = dominio.lastIndexOf('.');
        if (dot < 0) {
            return "****@****";
        }
        String extension = dominio.substring(dot);
        return "****@****" + extension;
    }
}
