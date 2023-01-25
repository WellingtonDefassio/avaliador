package io.git.welldefassio.avaliador.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.git.welldefassio.avaliador.application.exception.DadosClienteNotFoundException;
import io.git.welldefassio.avaliador.application.exception.ErroComunicacaoException;
import io.git.welldefassio.avaliador.application.exception.SolicitacaoCartaoException;
import io.git.welldefassio.avaliador.domain.model.*;
import io.git.welldefassio.avaliador.infra.clientes.CartoesResourceClient;
import io.git.welldefassio.avaliador.infra.clientes.ClientesResourceClient;
import io.git.welldefassio.avaliador.infra.mqueue.EmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvaliadorService {

    private final ClientesResourceClient clienteClient;
    private final CartoesResourceClient cartoesClient;

    private final EmissaoCartaoPublisher emissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesByClienteResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente.builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesByClienteResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAteh(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            DadosCliente dadosCliente = dadosClienteResponse.getBody();

            List<CartaoAprovado> cartoesAprovado = cartoes.stream().map(cartao -> {
                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));

                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado cartaoAprovado = new CartaoAprovado();
                cartaoAprovado.setCartao(cartao.getNome());
                cartaoAprovado.setBandeira(cartao.getBandeira());
                cartaoAprovado.setLimiteAprovado(limiteAprovado);

                return cartaoAprovado;
            }).toList();

            return new RetornoAvaliacaoCliente(cartoesAprovado);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosEmissaoCartao dados) {
        try {
            emissaoCartaoPublisher.solicitarCartao(dados);
            String protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception e) {
            throw new SolicitacaoCartaoException(e.getMessage());
        }
    }

}
