package com.ecommerce.pedido.services.exceptions;

public class ValidacaoNegocioException extends RuntimeException {
    public ValidacaoNegocioException(String msg) {
        super(msg);
    }
}