package com.lewandowski.minhasfinancas.service;

import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lewandowski.minhasfinancas.exception.RegraNegocioException;
import com.lewandowski.minhasfinancas.model.entity.Lancamento;
import com.lewandowski.minhasfinancas.model.entity.Usuario;
import com.lewandowski.minhasfinancas.model.enums.StatusLancamento;
import com.lewandowski.minhasfinancas.model.repository.LancamentoRepository;
import com.lewandowski.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.lewandowski.minhasfinancas.service.implementations.LancamentoServiceImpl;

import lombok.val;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
    @SpyBean
    LancamentoServiceImpl service;
    
    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarLancamento() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
        
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoSalvaLancamentoComErroValidacao() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarLancamento() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);
        
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);        
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoNaoSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        
        catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    public void deveDeletarLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        service.deletar(lancamento);
        Mockito.verify(repository).delete(lancamento);

    }

    @Test
    public void deveLancarErroAoTentarDeletarLancamentoNaoSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when( repository.findAll( Mockito.any(Example.class)) ).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);
    }

    @Test
    public void deveAtualizarStatusLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterLancamentoPorId() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> resultado = service.obterPorId(id);
        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExiste() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Lancamento> resultado = service.obterPorId(id);
        assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();
        Throwable erro = catchThrowable(() -> service.validar(lancamento));
        //DESCRICAO
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida");

        lancamento.setDescricao("");

        catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida");

        //MES
        lancamento.setDescricao("descrição");
        
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");
        
        lancamento.setMes(0);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");

        lancamento.setMes(13);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");

        //ANO
        lancamento.setMes(1);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido");

        lancamento.setAno(222);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido");

        //USUARIO
        lancamento.setAno(2023);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");
        
        lancamento.setIdUsuario(new Usuario());
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");
        //VALOR        
        lancamento.getIdUsuario().setId(1L);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido");

        lancamento.setValor(BigDecimal.ZERO);
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido");
        //TIPO
        lancamento.setValor(BigDecimal.valueOf(5));

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento");        
    }

    

}
