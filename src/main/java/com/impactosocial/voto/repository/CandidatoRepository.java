package com.impactosocial.voto.repository;

import com.impactosocial.voto.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    boolean existsByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
            Integer numero, String estadoUf, String cargo, Integer anoEleicao
    );

    Optional<Candidato> findFirstByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
            Integer numero, String estadoUf, String cargo, Integer anoEleicao
    );

    // ==========================================
    //  MÉTODOS DE HISTÓRICO (2022 para trás)
    // ==========================================
    List<Candidato> findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrueAndAnoEleicao(
            String federacao, String estadoUf, String cargo, Integer anoEleicao
    );

    List<Candidato> findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrueAndAnoEleicao(
            String partido, String estadoUf, String cargo, Integer anoEleicao
    );

    // ==========================================
    //  MÉTODOS DE PROJEÇÃO (2026 para frente)
    // ==========================================
    List<Candidato> findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
            String federacao, String estadoUf, String cargo, Integer anoEleicao
    );

    List<Candidato> findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndAnoEleicao(
            String partido, String estadoUf, String cargo, Integer anoEleicao
    );
}