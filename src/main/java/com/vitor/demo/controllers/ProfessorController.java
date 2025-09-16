package com.vitor.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.vitor.demo.services.ProfessorService;
import com.vitor.demo.services.TurmaService;
import com.vitor.demo.models.Professor;
import com.vitor.demo.models.Turma;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/professor")
@Validated
public class ProfessorController {
    
    @Autowired
    private ProfessorService professorService;

    @Autowired
    private TurmaService turmaService;

    @GetMapping
    public ResponseEntity<List<Professor>> findAll() {
        List<Professor> professors = professorService.findAll();
        return ResponseEntity.ok(professors);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Professor> findById(@PathVariable Long id) {
        Professor obj = professorService.findById(id);
        return ResponseEntity.ok(obj);
    }

    // NOVO ENDPOINT: Listar turmas de um professor
    @GetMapping(value = "/{id}/turmas")
    public ResponseEntity<List<Turma>> findTurmasByProfessorId(@PathVariable Long id) {
        List<Turma> turmas = turmaService.findByProfessorId(id);
        return ResponseEntity.ok(turmas);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Professor obj) {
        professorService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Professor obj, @PathVariable Long id) {
        obj.setId(id);
        professorService.update(obj);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}