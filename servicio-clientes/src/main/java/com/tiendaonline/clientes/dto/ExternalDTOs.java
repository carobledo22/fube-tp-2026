package com.tiendaonline.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ExternalDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PedidoResponse {
        private UUID pedidoId;
        private UUID clienteId;
        private String estadoPedido;
        private BigDecimal montoTotal;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
        private List<ItemResponse> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResponse {
        private UUID id;
        private UUID productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
