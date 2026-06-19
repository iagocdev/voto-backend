package com.impactosocial.voto.repository;

import com.impactosocial.voto.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    // O candidato principal não precisa do EleitoTrue (pois podemos pesquisar alguém que perdeu)
    Optional<Candidato> findByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCase(Integer numero, String estadoUf, String cargo);

    // Os colegas beneficiados AGORA TÊM QUE SER ELEITOS
    List<Candidato> findByFederacaoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrue(String federacao, String estadoUf, String cargo);

    List<Candidato> findByPartidoAndEstadoUfIgnoreCaseAndCargoIgnoreCaseAndEleitoTrue(String partido, String estadoUf, String cargo);
}