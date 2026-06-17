package com.impactosocial.voto.service;

import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.repository.CandidatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TseImportService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Transactional
    public String importarDadosCsv() {
        // Limpa a tabela antes da nova carga para evitar duplicados
        candidatoRepository.deleteAll();

        List<Candidato> candidatosParaSalvar = new ArrayList<>();

        // O TSE utiliza a codificação ISO-8859-1 (Latin1) nos arquivos de consulta
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("candidatos_brutosDF.csv").getInputStream(), StandardCharsets.ISO_8859_1))) {

            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                // Pula o cabeçalho com os nomes das colunas
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                // O TSE envolve os campos com aspas duplas, vamos removê-las para limpar o texto
                linha = linha.replace("\"", "");

                // Divide a linha pelo ponto e vírgula
                String[] dados = linha.split(";");

                // Garante que a linha possui as colunas necessárias antes de processar
                if (dados.length > 30) {
                    Candidato candidato = new Candidato();

                    // Mapeamento baseado nos índices oficiais do Portal de Dados Abertos
                    candidato.setNumero(Integer.parseInt(dados[16].trim()));
                    candidato.setNomeUrna(dados[18].trim());
                    candidato.setEstadoUf(dados[10].trim().toUpperCase());
                    candidato.setPartido(dados[26].trim());

                    // Tratamento para Federações (substitui o #NULO do governo por algo limpo)
                    String federacao = dados[29].trim();
                    candidato.setFederacao(federacao.equals("#NULO") ? "Nenhuma" : federacao);

                    // Padronização do formato do Cargo (Ex: DEPUTADO FEDERAL -> Deputado federal)
                    String cargoBruto = dados[14].trim().toLowerCase();
                    if (!cargoBruto.isEmpty()) {
                        String cargoFormatado = cargoBruto.substring(0, 1).toUpperCase() + cargoBruto.substring(1);
                        candidato.setCargo(cargoFormatado);
                    }

                    // Mapeamento simplificado da situação da candidatura
                    String situacao = dados[23].trim().toUpperCase();
                    candidato.setSituacao(situacao.equals("APTO") ? "Deferido" : "Indeferido");

                    candidatosParaSalvar.add(candidato);
                }
            }

            // Persiste todos os candidatos no PostgreSQL em um único lote
            candidatoRepository.saveAll(candidatosParaSalvar);

            return "Carga massiva concluída! " + candidatosParaSalvar.size() + " candidatos reais importados diretamente do TSE.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Falha crítica na ingestão de dados: " + e.getMessage();
        }
    }
}