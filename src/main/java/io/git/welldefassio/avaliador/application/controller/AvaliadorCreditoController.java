package io.git.welldefassio.avaliador.application.controller;

import io.git.welldefassio.avaliador.application.exception.DadosClienteNotFoundException;
import io.git.welldefassio.avaliador.application.exception.ErroComunicacaoException;
import io.git.welldefassio.avaliador.application.service.AvaliadorService;
import io.git.welldefassio.avaliador.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes")
@RequiredArgsConstructor
public class AvaliadorCreditoController {


    private final AvaliadorService avaliadorService;


    @GetMapping
    public String status() {
        return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity<SituacaoCliente> consultaSituacaoCliente(@RequestParam("cpf") String cpf) {
        try {
            return ResponseEntity.ok(avaliadorService.obterSituacaoCliente(cpf));
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).build();
        }
    }

    @PostMapping
    public ResponseEntity<RetornoAvaliacaoCliente> realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
        try {
            RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorService.realizarAvaliacao(dados.getCpf(), dados.getRenda());
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).build();
        }

    }
    @PostMapping("solicitar-cartao")
    public ResponseEntity<ProtocoloSolicitacaoCartao> solicitarCartao(@RequestBody DadosEmissaoCartao dados) {
        try {
            ProtocoloSolicitacaoCartao protocolo = avaliadorService.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocolo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

}
