package com.vitor.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.vitor.demo.services.DisciplinaService;
import com.vitor.demo.services.TurmaService;
import com.vitor.demo.models.Disciplina;
import com.vitor.demo.models.Turma;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/disciplina")
@Validated
public class DisciplinaController {
    
    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private TurmaService turmaService;

    @GetMapping
    public ResponseEntity<List<Disciplina>> findAll() {
        List<Disciplina> discplinas = disciplinaService.findAll();
        return ResponseEntity.ok(discplinas);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Disciplina> findById(@PathVariable Long id) {
        Disciplina obj = disciplinaService.findById(id);
        return ResponseEntity.ok(obj);
    }

    // NOVO ENDPOINT: Listar turmas de uma disciplina
    @GetMapping(value = "/{id}/turmas")
    public ResponseEntity<List<Turma>> findTurmasByDisciplinaId(@PathVariable Long id) {
        List<Turma> turmas = turmaService.findByDisciplinaId(id);
        return ResponseEntity.ok(turmas);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Disciplina obj) {
        disciplinaService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Disciplina obj, @PathVariable Long id) {
        obj.setId(id);
        disciplinaService.update(obj);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplinaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}