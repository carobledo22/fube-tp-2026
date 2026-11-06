package com.tiendaonline.clientes.exception;

import com.tiendaonline.commons.exception.ResourceNotFoundException;
public class ClienteNotFoundException extends ResourceNotFoundException {
    public ClienteNotFoundException(String mensaje) {
        super(mensaje);
    }
}
