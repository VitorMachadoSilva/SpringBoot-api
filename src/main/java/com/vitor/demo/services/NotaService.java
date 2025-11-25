package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.NotaRepository;
import com.vitor.demo.models.Nota;
import com.vitor.demo.security.UserSpringSecurity;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private TurmaService turmaService;

    public Nota findById(Long id) {
        Optional<Nota> nota = notaRepository.findById(id);
        Nota foundNota = nota.orElseThrow(() -> new ResourceNotFoundException("Nota", id));
        
        // Validar se usuário pode acessar esta nota
        validateNotaAccess(foundNota);
        
        return foundNota;
    }

    public List<Nota> findByAlunoId(Long alunoId) {
        // Admin pode ver todas as notas, usuário comum só as suas
        UserSpringSecurity currentUser = UserService.authenticated();
        if (currentUser == null) {
            throw new AuthorizationException("Usuário não autenticado");
        }
        
        if (!UserService.isAdmin() && !currentUser.getId().equals(alunoId)) {
            throw new AuthorizationException("Acesso negado: você só pode visualizar suas próprias notas");
        }
        
        return notaRepository.findByAlunoId(alunoId);
    }

    public List<Nota> findByTurmaId(Long turmaId) {
        // Apenas admin pode ver todas as notas de uma turma
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem visualizar notas por turma");
        }
        
        return notaRepository.findByTurmaId(turmaId);
    }

    @Transactional
    public Nota create(Nota obj) {
        // Apenas admin pode criar notas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar notas");
        }
        
        obj.setId(null);
        
        // Verificar se aluno e turma existem
        alunoService.findById(obj.getAluno().getId());
        turmaService.findById(obj.getTurma().getId());
        
        // Validação adicional da nota
        if (obj.getValor() == null || obj.getValor().compareTo(java.math.BigDecimal.ZERO) < 0 || 
            obj.getValor().compareTo(new java.math.BigDecimal("10.00")) > 0) {
            throw new BusinessException("A nota deve estar entre 0 e 10.");
        }
        
        try {
            return notaRepository.save(obj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar nota. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public Nota update(Nota obj) {
        // Apenas admin pode atualizar notas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar notas");
        }
        
        Nota newObj = findById(obj.getId());
        
        // Verificar se aluno e turma existem
        if (obj.getAluno() != null) {
            alunoService.findById(obj.getAluno().getId());
            newObj.setAluno(obj.getAluno());
        }
        
        if (obj.getTurma() != null) {
            turmaService.findById(obj.getTurma().getId());
            newObj.setTurma(obj.getTurma());
        }
        
        // Validação da nota
        if (obj.getValor() != null) {
            if (obj.getValor().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                obj.getValor().compareTo(new java.math.BigDecimal("10.00")) > 0) {
                throw new BusinessException("A nota deve estar entre 0 e 10.");
            }
            newObj.setValor(obj.getValor());
        }
        
        if (obj.getObservacao() != null) {
            newObj.setObservacao(obj.getObservacao());
        }
        
        try {
            return notaRepository.save(newObj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar nota. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Apenas admin pode excluir notas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir notas");
        }
        
        Nota nota = findById(id);
        try {
            notaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir nota: " + e.getMessage());
        }
    }

    public List<Nota> findAll() {
        // Apenas admin pode listar todas as notas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem listar todas as notas");
        }
        
        return notaRepository.findAll();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void validateNotaAccess(Nota nota) {
        UserSpringSecurity currentUser = UserService.authenticated();
        if (currentUser == null) {
            throw new AuthorizationException("Usuário não autenticado");
        }
        
        // Admin tem acesso a tudo
        if (UserService.isAdmin()) {
            return;
        }
        
        // Usuário comum só pode acessar suas próprias notas
        if (!currentUser.getId().equals(nota.getAluno().getId())) {
            throw new AuthorizationException("Acesso negado: você só pode visualizar suas próprias notas");
        }
    }
}