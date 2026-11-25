package com.tiendaonline.pedidos.controller;


import com.tiendaonline.pedidos.dto.PedidoDTOs.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.PedidoResponse;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    private final JobLauncher jobLauncher;
    private final Job expirarPedidosJob;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {

        PedidoResponse creado = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    @PostMapping("/batch/expirar")
    public ResponseEntity<Map<String, String>> ejecutarBatchExpiracion() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("ejecucionManual", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(expirarPedidosJob, params);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Job de expiración ejecutado",
                    "estado", execution.getStatus().toString(),
                    "jobExecutionId", String.valueOf(execution.getId())
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error ejecutando el batch: " + e.getMessage()
            ));
        }
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
    @PutMapping("{id}/confirmar")
    public ResponseEntity<PedidoResponse> confirmarPedido(@PathVariable UUID id){
        PedidoResponse response = pedidoService.confirmarPedido(id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(@PathVariable UUID id){
        PedidoResponse response = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(response);
    }

}
