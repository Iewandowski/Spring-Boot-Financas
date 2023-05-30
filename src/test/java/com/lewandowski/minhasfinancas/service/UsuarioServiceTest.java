package com.lewandowski.minhasfinancas.service;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lewandowski.minhasfinancas.exception.ErroAutenticacaoException;
import com.lewandowski.minhasfinancas.exception.RegraNegocioException;
import com.lewandowski.minhasfinancas.model.entity.Usuario;
import com.lewandowski.minhasfinancas.model.repository.UsuarioRepository;
import com.lewandowski.minhasfinancas.service.implementations.UsuarioServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test(expected = Test.None.class)
    public void deveSalvarUsuario() {
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .nome("nome")
                .email("email@email.com")
                .senha("senha").build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        Usuario usuarioSalvo = service.salvarUsuario(usuario);

        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUsuarioComEmailCadastrado() {
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
        service.salvarUsuario(usuario);
        Mockito.verify(repository, Mockito.never()).save(usuario);
    }

    @Test
    public void autenticaUsuarioComSucesso() {
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario result = service.autenticar(email, senha);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void erroAoNaoEncontrarUsuarioComEmailInformado() {
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class)
                .hasMessage("Usuario não encontrado");
    }

    @Test
    public void erroAoInserirSenhaInvalida() {
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha inválida");
    }

    @Test(expected = Test.None.class)
    public void deveValidarEmail() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        service.validarEmail("email@email.com");
    }

    @Test(expected = RegraNegocioException.class)
    public void erroEmailJaCadastrado() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        service.validarEmail("email@email.com");
    }

}
