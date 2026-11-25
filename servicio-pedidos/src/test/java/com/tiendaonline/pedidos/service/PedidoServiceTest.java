package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.client.ClienteClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dto.PedidoDTOs.PedidoResponse;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.exception.PedidoNotFoundException;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    //Test Pedido Existe
    @Test
    void obtenerPorId_cuandoExiste_devuelvePedidoResponse(){

        //Arrange
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Pedido pedido = new Pedido(id, clienteId, EstadoPedido.CREADO, new BigDecimal("100000"), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        //Act
        PedidoResponse response = pedidoService.obtenerPorId(id);

        //Assert
        assertEquals(id, response.getPedidoId());
        assertEquals(clienteId, response.getClienteId());
        assertEquals(EstadoPedido.CREADO, response.getEstadoPedido());
        assertEquals(new BigDecimal("100000"), response.getMontoTotal());
    }

    //Test Pedido no existe
    @Test
    void obtenerPorId_cuandoNoExiste_lanzaPedidoNotFoundException(){

        //Arrange
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        //Act + Assert
        PedidoNotFoundException ex = assertThrows(PedidoNotFoundException.class, () -> pedidoService.obtenerPorId(id));
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    //Cancelar Pedido existente
    @Test
    void cancelarPedido_cuandoExiste_liberaReservasYCambiaEstadoACancelado(){

        //Arrange
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Pedido pedido = new Pedido(id, clienteId, EstadoPedido.CREADO, new BigDecimal("100000"),
                LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        //Act
        PedidoResponse response = pedidoService.cancelarPedido(id);

        //Assert
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        assertEquals(EstadoPedido.CANCELADO, response.getEstadoPedido());
        verify(inventarioClient).liberarReservas(id);
        verify(pedidoRepository).save(pedido);
    }
}
