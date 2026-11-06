package com.tiendaonline.inventario.exception;

import com.tiendaonline.commons.exception.ResourceNotFoundException;

public class ClienteNotFoundException extends ResourceNotFoundException{
    public ClienteNotFoundException(String message) {
        super(message);
    }
}
