package com.tiendaonline.inventario.repository;

import com.tiendaonline.inventario.entity.Producto;
import com.tiendaonline.inventario.entity.Reserva;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    List<Reserva> findByPedidoId(UUID pedidoId);
}
