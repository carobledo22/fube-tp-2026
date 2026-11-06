package com.tiendaonline.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClienteDTOs
{
    @Data
    @AllArgsConstructor
    public static class ClienteResponse {
        private UUID id;
        private String nombre;
        private String email;
        private LocalDateTime fechaAlta;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClienteRequest {
        @NotBlank
        private String nombre;
        @NotBlank
        @Email
        private String email;
    }
}
