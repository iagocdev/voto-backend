# VotoConsciente — Backend
**Descubra quem os seus votos ajudam a eleger.**

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)

API REST que simula o **Efeito Arrastão** do sistema eleitoral proporcional brasileiro.

##  Sobre o Projeto
No sistema proporcional brasileiro, votar em um candidato pode eleger outro. Esse fenômeno, conhecido como Efeito Arrastão, acontece porque os votos se somam dentro de uma federação ou partido para definir quantas vagas cada grupo conquista — e quem ocupa essas vagas depende da votação individual de cada candidato.

O **VotoConsciente** expõe esse mecanismo de forma clara e acessível: o eleitor informa o número do candidato, o estado e o cargo, e a aplicação retorna todos os outros candidatos da mesma federação que seriam beneficiados por esse voto.

 **Repositório Frontend:** [iagocdev/voto-frontend](https://github.com/iagocdev/voto-frontend)

---

##  Stack Tecnológica

| Camada | Tecnologia / Linguagem |
| :--- | :--- |
| **Linguagem** | Java 21 |
| **Framework** | Spring Boot |
| **Persistência** | Spring Data JPA + Hibernate |
| **Banco de Dados** | PostgreSQL |
| **Build** | Maven |
| **API** | REST (JSON) |

---

##  Arquitetura
O projeto segue uma estrutura em camadas clara e orientada a responsabilidades:

```text
src/
└── main/
    └── java/com/impactosocial/voto/
        ├── controller/     # Endpoints REST (recebe e responde requisições)
        ├── service/        # Regras de negócio (lógica do Efeito Arrastão)
        ├── repository/     # Acesso ao banco de dados via JPA
        ├── model/          # Entidades JPA (Candidato, etc.)
        └── dto/            # Objetos de transferência (ResultadoArrastaoDTO)
```

### Decisões técnicas notáveis

**Pipeline de ingestão de dados (CSV do TSE):** o backend possui um motor de importação em lote capaz de processar e sanitizar os arquivos `.csv` disponibilizados pelo TSE com dados oficiais de candidaturas, persistindo os registros de forma otimizada no PostgreSQL.

**Consulta por federação:** a lógica central do `CandidatoService` identifica a federação do candidato buscado e retorna todos os candidatos elegíveis do mesmo agrupamento eleitoral no mesmo estado, permitindo ao eleitor visualizar o impacto real do seu voto.

---Pipeline de Tratamento e Sanitização de Dados (ETL)

O motor de ingestão em lote localizado no TseImportService atua como um pipeline de ETL (Extract, Transform, Load) robusto, projetado para processar arquivos .csv massivos e potencialmente "sujos" do TSE, garantindo a integridade de quase 30.000 registros consolidados.

A esteira de processamento realiza as seguintes transformações:
1. Ingestão e Fatiamento Dinâmico

O arquivo é lido via BufferedReader com codificação ISO_8859_1. As aspas duplas são removidas e a linha é fatiada utilizando o separador ponto e vírgula:
Java

linha = linha.replace("\"", "");
String[] dados = linha.split(";", -1);

O argumento -1 no método split garante que colunas vazias consecutivas não alterem a estrutura do array.
2. Mapeamento Inteligente de Cabeçalhos

Para evitar acoplamento rígido com a ordem das colunas do arquivo do TSE, a primeira linha constrói um dicionário em memória com a posição indexada de cada atributo, expurgando caracteres invisíveis de formatação (como o BOM do Excel):
Java

String nomeColuna = dados[i].replace("\uFEFF", "").trim().toUpperCase();
colunas.put(nomeColuna, i);

3. Sanitização e Enriquecimento de Dados

Antes da persistência, os dados brutos passam por regras de negócio estéticas e lógicas:

    Padronização de Siglas: Remoção de espaços e conversão forçada para maiúsculas em campos como SG_UF.

    Tratamento de Valores Nulos: O padrão #NULO enviado pelo TSE no campo de federações é traduzido para "Nenhuma".

    Formatação Estética (Capitalize): Cargos em caixa alta (ex: DEPUTADO FEDERAL) são convertidos para o padrão de leitura convencional (Deputado federal).

    Tradução Lógica: Situações de candidatura como APTO são normalizadas para Deferido ou Indeferido.

    Otimização de Booleanos: O resultado final da urna (DS_SIT_TOT_TURNO) é mapeado diretamente para um campo booleano eleito (true/false), reduzindo drasticamente o tamanho de armazenamento e acelerando as consultas indexadas.

4. Dupla Camada de Proteção contra Duplicados

Para suportar cargas massivas sem quebrar a transação ativa ou gerar registros repetidos, o sistema aplica uma barreira dupla:

    Filtro em Memória (HashSet): Cria uma chave única combinando Numero-UF-Cargo para cada linha. Se a chave já existir no arquivo atual, a linha é descartada antes de onerar o banco de dados.

    Filtro de Persistência (existsBy): Consulta o repositório para validar se o candidato já foi inserido em importações anteriores. Uma restrição rígida de unicidade (UniqueConstraint) também está ativa a nível de base de dados na entidade


##  Como Rodar Localmente

### Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ rodando localmente

### 1. Clone o repositório

```bash
git clone https://github.com/iagocdev/voto-backend.git
cd voto-backend
```

### 2. Configure o banco de dados

Crie um banco no PostgreSQL:

```sql
CREATE DATABASE votodb;
```

Configure as credenciais no `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/votodb
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

##  Endpoints da API

### `GET /api/candidatos/impacto`

Retorna o candidato buscado e os candidatos da mesma federação que foram eleitos sendo beneficiados pelo voto.

**Parâmetros de query:**

Endpoints da API
GET /api/candidatos/impacto

Retorna o candidato buscado e os candidatos da mesma federação que foram eleitos sendo beneficiados pelo voto.

Parâmetros de query:
Parâmetro |	Tipo |	Exemplo |	Descrição
numero    | int	 |     1234 |	Número do candidato na urna
estadoUf  |string|	  DF    | Unidade federativa (maiúsculo)
cargo	  |string|Deputado Federal |	Cargo disputado

**Exemplo de requisição:**

```
GET /api/candidatos/impacto?numero=1234&estadoUf=DF&cargo=Deputado%20Federal
```

**Exemplo de resposta (`200 OK`):**

```{
  "candidatoPrincipal": {
    "id": 85787,
    "nomeUrna": "EDUARDO BOLSONARO",
    "numero": 2222,
    "cargo": "Deputado federal",
    "estadoUf": "SP",
    "partido": "PL",
    "federacao": "Nenhuma",
    "situacao": "Deferido",
    "eleito": true
  },
  "beneficiados": [
    {
      "id": 71229,
      "nomeUrna": "PASTOR MARCO FELICIANO",
      "numero": 2270,
      "cargo": "Deputado federal",
      "estadoUf": "SP",
      "partido": "PL",
      "federacao": "Nenhuma",
      "situacao": "Deferido",
      "eleito": true
    }
  ]
}
```

**Resposta de erro (`404 Not Found`):** candidato não encontrado com os parâmetros informados.

---

##  CORS

A API está configurada para aceitar requisições do frontend Angular rodando em `http://localhost:4200` durante o desenvolvimento.

---

##  Licença

Projeto de código aberto para fins educacionais e de portfólio.

---

*Desenvolvido por [Iago](https://github.com/iagocdev) — Java & Spring Boot*
