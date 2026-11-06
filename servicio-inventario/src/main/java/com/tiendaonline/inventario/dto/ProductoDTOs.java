package com.tiendaonline.inventario.dto;


import com.tiendaonline.inventario.entity.EstadoReserva;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductoDTOs {

    @Data
    @AllArgsConstructor
    public static class StockResponse {
        private UUID id;
        private String nombre;
        private Integer cantidadDisponible;
        private BigDecimal precioUnitario;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearProductoRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidadDisponible;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio debe ser mayor o igual a 0")
        private BigDecimal precioUnitario;
    }

}
