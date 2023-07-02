package com.lewandowski.minhasfinancas.controller;

import java.security.Provider.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.lewandowski.minhasfinancas.dto.AtualizaStatusDTO;
import com.lewandowski.minhasfinancas.dto.LancamentoDTO;
import com.lewandowski.minhasfinancas.exception.RegraNegocioException;
import com.lewandowski.minhasfinancas.model.entity.Lancamento;
import com.lewandowski.minhasfinancas.model.entity.Usuario;
import com.lewandowski.minhasfinancas.model.enums.StatusLancamento;
import com.lewandowski.minhasfinancas.model.enums.TipoLancamento;
import com.lewandowski.minhasfinancas.service.LancamentoService;
import com.lewandowski.minhasfinancas.service.UsuarioService;

import javassist.bytecode.stackmap.BasicBlock.Catch;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
    
    private final LancamentoService lancamento;
    private final UsuarioService user;

    public LancamentoController(LancamentoService lancamento, UsuarioService user) {
        this.lancamento = lancamento;
        this.user = user;
    }
    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = lancamento.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
            //return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    private ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return lancamento.obterPorId(id).map(entity -> {
            try {
                Lancamento lanc = converter(dto);
                lanc.setId(entity.getId());
                lancamento.atualizar(lanc);
                return ResponseEntity.ok(lanc);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }            
        }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST)); 
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar ( @PathVariable("id") Long id) {
        return lancamento.obterPorId(id).map( entidade -> {
            lancamento.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () ->
            new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscar (
        @RequestParam(value = "descricao", required = false) String descricao, 
        @RequestParam(value = "mes", required = false) Integer mes, 
        @RequestParam(value = "ano", required = false) Integer ano,
        @RequestParam(value = "usuario", required = false) Long idUsuario) {
            Lancamento filtro = new Lancamento();
            filtro.setDescricao(descricao);
            filtro.setMes(mes);
            filtro.setAno(ano);
            filtro.setIdUsuario(null);

            Optional<Usuario> usuario = user.obterPorId(idUsuario);
            if (!usuario.isPresent()) {
                return ResponseEntity.badRequest().body("Não foi possível realizar a consulta: Usuario não encontrado na base de dados");
            }
            else {
                filtro.setIdUsuario(usuario.get());
            }

            List<Lancamento> lancamentos = lancamento.buscar(filtro);
            return ResponseEntity.ok(lancamentos);
        }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus( @PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto ) {
        return lancamento.obterPorId(id).map( entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento");
            }
            try {
                entity.setStatus(statusSelecionado);
                lancamento.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch(RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }         
        }).orElseGet( () -> 
            new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());        

        Usuario usuario = user
                        .obterPorId(dto.getIdUsuario())
                        .orElseThrow( () -> new RegraNegocioException("Usuario não encontrado para o Id informado."));
        
        lancamento.setIdUsuario(usuario);
        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }


}
