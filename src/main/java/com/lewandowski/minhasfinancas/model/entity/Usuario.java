package com.lewandowski.minhasfinancas.model.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "usuario", schema = "financas")
@Data
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nome;
    @Column
    private String email;
    @Column
    private String senha;
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;
    
    public Usuario(Long id, String nome, String email, String senha, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
    }

    public Usuario() {
    }

    
}
