package com.ecommerce.pedido.services.exceptions;

public class ProdutoNaoEncontradoException extends EntidadeNaoEncontradaException {
    public ProdutoNaoEncontradoException(String message) {
        super(message);
    }

}
