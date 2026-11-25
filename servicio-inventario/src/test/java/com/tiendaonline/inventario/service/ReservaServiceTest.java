package com.tiendaonline.inventario.service;

import com.tiendaonline.inventario.dto.ReservaDTOs;
import com.tiendaonline.inventario.entity.EstadoReserva;
import com.tiendaonline.inventario.entity.Producto;
import com.tiendaonline.inventario.entity.Reserva;
import com.tiendaonline.inventario.exception.ProductoNotFoundException;
import com.tiendaonline.inventario.exception.ReservasNotFoundException;
import com.tiendaonline.inventario.exception.StockInsuficienteException;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks

    private ReservaService reservaService;


    @Test

    void reservarStockBatch_cuandoHayStock_descuentaStockYCreaReservaActiva(){
        //Arrange
        UUID pedidoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        Producto producto = new Producto(productoId, "LN100", "Notebook lenovo", 10, new BigDecimal("1500000"));

        ReservaDTOs.ItemReservaRequest item = new ReservaDTOs.ItemReservaRequest(productoId, 3);
        ReservaDTOs.CrearReservaBatchRequest request = new ReservaDTOs.CrearReservaBatchRequest(pedidoId, List.of(item));

        Reserva reservaGuardada = new Reserva(reservaId, productoId, pedidoId, 3, EstadoReserva.ACTIVA);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        //Act
        List<ReservaDTOs.ReservaItemResponse> respuestas = reservaService.reservarStockBatch(request);

        //Assert
        assertEquals(1, respuestas.size());
        assertEquals(reservaId, respuestas.get(0).getReservaId());
        assertEquals(productoId, respuestas.get(0).getProductoId());
        assertEquals(3, respuestas.get(0).getCantidad());
        assertEquals("ACTIVA", respuestas.get(0).getEstado());

        assertEquals(7, producto.getCantidadDisponible());

        verify(productoRepository).save(producto);
        verify(reservaRepository).save(any(Reserva.class));

    }

    @Test

    void reservarStockBatch_cuandoProductoNoExiste_lanzaProductoNotFoundException(){

        //Arrange
        UUID pedidoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        ReservaDTOs.ItemReservaRequest item = new ReservaDTOs.ItemReservaRequest(productoId, 5);

        ReservaDTOs.CrearReservaBatchRequest request = new ReservaDTOs.CrearReservaBatchRequest(pedidoId, List.of(item));

        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        //Act + Assert
        ProductoNotFoundException ex = assertThrows(ProductoNotFoundException.class, () -> reservaService.reservarStockBatch(request));
        assertTrue(ex.getMessage().contains(productoId.toString()));
    }

    @Test

    void reservarStockBatch_cuandoStockInsuficiente_lanzaStockInsuficienteException(){

        //Arrange
        UUID pedidoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        Producto producto = new Producto(productoId, "LN100", "Notebook lenovo", 5, new BigDecimal("1500000"));
        ReservaDTOs.ItemReservaRequest item = new ReservaDTOs.ItemReservaRequest(productoId, 10);
        ReservaDTOs.CrearReservaBatchRequest request = new ReservaDTOs.CrearReservaBatchRequest(pedidoId, List.of(item));

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        //Act + Assert
        StockInsuficienteException ex = assertThrows(StockInsuficienteException.class, () -> reservaService.reservarStockBatch(request));
        assertTrue(ex.getMessage().contains(producto.getNombre()));
    }


    @Test

    void  liberarReservasPorPedido_cuandoHayReservasActivas_devuelveStockYCambiaALiberada(){

        //Arrange
        UUID pedidoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        Producto producto = new Producto(productoId, "LN100", "Notebook lenovo", 5, new BigDecimal("1500000"));
        Reserva reserva = new Reserva(reservaId, productoId, pedidoId, 4, EstadoReserva.ACTIVA);

        when(reservaRepository.findByPedidoId(pedidoId)).thenReturn(List.of(reserva));
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        //Act
        reservaService.liberarReservasPorPedido(pedidoId);

        //Assert
        assertEquals(9, producto.getCantidadDisponible());
        assertEquals(EstadoReserva.LIBERADA, reserva.getEstado());
        verify(productoRepository).save(producto);
        verify(reservaRepository).save(reserva);
    }

    @Test

    void liberarReservasPorPedido_cuandoNoHayReservas_lanzaReservasNotFoundException(){

        //Arrange
        UUID pedidoId = UUID.randomUUID();
        ReservaDTOs.CrearReservaBatchRequest request = new ReservaDTOs.CrearReservaBatchRequest(pedidoId, List.of());
        when(reservaRepository.findByPedidoId(pedidoId)).thenReturn(List.of());

        //Act + Assert

        ReservasNotFoundException ex = assertThrows(ReservasNotFoundException.class, () -> reservaService.liberarReservasPorPedido(pedidoId));
        assertTrue(ex.getMessage().contains(pedidoId.toString()));
    }


}
