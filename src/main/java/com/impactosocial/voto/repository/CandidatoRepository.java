package com.impactosocial.voto.repository;

import com.impactosocial.voto.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    // O Spring  monta o SQL
    Optional<Candidato> findByNumeroAndEstadoUfAndCargo(Integer numero, String estadoUf, String cargo);

    //Busca Chapa
    List<Candidato> findByFederacaoAndEstadoUfAndCargo(String federacao, String estadoUf, String cargo);
}