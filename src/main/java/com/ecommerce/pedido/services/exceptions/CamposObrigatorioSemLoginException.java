package com.ecommerce.pedido.services.exceptions;

public class CamposObrigatorioSemLoginException extends ValidacaoNegocioException{
    public CamposObrigatorioSemLoginException(String message){

        super(message);
    }
}
