package com.vitor.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.vitor.demo.models.TurmaAluno;
import java.util.List;

@Repository
public interface TurmaAlunoRepository extends JpaRepository<TurmaAluno, Long> {
    List<TurmaAluno> findByAlunoId(Long alunoId);
    List<TurmaAluno> findByTurmaId(Long turmaId);
    
    @Query("SELECT ta FROM TurmaAluno ta WHERE ta.aluno.id = :alunoId AND ta.turma.id = :turmaId")
    List<TurmaAluno> findByAlunoIdAndTurmaId(@Param("alunoId") Long alunoId, @Param("turmaId") Long turmaId);
}