package com.vitor.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.vitor.demo.services.TurmaService;
import com.vitor.demo.services.NotaService;
import com.vitor.demo.services.TurmaAlunoService;
import com.vitor.demo.models.Turma;
import com.vitor.demo.models.Nota;
import com.vitor.demo.models.TurmaAluno;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/turma")
@Validated
public class TurmaController {
    
    @Autowired
    private TurmaService turmaService;

    @Autowired
    private NotaService notaService;

    @Autowired
    private TurmaAlunoService turmaAlunoService;

    @GetMapping
    public ResponseEntity<List<Turma>> findAll() {
        List<Turma> turmas = turmaService.findAll();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping(value = "/{id}/notas")
    public ResponseEntity<List<Nota>> findNotasByTurmaId(@PathVariable Long id) {
        List<Nota> notas = notaService.findByTurmaId(id);
        return ResponseEntity.ok(notas);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Turma> findById(@PathVariable Long id) {
        Turma obj = turmaService.findById(id);
        return ResponseEntity.ok(obj);
    }

    @GetMapping(value = "/disciplina/{disciplinaId}")
    public ResponseEntity<List<Turma>> findByDisciplinaId(@PathVariable Long disciplinaId) {
        List<Turma> turmas = turmaService.findByDisciplinaId(disciplinaId);
        return ResponseEntity.ok(turmas);
    }

    @GetMapping(value = "/professor/{professorId}")
    public ResponseEntity<List<Turma>> findByProfessorId(@PathVariable Long professorId) {
        List<Turma> turmas = turmaService.findByProfessorId(professorId);
        return ResponseEntity.ok(turmas);
    }

    // NOVO ENDPOINT: Listar notas de uma turma
    // @GetMapping(value = "/{id}/notas")
    // public ResponseEntity<List<Nota>> findNotasByTurmaId(@PathVariable Long id) {
    //     List<Nota> notas = notaService.findByTurmaId(id);
    //     return ResponseEntity.ok(notas);
    // }

    // NOVO ENDPOINT: Listar alunos de uma turma
    @GetMapping(value = "/{id}/alunos")
    public ResponseEntity<List<TurmaAluno>> findAlunosByTurmaId(@PathVariable Long id) {
        List<TurmaAluno> matriculas = turmaAlunoService.findByTurmaId(id);
        return ResponseEntity.ok(matriculas);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Turma obj) {
        turmaService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Turma obj, @PathVariable Long id) {
        obj.setId(id);
        turmaService.update(obj);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        turmaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}