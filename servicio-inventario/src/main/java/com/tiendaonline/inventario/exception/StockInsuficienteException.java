package com.tiendaonline.inventario.exception;

import com.tiendaonline.commons.exception.BusinessException;

public class StockInsuficienteException extends BusinessException {
    public StockInsuficienteException(String message) {
        super(message);
    }
}