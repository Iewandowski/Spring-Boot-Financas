package com.lewandowski.minhasfinancas.model.repository;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lewandowski.minhasfinancas.model.entity.Usuario;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository user;

    @Autowired
    TestEntityManager entityManager;

    public static Usuario criarUsuario() {
        return Usuario.builder()
                .nome("Usuario")
                .email("email@email.com")
                .senha("senha").build();
    }

    @Test
    public void verificaExistenciaDeEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        boolean result = user.existsByEmail("usuario@email.com");
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void retornaFalsoQuandoNaoHouverEmailCadastrado() {
        boolean result = user.existsByEmail("usuario@email.com");
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void persisteBaseDados() {
        Usuario usuario = criarUsuario();
        Usuario usuarioSalvo = user.save(usuario);

        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    public void buscaUserPorEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        Optional<Usuario> result = user.findByEmail("usuario@email.com");
        Assertions.assertThat(result.isPresent());
    }
}
