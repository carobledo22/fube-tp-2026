package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.Item;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByPedidoId(UUID pedido);
}
