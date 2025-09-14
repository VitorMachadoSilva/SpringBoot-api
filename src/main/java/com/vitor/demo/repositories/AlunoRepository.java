package com.vitor.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vitor.demo.models.Aluno;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    Optional<Aluno> findByCpf(String cpf);
    
    @Query("SELECT a FROM Aluno a WHERE a.nome = :nome")
    Optional<Aluno> findByNome(@Param("nome") String nome);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByNome(String nome);
}