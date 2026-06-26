package com.impactosocial.voto.controller;

import com.impactosocial.voto.dto.ResultadoArrastaoDTO;
import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.service.CandidatoService;
import com.impactosocial.voto.service.TseImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

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
            @RequestParam String cargo,
            // Adicionado o parâmetro com valor padrão 2022
            @RequestParam(defaultValue = "2022") Integer anoEleicao ) {
        return candidatoService.buscarCandidatoEspecifico(numero, estadoUf, cargo, anoEleicao);
    }

    @GetMapping("/impacto")
    public ResultadoArrastaoDTO simularArrastao(
            @RequestParam Integer numero,
            @RequestParam String estadoUf,
            @RequestParam String cargo,
            //  valor padrão 2022
            @RequestParam(defaultValue = "2022") Integer anoEleicao){
        return candidatoService.calcularImpacto(numero, estadoUf, cargo, anoEleicao);
    }

    // A MÁGICA ACONTECE AQUI: Recebendo o arquivo do Postman/Front-end
    @PostMapping("/importar-tse")
    public ResponseEntity<String> importarDadosTse(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: O arquivo enviado está vazio.");
        }

        try {
            String resultado = tseImportService.importarDadosCsv(file);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}