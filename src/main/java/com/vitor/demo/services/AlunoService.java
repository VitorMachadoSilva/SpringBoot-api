package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.AlunoRepository;
import com.vitor.demo.models.Aluno;

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
        return aluno.orElseThrow(() -> new RuntimeException("Aluno não encontrado! Id: " + id));
    }

    @Transactional
    public Aluno create(Aluno obj) {
        // Verifica se CPF já existe
        if (alunoRepository.existsByCpf(obj.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado: " + obj.getCpf());
        }
        
        // Verifica se nome já existe (se desejar essa validação)
        if (alunoRepository.existsByNome(obj.getNome())) {
            throw new DataIntegrityViolationException("Nome já cadastrado: " + obj.getNome());
        }
        
        obj.setId(null);
        return alunoRepository.save(obj);
    }

    @Transactional
    public Aluno update(Aluno obj) {
        Aluno newObj = findById(obj.getId());
        
        // Verifica se o CPF foi alterado e se já existe outro aluno com esse CPF
        if (!newObj.getCpf().equals(obj.getCpf()) && alunoRepository.existsByCpf(obj.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado: " + obj.getCpf());
        }
        
        // Verifica se o nome foi alterado e se já existe outro aluno com esse nome
        if (!newObj.getNome().equals(obj.getNome()) && alunoRepository.existsByNome(obj.getNome())) {
            throw new DataIntegrityViolationException("Nome já cadastrado: " + obj.getNome());
        }
        
        newObj.setNome(obj.getNome());
        newObj.setCpf(obj.getCpf());
        return alunoRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            alunoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar o aluno! Id: " + id);
        }
    }

    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }
}