package com.ecommerce.pedido.services.exceptions;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException{
    public UsuarioNaoEncontradoException(String message){
        super(message);
    }
}
