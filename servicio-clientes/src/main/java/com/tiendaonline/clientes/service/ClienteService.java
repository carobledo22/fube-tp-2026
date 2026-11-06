package com.tiendaonline.clientes.service;

import com.tiendaonline.clientes.dto.ClienteDTOs;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteRequest;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteResponse;
import com.tiendaonline.clientes.entity.Cliente;
import com.tiendaonline.clientes.exception.ClienteNotFoundException;
import com.tiendaonline.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    @Transactional
    public ClienteResponse crearCliente(ClienteRequest request){
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .fechaAlta(LocalDateTime.now())
                .build();
        clienteRepository.save(cliente);
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getEmail(), cliente.getFechaAlta());
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(UUID id){
        Cliente cliente = clienteRepository.findById(id).
                orElseThrow(() -> new ClienteNotFoundException("Cliente con id " + id + " no encontrado"));
        return new ClienteResponse(id, cliente.getNombre(), cliente.getEmail(), cliente.getFechaAlta());

    }
}
