package com.impactosocial.voto.model;

import jakarta.persistence.*;

@Entity
@Table(name ="candidato", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"numero","estado_uf","cargo" ,"ano_eleicao"})
} )
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificação
    private String nomeUrna;
    private Integer numero;
    private String cargo;
    private String estadoUf;

    @Column(name = "ano_eleicao")
    private Integer anoEleicao;

    // Regras Eleitorais
    private String partido;
    private String federacao;
    private String situacao;
    private Boolean eleito;

    // Construtor Vazio (Obrigatório para o Spring/Hibernate)
    public Candidato() {
    }

    // Construtor Completo (Vai nos ajudar na hora de criar os scripts de importação)
    public Candidato(String nomeUrna, Integer numero, String cargo, String estadoUf, String partido, String federacao, String situacao) {
        this.nomeUrna = nomeUrna;
        this.numero = numero;
        this.cargo = cargo;
        this.estadoUf = estadoUf;
        this.anoEleicao = anoEleicao;
        this.partido = partido;
        this.federacao = federacao;
        this.situacao = situacao;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeUrna() { return nomeUrna; }
    public void setNomeUrna(String nomeUrna) { this.nomeUrna = nomeUrna; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getEstadoUf() { return estadoUf; }
    public void setEstadoUf(String estadoUf) { this.estadoUf = estadoUf; }

    public Integer getAnoEleicao() { return anoEleicao; }
    public void setAnoEleicao(Integer anoEleicao) { this.anoEleicao = anoEleicao; }

    public String getPartido() { return partido; }
    public void setPartido(String partido) { this.partido = partido; }

    public String getFederacao() { return federacao; }
    public void setFederacao(String federacao) { this.federacao = federacao; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public Boolean getEleito() {return eleito;}

    public void setEleito(Boolean eleito) {this.eleito = eleito;}
}