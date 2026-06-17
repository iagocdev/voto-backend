package com.impactosocial.voto.service;

import com.impactosocial.voto.dto.ResultadoArrastaoDTO;
import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.repository.CandidatoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CandidatoService {

    private final CandidatoRepository repository;

    // Injetando o repositório
    public CandidatoService(CandidatoRepository repository) {
        this.repository = repository;
    }

    public Candidato buscarCandidatoEspecifico(Integer numero, String estadoUf, String cargo) {
        // TRATAMENTO INTELIGENTE: Corrige "Estadual" para "Distrital" no DF
        // E usamos equalsIgnoreCase para não dar erro se o Angular mandar "df" ou "DF"
        if (estadoUf.equalsIgnoreCase("DF") && cargo.equalsIgnoreCase("Deputado Estadual")) {
            cargo = "Deputado Distrital";
        }

        // Agora busca no banco ignorando letras maiúsculas e minúsculas (IgnoreCase)
        String finalCargo = cargo;
        return repository.findByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCase(numero, estadoUf, cargo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidato não encontrado com o número " + numero +
                                " para o cargo de " + finalCargo + " em " + estadoUf));
    }

    public Candidato salvarCandidato(Candidato candidato){
        return repository.save(candidato);
    }

    public ResultadoArrastaoDTO calcularImpacto(Integer numero, String estadoUf, String cargo) {
        // Busca o candidato (A correção de DF/Distrital já acontece automaticamente lá dentro!)
        Candidato principal = buscarCandidatoEspecifico(numero, estadoUf, cargo);

        List<Candidato> colegas;

        // Se o candidato faz parte de uma federação, a busca é pela federação.
        // Se a federação for "Nenhuma", a busca é apenas pelo partido.
        if (!principal.getFederacao().equalsIgnoreCase("Nenhuma")) {
            colegas = repository.findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCase(
                    principal.getFederacao(),
                    principal.getEstadoUf(),
                    principal.getCargo()
            );
        } else {
            colegas = repository.findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCase(
                    principal.getPartido(),
                    principal.getEstadoUf(),
                    principal.getCargo()
            );
        }

        // Remove o próprio candidato da lista de beneficiados
        colegas.removeIf(c -> c.getId().equals(principal.getId()));

        return new ResultadoArrastaoDTO(principal, colegas);
    }
}