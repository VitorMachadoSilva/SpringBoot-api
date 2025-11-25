package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.AlunoRepository;
import com.vitor.demo.models.Aluno;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    public Aluno findById(Long id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);
        return aluno.orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    @Transactional
    public Aluno create(Aluno obj) {
        // Apenas admin pode criar alunos
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem criar alunos");
        }
        
        // Verifica se CPF já existe
        if (alunoRepository.existsByCpf(obj.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + obj.getCpf());
        }
        
        // Verifica se nome já existe (se desejar essa validação)
        if (alunoRepository.existsByNome(obj.getNome())) {
            throw new BusinessException("Nome já cadastrado: " + obj.getNome());
        }
        
        obj.setId(null);
        return alunoRepository.save(obj);
    }

    @Transactional
    public Aluno update(Aluno obj) {
        // Apenas admin pode atualizar alunos
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem atualizar alunos");
        }
        
        Aluno newObj = findById(obj.getId());
        
        // Verifica se o CPF foi alterado e se já existe outro aluno com esse CPF
        if (!newObj.getCpf().equals(obj.getCpf()) && alunoRepository.existsByCpf(obj.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + obj.getCpf());
        }
        
        // Verifica se o nome foi alterado e se já existe outro aluno com esse nome
        if (!newObj.getNome().equals(obj.getNome()) && alunoRepository.existsByNome(obj.getNome())) {
            throw new BusinessException("Nome já cadastrado: " + obj.getNome());
        }
        
        newObj.setNome(obj.getNome());
        newObj.setCpf(obj.getCpf());
        return alunoRepository.save(newObj);
    }

    public void delete(Long id) {
        // Apenas admin pode excluir alunos
        if (!UserService.isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem excluir alunos");
        }
        
        Aluno aluno = findById(id);
        try {
            alunoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar o aluno! Id: " + id);
        }
    }

    public List<Aluno> findAll() {
        // Qualquer usuário autenticado pode listar alunos
        if (UserService.authenticated() == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        return alunoRepository.findAll();
    }
}