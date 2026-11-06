package com.tiendaonline.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ExternalDTOs {

    // DTO respuesta de servicio-clientes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteResponse {
        private UUID id;
        private String nombre;
        private String email;
    }

    // DTO respuesta de servicio-inventario al consultar producto
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoResponse {
        private UUID id;
        private String nombre;
        private Integer cantidadDisponible;
        private BigDecimal precioUnitario;
    }

    // DTO petición para reservar lote en servicio-inventario
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrearReservaBatchRequest {
        private UUID pedidoId;
        private List<ItemReservaRequest> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemReservaRequest {
        private UUID productoId;
        private Integer cantidad;
    }

    // DTO respuesta de servicio-inventario
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservaItemResponse {
        private UUID reservaId;
        private UUID productoId;
        private Integer cantidad;
        private String estado;
    }
}