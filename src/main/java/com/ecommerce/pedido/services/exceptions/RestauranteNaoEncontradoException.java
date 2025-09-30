package com.ecommerce.pedido.services.exceptions;

public class RestauranteNaoEncontradoException extends EntidadeNaoEncontradaException{
    public RestauranteNaoEncontradoException(String message){
        super(message);
    }
}
