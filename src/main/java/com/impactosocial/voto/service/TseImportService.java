package com.impactosocial.voto.service;

import com.impactosocial.voto.model.Candidato;
import com.impactosocial.voto.repository.CandidatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TseImportService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Transactional
    public String importarDadosCsv(MultipartFile arquivoTse) {
        List<Candidato> candidatosParaSalvar = new ArrayList<>();

        // Este mapa vai guardar o nome da coluna e a posição exata dela no arquivo
        Map<String, Integer> colunas = new HashMap<>();

        // Este Set impede a duplicação de candidatos DENTRO do mesmo arquivo CSV
        Set<String> chavesNoArquivo = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                arquivoTse.getInputStream(), StandardCharsets.ISO_8859_1))) {

            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                linha = linha.replace("\"", "");
                String[] dados = linha.split(";", -1);

                // O MAPEADOR INTELIGENTE
                if (primeiraLinha) {
                    for (int i = 0; i < dados.length; i++) {
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

                        // Lê os dados usando o NOME da coluna
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
                            candidato.setFederacao("Nenhuma");
                        }

                        if (colunas.containsKey("DS_CARGO")) {
                            String cargoBruto = dados[colunas.get("DS_CARGO")].trim().toLowerCase();
                            if (!cargoBruto.isEmpty()) {
                                String cargoFormatado = cargoBruto.substring(0, 1).toUpperCase() + cargoBruto.substring(1);

                                candidato.setCargo(cargoFormatado);
                            }
                        }

                        if (colunas.containsKey("DS_SITUACAO_CANDIDATURA")) {
                            String proxySituacao = dados[colunas.get("DS_SITUACAO_CANDIDATURA")].trim().toUpperCase();
                            candidato.setSituacao(proxySituacao.equals("APTO") ? "Deferido" : "Indeferido");
                        }

                        if (colunas.containsKey("DS_SIT_TOT_TURNO")) {
                            String situacaoTurno = dados[colunas.get("DS_SIT_TOT_TURNO")].trim().toUpperCase();
                            candidato.setEleito(situacaoTurno.startsWith("ELEITO"));
                        } else {
                            candidato.setEleito(false);
                        }

                        // === AQUI ENTRA A DUPLA PROTEÇÃO BLINDADA ===

                        // 1. Gera uma chave identificadora única para a linha atual
                        String chaveUnica = candidato.getNumero() + "-" + candidato.getEstadoUf() + "-" + candidato.getCargo().toUpperCase();

                        // 2. Se essa chave já foi vista neste arquivo, pula imediatamente
                        if (chavesNoArquivo.contains(chaveUnica)) {
                            System.out.println("⚠️ Ignorando duplicata interna do arquivo: " + chaveUnica);
                            continue;
                        }

                        // 3. Se não for duplicado no arquivo, checa se já existe salvo no Banco de Dados
                        boolean candidatoJaExisteNoBanco = candidatoRepository.existsByNumeroAndEstadoUfIgnoreCaseAndCargoIgnoreCase(
                                candidato.getNumero(),
                                candidato.getEstadoUf(),
                                candidato.getCargo()
                        );

                        if (!candidatoJaExisteNoBanco) {
                            // Adiciona ao controle de memória e inclui na lista de salvamento
                            chavesNoArquivo.add(chaveUnica);
                            candidatosParaSalvar.add(candidato);
                        } else {
                            System.out.println("🚫 Candidato já existente no banco de dados: " + chaveUnica);
                        }

                    } catch (Exception ex) {
                        // Se uma linha isolada estiver corrompida, pula e vai para a próxima
                        continue;
                    }
                }
            }

            // Salva apenas os registros que sobreviveram aos dois filtros
            if (!candidatosParaSalvar.isEmpty()) {
                candidatoRepository.saveAll(candidatosParaSalvar);
            }

            return "Carga massiva INTELIGENTE concluída! " + candidatosParaSalvar.size() + " novos candidatos reais importados com sucesso.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Falha crítica na ingestão de dados: " + e.getMessage();
        }
    }
}