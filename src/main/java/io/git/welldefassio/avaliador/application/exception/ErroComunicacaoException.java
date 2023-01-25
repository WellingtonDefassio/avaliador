package io.git.welldefassio.avaliador.application.exception;

import lombok.Getter;

public class ErroComunicacaoException extends Exception {
    @Getter
    private Integer status;

    public ErroComunicacaoException(String msg, Integer status) {

        super(msg);
        this.status = status;
    }
}
