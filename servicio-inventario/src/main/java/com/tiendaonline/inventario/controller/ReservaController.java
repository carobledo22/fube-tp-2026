package com.tiendaonline.inventario.controller;

import com.tiendaonline.inventario.dto.ReservaDTOs.*;
import com.tiendaonline.inventario.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping("/batch")
    public ResponseEntity<List<ReservaItemResponse>> reservarStock(@Valid @RequestBody CrearReservaBatchRequest request) {
        return ResponseEntity.ok(reservaService.reservarStockBatch(request));
    }

    @PutMapping("/liberar/{pedidoId}")
    public ResponseEntity<Void> liberarReservas(@PathVariable UUID pedidoId) {
        reservaService.liberarReservasPorPedido(pedidoId);
        return ResponseEntity.noContent().build();
    }
}