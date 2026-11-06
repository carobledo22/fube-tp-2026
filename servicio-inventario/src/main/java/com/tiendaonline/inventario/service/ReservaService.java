package com.tiendaonline.inventario.service;

import com.tiendaonline.commons.exception.ResourceNotFoundException;
import com.tiendaonline.inventario.exception.ProductoNotFoundException;
import com.tiendaonline.inventario.exception.ReservasNotFoundException;
import com.tiendaonline.inventario.exception.StockInsuficienteException;
import com.tiendaonline.inventario.dto.ReservaDTOs.*;
import com.tiendaonline.inventario.entity.EstadoReserva;
import com.tiendaonline.inventario.entity.Producto;
import com.tiendaonline.inventario.entity.Reserva;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public List<ReservaItemResponse> reservarStockBatch(CrearReservaBatchRequest request) {
        List<ReservaItemResponse> respuestas = new ArrayList<>();

        for (ItemReservaRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException("No existe el producto con ID: " + item.getProductoId()));

            if (item.getCantidad() > producto.getCantidadDisponible()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setCantidadDisponible(producto.getCantidadDisponible() - item.getCantidad());
            productoRepository.save(producto);

            Reserva reserva = Reserva.builder()
                    .productoId(producto.getId())
                    .pedidoId(request.getPedidoId())
                    .cantidad(item.getCantidad())
                    .estado(EstadoReserva.ACTIVA)
                    .build();

            Reserva guardada = reservaRepository.save(reserva);

            respuestas.add(ReservaItemResponse.builder()
                    .reservaId(guardada.getId())
                    .productoId(guardada.getProductoId())
                    .cantidad(guardada.getCantidad())
                    .estado(guardada.getEstado().name())
                    .build());
        }

        return respuestas;
    }

    @Transactional
    public void liberarReservasPorPedido(UUID pedidoId) {
        List<Reserva> reservas = reservaRepository.findByPedidoId(pedidoId);

        if (reservas.isEmpty()) {
            throw new ReservasNotFoundException("No se encontraron reservas asociadas al pedido: " + pedidoId);
        }

        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                // Devolver stock al producto
                Producto producto = productoRepository.findById(reserva.getProductoId())
                        .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + reserva.getProductoId()));

                producto.setCantidadDisponible(producto.getCantidadDisponible() + reserva.getCantidad());
                productoRepository.save(producto);

                reserva.setEstado(EstadoReserva.LIBERADA);
                reservaRepository.save(reserva);
            }
        }
    }
}