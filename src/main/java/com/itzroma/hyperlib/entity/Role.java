package com.itzroma.hyperlib.entity;

import lombok.Getter;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    LIBRARIAN("ROLE_LIBRARIAN"),
    USER("ROLE_USER");

    @Getter
    private final String name;

    Role(String name) {
        this.name = name;
    }
}

