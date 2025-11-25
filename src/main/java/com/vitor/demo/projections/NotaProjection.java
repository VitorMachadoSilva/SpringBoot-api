package com.vitor.demo.projections;

import java.math.BigDecimal;

public interface NotaProjection {
    Long getId();
    String getAlunoNome();
    String getDisciplinaNome();
    BigDecimal getValor();
    String getObservacao();
}