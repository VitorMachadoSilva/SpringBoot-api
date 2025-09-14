package com.vitor.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vitor.demo.models.TurmaAluno;
import java.util.List;

@Repository
public interface TurmaAlunoRepository extends JpaRepository<TurmaAluno, Long> {
    List<TurmaAluno> findByAlunoId(Long alunoId);
    List<TurmaAluno> findByTurmaId(Long turmaId);
}