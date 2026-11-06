package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.Pedido;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
public interface PedidoRepository extends JpaRepository<Pedido, UUID>{
    List<Pedido> findByClienteId(UUID clienteId);
}
