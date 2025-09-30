package com.ecommerce.pedido.services.exceptions;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String msg) {
        super(msg);
    }
}