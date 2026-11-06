package com.tiendaonline.pedidos.exception;

import com.tiendaonline.commons.exception.ResourceNotFoundException;

public class PedidoNotFoundException extends ResourceNotFoundException {
    public PedidoNotFoundException(String message) {

        super(message);
    }
}
