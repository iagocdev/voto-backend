package com.impactosocial.voto.controller;

import com.impactosocial.voto.dto.ResultadoArrastaoDTO;
import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.service.CandidatoService;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @PostMapping
    public Candidato criarCandidato(@RequestBody Candidato candidato){
        return candidatoService.salvarCandidato(candidato);
    }
    @GetMapping("/busca")
    public Candidato buscarCandidato(
            @RequestParam Integer numero,
            @RequestParam String estadoUf,
            @RequestParam  String cargo ) {
        return candidatoService.buscarCandidatoEspecifico(numero, estadoUf, cargo);
    }
    @GetMapping("/impacto")
    public ResultadoArrastaoDTO simularArrastao(
            @RequestParam Integer numero,
            @RequestParam String estadoUf,
            @RequestParam String cargo){
        return candidatoService.calcularImpacto(numero, estadoUf, cargo);
    }



}
