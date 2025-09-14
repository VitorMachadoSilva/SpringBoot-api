package com.vitor.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.vitor.demo.services.TurmaAlunoService;
import com.vitor.demo.models.TurmaAluno;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/turmaaluno")
@Validated
public class TurmaAlunoController {
    
    @Autowired
    private TurmaAlunoService turmaAlunoService;
    
    @GetMapping
    public ResponseEntity<List<TurmaAluno>> findAll() {
        List<TurmaAluno> matriculas = turmaAlunoService.findAll();
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TurmaAluno> findById(@PathVariable Long id) {
        TurmaAluno obj = turmaAlunoService.findById(id);
        return ResponseEntity.ok(obj);
    }

    @GetMapping(value = "/aluno/{alunoId}")
    public ResponseEntity<List<TurmaAluno>> findByAlunoId(@PathVariable Long alunoId) {
        List<TurmaAluno> matriculas = turmaAlunoService.findByAlunoId(alunoId);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping(value = "/turma/{turmaId}")
    public ResponseEntity<List<TurmaAluno>> findByTurmaId(@PathVariable Long turmaId) {
        List<TurmaAluno> matriculas = turmaAlunoService.findByTurmaId(turmaId);
        return ResponseEntity.ok(matriculas);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody TurmaAluno obj) {
        turmaAlunoService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody TurmaAluno obj, @PathVariable Long id) {
        obj.setId(id);
        turmaAlunoService.update(obj);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        turmaAlunoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}