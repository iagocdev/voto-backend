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

    // Injetando o repositório via construtor
    public CandidatoService(CandidatoRepository repository) {
        this.repository = repository;
    }

    // O anoEleicao é parâmetro obrigatório para garantir o isolamento temporal
    public Candidato buscarCandidatoEspecifico(Integer numero, String estadoUf, String cargo, Integer anoEleicao) {
        // TRATAMENTO INTELIGENTE: Corrige "Estadual" para "Distrital" no DF
        if (estadoUf.equalsIgnoreCase("DF") && cargo.equalsIgnoreCase("Deputado Estadual")) {
            cargo = "Deputado Distrital";
        }

        String finalCargo = cargo;
        // Chamando o método do repositório indexado por ano
        return repository.findFirstByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(numero, estadoUf, cargo, anoEleicao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidato não encontrado com o número " + numero +
                                " para o cargo de " + finalCargo + " em " + estadoUf + " no ano de " + anoEleicao));
    }

    public Candidato salvarCandidato(Candidato candidato){
        return repository.save(candidato);
    }

    // Calcula o impacto aplicando o desvio de comportamento com base no ano eleitoral
    public ResultadoArrastaoDTO calcularImpacto(Integer numero, String estadoUf, String cargo, Integer anoEleicao) {
        // Busca o candidato de referência para o ano solicitado
        Candidato principal = buscarCandidatoEspecifico(numero, estadoUf, cargo, anoEleicao);

        List<Candidato> colegas;

        // BIFURCAÇÃO DA REGRA DE NEGÓCIO:
        // Anos a partir de 2026 são considerados "Projeção" (retorna toda a legenda ativa).
        // Anos anteriores (como 2022) são considerados "Histórico" (retorna estritamente quem obteve êxito nas urnas).
        boolean isProjecao = anoEleicao >= 2026;

        if (!principal.getFederacao().equalsIgnoreCase("Nenhuma")) {
            if (isProjecao) {
                // 2026: Puxa todos os membros da federação que dividem a mesma legenda e podem se beneficiar
                colegas = repository.findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
                        principal.getFederacao(),
                        principal.getEstadoUf(),
                        principal.getCargo(),
                        principal.getAnoEleicao()
                );
            } else {
                // 2022: Filtra exclusivamente os companheiros de federação que terminaram eleitos
                colegas = repository.findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrueAndAnoEleicao(
                        principal.getFederacao(),
                        principal.getEstadoUf(),
                        principal.getCargo(),
                        principal.getAnoEleicao()
                );
            }
        } else {
            if (isProjecao) {
                // 2026: Puxa todos os membros do partido isolado para mapeamento de potencial de votos
                colegas = repository.findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
                        principal.getPartido(),
                        principal.getEstadoUf(),
                        principal.getCargo(),
                        principal.getAnoEleicao()
                );
            } else {
                // 2022: Filtra exclusivamente os companheiros de partido isolado que terminaram eleitos
                colegas = repository.findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrueAndAnoEleicao(
                        principal.getPartido(),
                        principal.getEstadoUf(),
                        principal.getCargo(),
                        principal.getAnoEleicao()
                );
            }
        }

        // Remove o candidato principal da lista para exibir apenas os terceiros impactados
        colegas.removeIf(c -> c.getId().equals(principal.getId()));

        return new ResultadoArrastaoDTO(principal, colegas);
    }
}