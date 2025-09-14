package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.TurmaAlunoRepository;
import com.vitor.demo.models.TurmaAluno;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

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
        return turmaAluno.orElseThrow(() -> new RuntimeException("Matrícula não encontrada! Id: " + id));
    }

    public List<TurmaAluno> findByAlunoId(Long alunoId) {
        return turmaAlunoRepository.findByAlunoId(alunoId);
    }

    public List<TurmaAluno> findByTurmaId(Long turmaId) {
        return turmaAlunoRepository.findByTurmaId(turmaId);
    }

    @Transactional
    public TurmaAluno create(TurmaAluno obj) {
        obj.setId(null);
        
        // Verificar se aluno e turma existem
        alunoService.findById(obj.getAluno().getId());
        turmaService.findById(obj.getTurma().getId());
        
        return turmaAlunoRepository.save(obj);
    }

    @Transactional
    public TurmaAluno update(TurmaAluno obj) {
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
        
        newObj.setDataIngresso(obj.getDataIngresso());
        newObj.setAtivo(obj.getAtivo());
        
        return turmaAlunoRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            turmaAlunoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar a matrícula! Id: " + id);
        }
    }
     public List<TurmaAluno> findAll() {
        return turmaAlunoRepository.findAll();
    }
}