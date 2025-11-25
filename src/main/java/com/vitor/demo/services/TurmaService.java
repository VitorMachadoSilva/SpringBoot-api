package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.TurmaRepository;
import com.vitor.demo.models.Disciplina;
import com.vitor.demo.models.Professor;
import com.vitor.demo.models.Turma;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

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
        return turma.orElseThrow(() -> new ResourceNotFoundException("Turma", id));
    }

    public List<Turma> findByDisciplinaId(Long disciplinaId) {
        // Qualquer usuário autenticado pode ver turmas por disciplina
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        // Verifica se a disciplina existe
        disciplinaService.findById(disciplinaId);
        return turmaRepository.findByDisciplinaId(disciplinaId);
    }

    public List<Turma> findByProfessorId(Long professorId) {
        // Qualquer usuário autenticado pode ver turmas por professor
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        // Verifica se o professor existe
        professorService.findById(professorId);
        return turmaRepository.findByProfessorId(professorId);
    }

    @Transactional
    public Turma create(Turma obj) {
        // Apenas admin pode criar turmas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar turmas");
        }
        
        obj.setId(null);
        
        // Verificar se disciplina e professor existem
        Disciplina disciplina = disciplinaService.findById(obj.getDisciplina().getId());
        Professor professor = professorService.findById(obj.getProfessor().getId());
        
        obj.setDisciplina(disciplina);
        obj.setProfessor(professor);
        
        try {
            return turmaRepository.save(obj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar turma. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public Turma update(Turma obj) {
        // Apenas admin pode atualizar turmas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar turmas");
        }
        
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
        
        try {
            return turmaRepository.save(newObj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar turma. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Apenas admin pode excluir turmas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir turmas");
        }
        
        Turma turma = findById(id);
        try {
            turmaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir a turma de " + turma.getDisciplina().getNome() + " pois existem matrículas ou notas vinculadas a ela.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir turma: " + e.getMessage());
        }
    }

    public List<Turma> findAll() {
        // Qualquer usuário autenticado pode listar turmas
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        return turmaRepository.findAll();
    }
}