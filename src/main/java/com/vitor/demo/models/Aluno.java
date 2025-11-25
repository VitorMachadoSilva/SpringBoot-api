package com.vitor.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aluno")
public class Aluno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    @NotNull(message = "Nome é obrigatório")
    @NotEmpty(message = "Nome não pode estar vazio")
    @Size(min = 2, max = 100)
    private String nome;

    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    @NotNull
    @NotEmpty
    @Size(min = 11, max = 11)
    private String cpf;
}