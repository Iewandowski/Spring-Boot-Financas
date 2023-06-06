package com.lewandowski.minhasfinancas.service;

import java.util.List;

import com.lewandowski.minhasfinancas.model.entity.Lancamento;
import com.lewandowski.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {
    
    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void deletar(Lancamento lancamento);
    List<Lancamento> buscar(Lancamento filtro);
    void atualizarStatus(Lancamento lancamento, StatusLancamento status);
    void validar(Lancamento lancamento);
}
