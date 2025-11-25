package com.vitor.demo.projections;

public interface UserProjection {
    Long getId();
    String getUsername();
    String getEmail();
    Boolean getAtivo();
}