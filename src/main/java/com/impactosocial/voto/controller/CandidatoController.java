package com.impactosocial.voto.controller;

import com.impactosocial.voto.dto.ResultadoArrastaoDTO;
import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.service.CandidatoService;
import com.impactosocial.voto.service.TseImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    // A injeção do serviço de importação
    @Autowired
    private TseImportService tseImportService;

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
            @RequestParam String cargo ) {
        return candidatoService.buscarCandidatoEspecifico(numero, estadoUf, cargo);
    }

    @GetMapping("/impacto")
    public ResultadoArrastaoDTO simularArrastao(
            @RequestParam Integer numero,
            @RequestParam String estadoUf,
            @RequestParam String cargo){
        return candidatoService.calcularImpacto(numero, estadoUf, cargo);
    }

    @PostMapping("/importar-tse")
    public ResponseEntity<String> importarDadosTse() {
        String resultado = tseImportService.importarDadosCsv();
        return ResponseEntity.ok(resultado);
    }

}