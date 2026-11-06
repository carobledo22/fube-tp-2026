package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.config.FeignConfig;
import com.tiendaonline.pedidos.dto.ExternalDTOs.ClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "servicio-clientes",
        url = "${app.feign.clientes-url:http://localhost:8081}",
        configuration = FeignConfig.class
)
public interface ClienteClient {

    @GetMapping("/api/v1/clientes/{id}")
    ClienteResponse obtenerClientePorId(@PathVariable("id") UUID id);
}