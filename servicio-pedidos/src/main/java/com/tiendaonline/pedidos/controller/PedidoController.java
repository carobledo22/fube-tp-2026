package com.tiendaonline.pedidos.controller;


import com.tiendaonline.pedidos.dto.PedidoDTOs.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.PedidoResponse;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {

        PedidoResponse creado = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorCliente(@PathVariable UUID clienteId) {
        List<PedidoResponse> pedidos = pedidoService.pedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable UUID id){
        PedidoResponse response = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(@PathVariable UUID id){
        PedidoResponse response = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(response);
    }
}
