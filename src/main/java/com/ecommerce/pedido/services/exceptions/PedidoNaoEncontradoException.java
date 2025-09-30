package com.ecommerce.pedido.services.exceptions;

public class PedidoNaoEncontradoException extends EntidadeNaoEncontradaException {
    public PedidoNaoEncontradoException(String msg) {
        super(msg);
    }
}