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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TseImportService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Transactional
    public String importarDadosCsv() {
        // Limpa a tabela antes da nova carga
        candidatoRepository.deleteAll();

        List<Candidato> candidatosParaSalvar = new ArrayList<>();

        // Este mapa vai guardar o nome da coluna e a posição exata dela no arquivo
        Map<String, Integer> colunas = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("candidatos_brutosDF.csv").getInputStream(), StandardCharsets.ISO_8859_1))) {

            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                linha = linha.replace("\"", "");
                String[] dados = linha.split(";", -1);

                // O MAPEDADOR INTELIGENTE
                if (primeiraLinha) {
                    for (int i = 0; i < dados.length; i++) {
                        // Limpa o nome da coluna (remove espaços e caracteres invisíveis como o BOM do Excel)
                        String nomeColuna = dados[i].replace("\uFEFF", "").trim().toUpperCase();
                        colunas.put(nomeColuna, i);
                    }
                    primeiraLinha = false;
                    continue;
                }

                // Evita quebra caso o arquivo tenha linhas em branco no final
                if (dados.length > 10) {
                    try {
                        Candidato candidato = new Candidato();

                        // Lê os dados usando o NOME da coluna, não importa em qual posição ela esteja
                        candidato.setNumero(Integer.parseInt(dados[colunas.get("NR_CANDIDATO")].trim()));
                        candidato.setNomeUrna(dados[colunas.get("NM_URNA_CANDIDATO")].trim());
                        candidato.setEstadoUf(dados[colunas.get("SG_UF")].trim().toUpperCase());

                        if (colunas.containsKey("SG_PARTIDO")) {
                            candidato.setPartido(dados[colunas.get("SG_PARTIDO")].trim());
                        }

                        if (colunas.containsKey("NM_FEDERACAO")) {
                            String federacao = dados[colunas.get("NM_FEDERACAO")].trim();
                            candidato.setFederacao(federacao.equals("#NULO") ? "Nenhuma" : federacao);
                        } else {
                            candidato.setFederacao("Nenhuma"); // Segurança caso a coluna não exista
                        }

                        if (colunas.containsKey("DS_CARGO")) {
                            String cargoBruto = dados[colunas.get("DS_CARGO")].trim().toLowerCase();
                            if (!cargoBruto.isEmpty()) {
                                String cargoFormatado = cargoBruto.substring(0, 1).toUpperCase() + cargoBruto.substring(1);
                                candidato.setCargo(cargoFormatado);
                            }
                        }

                        if (colunas.containsKey("DS_SITUACAO_CANDIDATURA")) {
                            String situacao = dados[colunas.get("DS_SITUACAO_CANDIDATURA")].trim().toUpperCase();
                            candidato.setSituacao(situacao.equals("APTO") ? "Deferido" : "Indeferido");
                        }

                        // A GLÓRIA: Resultado real da urna
                        if (colunas.containsKey("DS_SIT_TOT_TURNO")) {
                            String situacaoTurno = dados[colunas.get("DS_SIT_TOT_TURNO")].trim().toUpperCase();
                            candidato.setEleito(situacaoTurno.startsWith("ELEITO"));
                        } else {
                            candidato.setEleito(false);
                        }

                        candidatosParaSalvar.add(candidato);

                    } catch (Exception ex) {
                        // Se uma linha isolada estiver corrompida, ele pula e continua salvando o resto
                        continue;
                    }
                }
            }

            candidatoRepository.saveAll(candidatosParaSalvar);

            return "Carga massiva INTELIGENTE concluída! " + candidatosParaSalvar.size() + " candidatos reais importados com sucesso.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Falha crítica na ingestão de dados: " + e.getMessage();
        }
    }
}