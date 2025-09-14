package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.DisciplinaRepository;
import com.vitor.demo.models.Disciplina;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Disciplina findById(Long id) {
        Optional<Disciplina> disciplina = disciplinaRepository.findById(id);
        return disciplina.orElseThrow(() -> new RuntimeException("Disciplina não encontrada! Id: " + id));
    }

    @Transactional
    public Disciplina create(Disciplina obj) {
        obj.setId(null);
        return disciplinaRepository.save(obj);
    }

    @Transactional
    public Disciplina update(Disciplina obj) {
        Disciplina newObj = findById(obj.getId());
        newObj.setNome(obj.getNome());
        newObj.setCargaHoraria(obj.getCargaHoraria());
        newObj.setEmenta(obj.getEmenta());
        return disciplinaRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            disciplinaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar a disciplina! Id: " + id);
        }
    }
    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }
}