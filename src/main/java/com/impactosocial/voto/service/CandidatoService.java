package com.impactosocial.voto.service;

import com.impactosocial.voto.dto.ResultadoArrastaoDTO;
import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.repository.CandidatoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    public Candidato buscarCandidatoEspecifico(Integer numero,  String estadoUf, String cargo ) {
        // Agora vamos buscar no banco de dados de verdade.
        // Se não achar, ele lança um erro avisando que o candidato não existe.
        return repository.findByNumeroAndEstadoUfAndCargo(numero, estadoUf, cargo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidato não encontrado com o número " + numero +
                                " para o cargo de " + cargo + " em " + estadoUf));
    }
    public Candidato salvarCandidato(Candidato candidato){
        return repository.save(candidato);
    }

    public ResultadoArrastaoDTO calcularImpacto(Integer numero, String estadoUf, String cargo) {
        //Busca candidato da pesquisa
        Candidato principal = buscarCandidatoEspecifico(numero, estadoUf, cargo);

        //busca lista de beneficados
        List<Candidato> colegas = repository.findByFederacaoAndEstadoUfAndCargo(
                principal.getFederacao(),
                principal.getEstadoUf(),
                principal.getCargo()
        );

        colegas.removeIf(c -> c.getId().equals(principal.getId()));
        return new ResultadoArrastaoDTO(principal, colegas);
    }

}