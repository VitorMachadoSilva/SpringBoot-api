package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.ProfessorRepository;
import com.vitor.demo.models.Professor;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public Professor findById(Long id) {
        Optional<Professor> professor = professorRepository.findById(id);
        return professor.orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    }

    @Transactional
    public Professor create(Professor obj) {
        // Apenas admin pode criar professores
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar professores");
        }
        
        obj.setId(null);
        try {
            return professorRepository.save(obj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar professor. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public Professor update(Professor obj) {
        // Apenas admin pode atualizar professores
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar professores");
        }
        
        Professor newObj = findById(obj.getId());
        
        newObj.setNome(obj.getNome());
        newObj.setEmail(obj.getEmail());
        newObj.setTelefone(obj.getTelefone());
        
        try {
            return professorRepository.save(newObj);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar professor. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Apenas admin pode excluir professores
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir professores");
        }
        
        Professor professor = findById(id);
        try {
            professorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir o professor " + professor.getNome() + " pois existem turmas vinculadas a ele.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir professor: " + e.getMessage());
        }
    }

    public List<Professor> findAll() {
        // Qualquer usuário autenticado pode listar professores
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        return professorRepository.findAll();
    }
}