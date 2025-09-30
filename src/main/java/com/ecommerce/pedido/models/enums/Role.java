package com.ecommerce.pedido.models.enums;

public enum Role {
    // Cada constante com texto correspondente.
    DONO_RESTAURANTE("ROLE_RESTAURANTE"),
    CLIENTE("ROLE_CLIENTE");

    private final String role;

    // Construtor
    Role(String role) {
        this.role = role;
    }

    // metodos
    public String getRole() {
        return role;
    }
}