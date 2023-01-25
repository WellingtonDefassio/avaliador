package io.git.welldefassio.avaliador.application.exception;

public class DadosClienteNotFoundException extends Exception{

    public DadosClienteNotFoundException() {
        super("Dados cliente não encontrado para cpf informado");
    }
}
