package com.tiendaonline.pedidos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.commons.exception.BusinessException;
import com.tiendaonline.commons.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String mensajeError = extraerMensaje(response);

        switch (response.status()) {
            case 404:
                return new ResourceNotFoundException(
                        mensajeError != null ? mensajeError : "El recurso solicitado en el servicio externo no existe"
                );
            case 400:
                if (mensajeError != null && mensajeError.toLowerCase().contains("stock")) {
                    return new BusinessException(mensajeError);
                }
                return new IllegalArgumentException(
                        mensajeError != null ? mensajeError : "Petición inválida enviada al servicio externo"
                );
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    private String extraerMensaje(Response response) {
        if (response.body() == null) {
            return null;
        }
        try {
            // Leer el cuerpo completo de la respuesta de manera segura como String
            String bodyStr = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
            if (bodyStr.trim().isEmpty()) {
                return null;
            }
            Map<?, ?> map = objectMapper.readValue(bodyStr, Map.class);
            if (map != null && map.containsKey("message")) {
                return map.get("message").toString();
            }
        } catch (Exception e) {
            log.warn("No se pudo parsear el cuerpo del error devuelto por Feign: {}", e.getMessage());
        }
        return null;
    }
}