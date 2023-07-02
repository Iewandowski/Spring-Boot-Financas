package com.lewandowski.minhasfinancas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.lewandowski.minhasfinancas.model.enums.StatusLancamento;
import com.lewandowski.minhasfinancas.model.enums.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "lancamento", schema = "financas")
@Data
@AllArgsConstructor
public class Lancamento {
    public Lancamento() {
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String descricao;

    @Column
    private Integer mes;

    @Column
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @Column
    private BigDecimal valor;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column
    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;

    @Column
    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;

}
