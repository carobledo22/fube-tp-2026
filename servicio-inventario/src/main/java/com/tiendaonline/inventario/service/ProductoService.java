package com.tiendaonline.inventario.service;

import com.tiendaonline.inventario.dto.ProductoDTOs.StockResponse;

import com.tiendaonline.inventario.dto.ProductoDTOs.CrearProductoRequest;
import com.tiendaonline.inventario.entity.EstadoReserva;
import com.tiendaonline.inventario.entity.Producto;
import com.tiendaonline.inventario.entity.Reserva;
import com.tiendaonline.commons.exception.BusinessException;
import com.tiendaonline.inventario.exception.ProductoNotFoundException;
import com.tiendaonline.inventario.exception.StockInsuficienteException;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public StockResponse crearProducto(CrearProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .cantidadDisponible(request.getCantidadDisponible())
                .precioUnitario(request.getPrecioUnitario())
                .sku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        productoRepository.save(producto);

        return new StockResponse(producto.getId(), producto.getNombre(), producto.getCantidadDisponible(), producto.getPrecioUnitario());
    }

    @Transactional (readOnly = true)
    public StockResponse obtenerPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("No existe el producto con ID número: " + id));
        return new StockResponse(producto.getId(), producto.getNombre(), producto.getCantidadDisponible(), producto.getPrecioUnitario());
    }

}
