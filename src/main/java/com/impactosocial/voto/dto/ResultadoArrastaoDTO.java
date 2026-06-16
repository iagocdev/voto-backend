package com.impactosocial.voto.dto;

import com.impactosocial.voto.model.Candidato;

import java.util.List;

public class ResultadoArrastaoDTO {
    private Candidato candidatoPrincipal;
    private List<Candidato> beneficiados;

    public ResultadoArrastaoDTO(Candidato candidatoPrincipal, List<Candidato> beneficiados) {
        this.candidatoPrincipal = candidatoPrincipal;
        this.beneficiados = beneficiados;
    }
    public Candidato getCandidatoPrincipal(){return candidatoPrincipal;}
    public void setCandidatoPrincipal(Candidato candidatoPrincipal){
        this.candidatoPrincipal = candidatoPrincipal;}

    public List<Candidato> getBeneficiados(){return beneficiados;}
    public void setBeneficiados(List<Candidato> beneficiados) {
        this.beneficiados = beneficiados;
    }
}
