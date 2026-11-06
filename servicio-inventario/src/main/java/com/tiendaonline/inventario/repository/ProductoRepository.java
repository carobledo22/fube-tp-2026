package com.tiendaonline.inventario.repository;

import com.tiendaonline.inventario.entity.Producto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    Optional<Producto> findByNombre(String nombre);
}
