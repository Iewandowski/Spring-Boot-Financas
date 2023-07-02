package com.lewandowski.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lewandowski.minhasfinancas.model.entity.Lancamento;
import com.lewandowski.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = " SELECT SUM(l.valor) "
                + " FROM Lancamento l JOIN l.idUsuario u WHERE u.id = :idUsuario and l.tipo = :tipo"
                + " GROUP BY u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario( @Param("idUsuario") Long idUsuario, 
                                                    @Param("tipo") TipoLancamento tipo);
}
