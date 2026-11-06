package com.tiendaonline.clientes.controller;

import com.tiendaonline.clientes.dto.ClienteDTOs;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteResponse;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteRequest;
import com.tiendaonline.clientes.repository.ClienteRepository;
import com.tiendaonline.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable UUID id){
        ClienteResponse response = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(response);
    }
}
