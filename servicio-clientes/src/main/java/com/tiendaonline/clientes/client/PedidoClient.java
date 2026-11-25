package com.tiendaonline.clientes.client;

import com.tiendaonline.clientes.dto.ExternalDTOs.PedidoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "servicio-pedidos",
        url = "${app.feign.pedidos-url:http://localhost:8083}"
)
public interface PedidoClient {

    @GetMapping("/api/v1/pedidos/cliente/{clienteId}")
    List<PedidoResponse> obtenerPedidosPorCliente(@PathVariable("clienteId") UUID clienteId);
}
