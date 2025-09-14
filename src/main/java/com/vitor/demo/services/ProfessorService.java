package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.ProfessorRepository;
import com.vitor.demo.models.Professor;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public Professor findById(Long id) {
        Optional<Professor> professor = professorRepository.findById(id);
        return professor.orElseThrow(() -> new RuntimeException("Professor não encontrado! Id: " + id));
    }

    @Transactional
    public Professor create(Professor obj) {
        obj.setId(null);
        return professorRepository.save(obj);
    }

    @Transactional
    public Professor update(Professor obj) {
        Professor newObj = findById(obj.getId());
        newObj.setNome(obj.getNome());
        newObj.setEmail(obj.getEmail());
        newObj.setTelefone(obj.getTelefone());
        return professorRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            professorRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar o professor! Id: " + id);
        }
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }
}