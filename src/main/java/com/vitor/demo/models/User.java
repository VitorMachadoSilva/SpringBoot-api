package com.vitor.demo.models;

import com.vitor.demo.security.ProfileEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "username", length = 100, nullable = false, unique = true)
    @NotNull(message = "Username é obrigatório")
    @NotEmpty(message = "Username não pode estar vazio")
    @Size(min = 3, max = 100)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    @NotNull(message = "Password é obrigatório")
    @NotEmpty(message = "Password não pode estar vazio")
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @NotNull(message = "Email é obrigatório")
    @NotEmpty(message = "Email não pode estar vazio")
    @Email(message = "Email deve ser válido")
    private String email;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profiles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "profile")
    @Enumerated(EnumType.STRING)
    private Set<ProfileEnum> profiles = new HashSet<>();

    // Construtor para facilitar criação de usuários
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.ativo = true;
        this.profiles.add(ProfileEnum.USER); // Perfil padrão
    }
    
    // Método para adicionar perfil
    public void addProfile(ProfileEnum profile) {
        this.profiles.add(profile);
    }
}