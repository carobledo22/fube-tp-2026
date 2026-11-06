package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Item;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PedidoDTOs {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrearPedidoRequest {
        @NotNull(message = "El ID cliente es obligatorio")
        private UUID clienteId;

        @NotEmpty(message = "El pedido debe contener al menos un (1) ítem")
        @Valid
        private List<ItemPedidoRequest> items;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemPedidoRequest {
        @NotNull(message = "El ID producto es obligatorio")
        private UUID productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1
                , message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
    }
    @Data
    @AllArgsConstructor
    public static class ListaPedidosResponse {
        private List<PedidoResponse> pedidos;
    }
    @Data
    @AllArgsConstructor
    @Builder
    public static class PedidoResponse {
        private UUID pedidoId;
        private UUID clienteId;
        private EstadoPedido estadoPedido;
        private BigDecimal montoTotal;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
        private List<ItemResponse> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemResponse {
        private UUID id;
        private UUID productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }

}
