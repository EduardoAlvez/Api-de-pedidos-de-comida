package com.ecommerce.pedido.services.exceptions.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class StandardError implements Serializable {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}