package com.vitor.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.vitor.demo.services.NotaService;
import com.vitor.demo.models.Nota;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/nota")
@Validated
public class NotaController {
    
    @Autowired
    private NotaService notaService;
    
    @GetMapping
    public ResponseEntity<List<Nota>> findAll() {
        List<Nota> notas = notaService.findAll();
        return ResponseEntity.ok(notas);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Nota> findById(@PathVariable Long id) {
        Nota obj = notaService.findById(id);
        return ResponseEntity.ok(obj);
    }

    @GetMapping(value = "/aluno/{alunoId}")
    public ResponseEntity<List<Nota>> findByAlunoId(@PathVariable Long alunoId) {
        List<Nota> notas = notaService.findByAlunoId(alunoId);
        return ResponseEntity.ok(notas);
    }

    @GetMapping(value = "/turma/{turmaId}")
    public ResponseEntity<List<Nota>> findByTurmaId(@PathVariable Long turmaId) {
        List<Nota> notas = notaService.findByTurmaId(turmaId);
        return ResponseEntity.ok(notas);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Nota obj) {
        notaService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Nota obj, @PathVariable Long id) {
        obj.setId(id);
        notaService.update(obj);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}