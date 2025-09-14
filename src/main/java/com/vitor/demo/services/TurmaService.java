package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.TurmaRepository;
import com.vitor.demo.models.Disciplina;
import com.vitor.demo.models.Professor;
import com.vitor.demo.models.Turma;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private ProfessorService professorService;

    public Turma findById(Long id) {
        Optional<Turma> turma = turmaRepository.findById(id);
        return turma.orElseThrow(() -> new RuntimeException("Turma não encontrada! Id: " + id));
    }

    public List<Turma> findByDisciplinaId(Long disciplinaId) {
        return turmaRepository.findByDisciplinaId(disciplinaId);
    }

    public List<Turma> findByProfessorId(Long professorId) {
        return turmaRepository.findByProfessorId(professorId);
    }

    @Transactional
    public Turma create(Turma obj) {
        obj.setId(null);
        
        // Verificar se disciplina e professor existem
        disciplinaService.findById(obj.getDisciplina().getId());
        professorService.findById(obj.getProfessor().getId());
        
        return turmaRepository.save(obj);
    }

   @Transactional
    public Turma update(Turma obj) {
        Turma newObj = findById(obj.getId());
        
        // Atualiza apenas os campos básicos
        newObj.setAno(obj.getAno());
        newObj.setPeriodo(obj.getPeriodo());
        
        // Atualiza disciplina apenas se for fornecida no JSON
        if (obj.getDisciplina() != null && obj.getDisciplina().getId() != null) {
            Disciplina disciplina = disciplinaService.findById(obj.getDisciplina().getId());
            newObj.setDisciplina(disciplina);
        }
        
        // Atualiza professor apenas se for fornecida no JSON
        if (obj.getProfessor() != null && obj.getProfessor().getId() != null) {
            Professor professor = professorService.findById(obj.getProfessor().getId());
            newObj.setProfessor(professor);
        }
        
        return turmaRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            turmaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar a turma! Id: " + id);
        }
    }
    public List<Turma> findAll() {
        return turmaRepository.findAll();
    }
}