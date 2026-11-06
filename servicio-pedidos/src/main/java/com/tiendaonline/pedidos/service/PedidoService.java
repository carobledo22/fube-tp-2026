package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.client.ClienteClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dto.ExternalDTOs.ClienteResponse;
import com.tiendaonline.pedidos.dto.ExternalDTOs.ProductoResponse;
import com.tiendaonline.pedidos.dto.ExternalDTOs.ItemReservaRequest;
import com.tiendaonline.pedidos.dto.ExternalDTOs.CrearReservaBatchRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.PedidoResponse;
import com.tiendaonline.pedidos.dto.PedidoDTOs.ItemResponse;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Item;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.exception.PedidoNotFoundException;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteClient clienteClient;
    private final InventarioClient inventarioClient;

    @Transactional
    public PedidoResponse crearPedido(CrearPedidoRequest request) {
        ClienteResponse clienteResponse = clienteClient.obtenerClientePorId(request.getClienteId());

        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteResponse.getId());
        pedido.setEstado(EstadoPedido.CREADO);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setFechaActualizacion(LocalDateTime.now());

        List<Item> itemsEntity = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.getItems()) {
            ProductoResponse productoResponse = inventarioClient.obtenerProductoPorId(itemRequest.getProductoId());

            BigDecimal subtotal = productoResponse.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemRequest.getCantidad()));

            total = total.add(subtotal);

            Item item = new Item();
            item.setPedido(pedido);
            item.setProductoId(productoResponse.getId());
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(productoResponse.getPrecioUnitario());

            itemsEntity.add(item);
        }

        pedido.setMontoTotal(total);
        pedido.setItems(itemsEntity);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        CrearReservaBatchRequest reservaRequest = CrearReservaBatchRequest.builder()
                .pedidoId(pedidoGuardado.getId())
                .items(itemsEntity.stream()
                        .map(this::mapToItemReservaRequest)
                        .toList())
                .build();

        inventarioClient.reservarStockBatch(reservaRequest);

        pedidoGuardado.setEstado(EstadoPedido.CONFIRMADO);
        pedidoGuardado.setFechaActualizacion(LocalDateTime.now());
        pedidoRepository.save(pedidoGuardado);

        return mapToPedidoResponse(pedidoGuardado);
    }

    private ItemReservaRequest mapToItemReservaRequest(Item item) {
        return ItemReservaRequest.builder()
                .productoId(item.getProductoId())
                .cantidad(item.getCantidad())
                .build();
    }

    @Transactional (readOnly = true)
    public PedidoResponse obtenerPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("No existe el producto con ID número: " + id));
        return new PedidoResponse(id, pedido.getClienteId(), pedido.getEstado(),
                pedido.getMontoTotal(), pedido.getFechaCreacion(), pedido.getFechaActualizacion(),
                pedido.getItems().stream()
                        .map(this::mapToItemResponse)
                        .toList());
    }
    @Transactional(readOnly = true)
    public List<PedidoResponse> pedidosPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToPedidoResponse)
                .toList();
    }

    private PedidoResponse mapToPedidoResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .pedidoId(pedido.getId())
                .clienteId(pedido.getClienteId())
                .estadoPedido(pedido.getEstado())
                .montoTotal(pedido.getMontoTotal())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .items(pedido.getItems().stream().map(this::mapToItemResponse).toList())
                .build();
    }
    private ItemResponse mapToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .build();
    }

    @Transactional
    public PedidoResponse cancelarPedido(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->  new PedidoNotFoundException("No existe el producto con ID: " + id));

        if(pedido.getEstado() == EstadoPedido.CANCELADO)
            throw new IllegalStateException("El pedido ya se encuentra CANCELADO");

        inventarioClient.liberarReservas(pedido.getId());

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setFechaActualizacion(LocalDateTime.now());
        Pedido guardado = pedidoRepository.save(pedido);

        return mapToPedidoResponse(guardado);

    }

}
