package com.vitor.demo.security;

import lombok.Getter;

@Getter
public enum ProfileEnum {
    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER");
    
    private final int code;
    private final String description;
    
    ProfileEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static ProfileEnum toEnum(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (ProfileEnum profile : ProfileEnum.values()) {
            if (code.equals(profile.getCode())) {
                return profile;
            }
        }
        
        throw new IllegalArgumentException("Código de perfil inválido: " + code);
    }
}