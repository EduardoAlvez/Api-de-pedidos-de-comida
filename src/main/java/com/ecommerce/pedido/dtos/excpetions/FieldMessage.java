package com.ecommerce.pedido.dtos.excpetions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class FieldMessage implements Serializable {
    private String fieldName;
    private String message;
}