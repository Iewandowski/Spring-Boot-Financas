package com.lewandowski.minhasfinancas.dto;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
}
