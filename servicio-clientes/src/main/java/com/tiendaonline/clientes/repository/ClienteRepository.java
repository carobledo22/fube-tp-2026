package com.tiendaonline.clientes.repository;

import com.tiendaonline.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID>  {
    boolean existsByEmail(String email);
}
