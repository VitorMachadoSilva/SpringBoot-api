package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.NotaRepository;
import com.vitor.demo.models.Nota;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

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
        return nota.orElseThrow(() -> new RuntimeException("Nota não encontrada! Id: " + id));
    }

    public List<Nota> findByAlunoId(Long alunoId) {
        return notaRepository.findByAlunoId(alunoId);
    }

    public List<Nota> findByTurmaId(Long turmaId) {
        return notaRepository.findByTurmaId(turmaId);
    }

    @Transactional
    public Nota create(Nota obj) {
        obj.setId(null);
        
        // Verificar se aluno e turma existem
        alunoService.findById(obj.getAluno().getId());
        turmaService.findById(obj.getTurma().getId());
        
        return notaRepository.save(obj);
    }

    @Transactional
    public Nota update(Nota obj) {
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
        
        newObj.setValor(obj.getValor());
        newObj.setObservacao(obj.getObservacao());
        
        return notaRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            notaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível deletar a nota! Id: " + id);
        }
    }
    public List<Nota> findAll() {
        return notaRepository.findAll();
    }
}