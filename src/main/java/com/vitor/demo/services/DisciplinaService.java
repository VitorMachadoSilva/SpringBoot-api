package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.DisciplinaRepository;
import com.vitor.demo.models.Disciplina;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Disciplina findById(Long id) {
        Optional<Disciplina> disciplina = disciplinaRepository.findById(id);
        return disciplina.orElseThrow(() -> new ResourceNotFoundException("Disciplina", id));
    }

    @Transactional
    public Disciplina create(Disciplina obj) {
        // Apenas admin pode criar disciplinas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar disciplinas");
        }
        
        obj.setId(null);
        try {
            return disciplinaRepository.save(obj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar disciplina. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public Disciplina update(Disciplina obj) {
        // Apenas admin pode atualizar disciplinas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar disciplinas");
        }
        
        Disciplina newObj = findById(obj.getId());
        
        newObj.setNome(obj.getNome());
        newObj.setCargaHoraria(obj.getCargaHoraria());
        newObj.setEmenta(obj.getEmenta());
        
        try {
            return disciplinaRepository.save(newObj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar disciplina. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Apenas admin pode excluir disciplinas
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir disciplinas");
        }
        
        Disciplina disciplina = findById(id);
        try {
            disciplinaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir a disciplina " + disciplina.getNome() + " pois existem turmas vinculadas a ela.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir disciplina: " + e.getMessage());
        }
    }

    public List<Disciplina> findAll() {
        // Qualquer usuário autenticado pode listar disciplinas
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        return disciplinaRepository.findAll();
    }
}