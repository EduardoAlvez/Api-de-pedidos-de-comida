package com.ecommerce.pedido.models.enums;

public enum Role {
    // Cada constante com texto correspondente.
    DONO_RESTAURANTE("ROLE_RESTAURANTE"),
    CLIENTE("ROLE_CLIENTE"),
    GARCOM("ROLE_GARCOM");

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