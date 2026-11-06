package com.tiendaonline.inventario.exception;

import com.tiendaonline.commons.exception.ResourceNotFoundException;

public class ProductoNotFoundException extends ResourceNotFoundException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}
