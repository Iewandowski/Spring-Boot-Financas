package com.lewandowski.minhasfinancas.service.implementations;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lewandowski.minhasfinancas.exception.ErroAutenticacaoException;
import com.lewandowski.minhasfinancas.exception.RegraNegocioException;
import com.lewandowski.minhasfinancas.model.entity.Usuario;
import com.lewandowski.minhasfinancas.model.repository.UsuarioRepository;
import com.lewandowski.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository user;

    public UsuarioServiceImpl(UsuarioRepository user) {
        super();
        this.user = user;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = user.findByEmail(email);
        if (!usuario.isPresent()) {
            throw new ErroAutenticacaoException("Usuario não encontrado");
        }
        if (!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacaoException("Senha inválida");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return user.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = user.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuario cadastrado com este email");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return user.findById(id);
    }

}
