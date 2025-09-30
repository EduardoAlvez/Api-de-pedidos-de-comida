package com.ecommerce.pedido.services.exceptions;

public class CnpjEmUsoExcption extends ValidacaoNegocioException {
    public CnpjEmUsoExcption(String s) {
        super(s);
    }
}
