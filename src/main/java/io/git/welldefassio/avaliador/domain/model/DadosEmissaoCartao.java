package io.git.welldefassio.avaliador.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosEmissaoCartao {

    private Long idCartao;
    private String cpf;
    private String endereco;
    private BigDecimal limiteLiberado;

}
