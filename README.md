#  VotoConsciente — Backend

> **Descubra quem os seus votos ajudam a eleger.**
> API REST que simula o Efeito Arrastão do sistema eleitoral proporcional brasileiro.

---

## Sobre o Projeto

No sistema proporcional brasileiro, votar em um candidato pode eleger outro. Esse fenômeno, conhecido como **Efeito Arrastão**, acontece porque os votos se somam dentro de uma federação ou partido para definir quantas vagas cada grupo conquista — e quem ocupa essas vagas depende da votação individual de cada candidato.

O **VotoConsciente** expõe esse mecanismo de forma clara e acessível: o eleitor informa o número do candidato, o estado e o cargo, e a aplicação retorna todos os outros candidatos da mesma federação que seriam beneficiados por esse voto.

**Repositório frontend:** [iagocdev/voto-frontend](https://github.com/iagocdev/voto-frontend)

---

##  Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de Dados | PostgreSQL |
| Build | Maven |
| API | REST (JSON) |

---

##  Arquitetura

O projeto segue uma estrutura em camadas clara e orientada a responsabilidades:

```
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

---

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

| Parâmetro | Tipo | Exemplo | Descrição |
|---|---|---|---|
| `numero` | `int` | `1234` | Número do candidato na urna |
| `estadoUf` | `string` | `DF` | Unidade federativa (maiúsculo) |
| `cargo` | `string` | `Deputado Federal` | Cargo disputado |

**Exemplo de requisição:**

```
GET /api/candidatos/impacto?numero=1234&estadoUf=DF&cargo=Deputado Federal
```

**Exemplo de resposta (`200 OK`):**

```json
{
  "candidatoPrincipal": {
    "id": 2,
    "nomeUrna": "Professor João",
    "numero": 1234,
    "cargo": "Deputado Federal",
    "estadoUf": "DF",
    "partido": "PE",
    "federacao": "Federação Brasil da Esperança",
    "situacao": "DEFERIDO"
  },
  "beneficiados": [
    {
      "id": 3,
      "nomeUrna": "Professora Maria",
      "numero": 5678,
      "cargo": "Deputado Federal",
      "estadoUf": "DF",
      "partido": "PT",
      "federacao": "Federação Brasil da Esperança",
      "situacao": "DEFERIDO"
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
