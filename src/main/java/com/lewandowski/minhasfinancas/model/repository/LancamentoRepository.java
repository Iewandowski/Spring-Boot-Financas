package com.lewandowski.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lewandowski.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
