package com.lewandowski.minhasfinancas.exception;

public class ErroAutenticacaoException extends RuntimeException {
    public ErroAutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
