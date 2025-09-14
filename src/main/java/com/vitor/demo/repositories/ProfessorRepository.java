package com.vitor.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vitor.demo.models.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}