package com.vitor.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @NotNull(message = "Username é obrigatório")
    @NotEmpty(message = "Username não pode estar vazio")
    @Size(min = 3, max = 100)
    private String username;

    @NotNull(message = "Email é obrigatório")
    @NotEmpty(message = "Email não pode estar vazio")
    @Email(message = "Email deve ser válido")
    private String email;

    private String password; // Opcional na atualização
}