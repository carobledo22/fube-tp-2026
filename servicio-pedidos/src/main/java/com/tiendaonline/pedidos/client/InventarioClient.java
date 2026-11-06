package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.config.FeignConfig;
import com.tiendaonline.pedidos.dto.ExternalDTOs.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "servicio-inventario",
        url = "${app.feign.inventario-url:http://localhost:8082}",
        configuration = FeignConfig.class
)
public interface InventarioClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") UUID id);

    @PostMapping("/api/v1/reservas/batch")
    List<ReservaItemResponse> reservarStockBatch(@RequestBody CrearReservaBatchRequest request);

    @PutMapping("/api/v1/reservas/liberar/{pedidoId}")
    void liberarReservas(@PathVariable("pedidoId") UUID pedidoId);
}