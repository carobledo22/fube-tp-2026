package com.tiendaonline.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class ReservaDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrearReservaBatchRequest {

        @NotNull(message = "El ID de pedido es obligatorio")
        private UUID pedidoId;

        @NotEmpty(message = "Debe enviar al menos un producto a reservar")
        @Valid
        private List<ItemReservaRequest> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemReservaRequest {

        @NotNull(message = "El ID de producto es obligatorio")
        private UUID productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad a reservar debe ser al menos 1")
        private Integer cantidad;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservaItemResponse {
        private UUID reservaId;
        private UUID productoId;
        private Integer cantidad;
        private String estado;
    }
}