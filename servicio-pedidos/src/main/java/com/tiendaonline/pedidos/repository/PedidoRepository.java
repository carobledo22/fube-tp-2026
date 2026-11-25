package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Pedido;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
public interface PedidoRepository extends JpaRepository<Pedido, UUID>{
    List<Pedido> findByClienteId(UUID clienteId);
    Page<Pedido> findByEstadoAndFechaCreacionBefore(EstadoPedido estado, LocalDateTime fechaCreacion, Pageable pageable);
}
