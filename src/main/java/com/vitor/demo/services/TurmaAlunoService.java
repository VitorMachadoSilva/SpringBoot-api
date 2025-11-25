package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.TurmaAlunoRepository;
import com.vitor.demo.models.TurmaAluno;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class TurmaAlunoService {

    @Autowired
    private TurmaAlunoRepository turmaAlunoRepository;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private TurmaService turmaService;

    public TurmaAluno findById(Long id) {
        Optional<TurmaAluno> turmaAluno = turmaAlunoRepository.findById(id);
        return turmaAluno.orElseThrow(() -> new ResourceNotFoundException("Matrícula", id));
    }

    public List<TurmaAluno> findByAlunoId(Long alunoId) {
        // Admin pode ver todas as matrículas, usuário comum só as suas
        if (!UserService.isAdmin() && !UserService.getCurrentUserId().equals(alunoId)) {
            throw new AuthorizationException("Acesso negado: você só pode visualizar suas próprias matrículas");
        }
        
        // Verifica se o aluno existe
        alunoService.findById(alunoId);
        return turmaAlunoRepository.findByAlunoId(alunoId);
    }

    public List<TurmaAluno> findByTurmaId(Long turmaId) {
        // Apenas admin pode ver matrículas por turma
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem visualizar matrículas por turma");
        }
        
        // Verifica se a turma existe
        turmaService.findById(turmaId);
        return turmaAlunoRepository.findByTurmaId(turmaId);
    }

    @Transactional
    public TurmaAluno create(TurmaAluno obj) {
        // Apenas admin pode criar matrículas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar matrículas");
        }
        
        obj.setId(null);
        
        // Verificar se aluno e turma existem
        alunoService.findById(obj.getAluno().getId());
        turmaService.findById(obj.getTurma().getId());
        
        // Verificar se já existe matrícula ativa para este aluno na turma
        List<TurmaAluno> matriculasExistentes = turmaAlunoRepository.findByAlunoIdAndTurmaId(
            obj.getAluno().getId(), obj.getTurma().getId());
        
        boolean matriculaAtiva = matriculasExistentes.stream()
            .anyMatch(m -> m.getAtivo() && !m.getId().equals(obj.getId()));
            
        if (matriculaAtiva) {
            throw new BusinessException("Este aluno já possui uma matrícula ativa nesta turma.");
        }
        
        try {
            return turmaAlunoRepository.save(obj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar matrícula. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public TurmaAluno update(TurmaAluno obj) {
        // Apenas admin pode atualizar matrículas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar matrículas");
        }
        
        TurmaAluno newObj = findById(obj.getId());
        
        // Verificar se aluno e turma existem
        if (obj.getAluno() != null) {
            alunoService.findById(obj.getAluno().getId());
            newObj.setAluno(obj.getAluno());
        }
        
        if (obj.getTurma() != null) {
            turmaService.findById(obj.getTurma().getId());
            newObj.setTurma(obj.getTurma());
        }
        
        // Verificar duplicação de matrícula ativa
        if (obj.getAluno() != null && obj.getTurma() != null) {
            List<TurmaAluno> matriculasExistentes = turmaAlunoRepository.findByAlunoIdAndTurmaId(
                obj.getAluno().getId(), obj.getTurma().getId());
            
            boolean matriculaAtiva = matriculasExistentes.stream()
                .anyMatch(m -> m.getAtivo() && !m.getId().equals(obj.getId()));
                
            if (matriculaAtiva) {
                throw new BusinessException("Este aluno já possui uma matrícula ativa nesta turma.");
            }
        }
        
        if (obj.getDataIngresso() != null) {
            newObj.setDataIngresso(obj.getDataIngresso());
        }
        
        if (obj.getAtivo() != null) {
            newObj.setAtivo(obj.getAtivo());
        }
        
        try {
            return turmaAlunoRepository.save(newObj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar matrícula. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Apenas admin pode excluir matrículas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir matrículas");
        }
        
        TurmaAluno matricula = findById(id);
        try {
            turmaAlunoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir matrícula: " + e.getMessage());
        }
    }

    public List<TurmaAluno> findAll() {
        // Apenas admin pode listar todas as matrículas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem listar todas as matrículas");
        }
        
        return turmaAlunoRepository.findAll();
    }
}