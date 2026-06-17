package com.impactosocial.voto.repository;

import com.impactosocial.voto.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    // Ignora letras maiúsculas e minúsculas no Estado e no Cargo
    Optional<Candidato> findByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCase(Integer numero, String estadoUf, String cargo);

    // Busca beneficiados da mesma federação
    List<Candidato> findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCase(String federacao, String estadoUf, String cargo);

    // Busca beneficiados do mesmo partido (caso não tenha federação)
    List<Candidato> findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCase(String partido, String estadoUf, String cargo);

}