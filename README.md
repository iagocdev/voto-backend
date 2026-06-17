# Simulador de Efeito Arrastão( voto indireto) 🗳️

Um sistema Full-Stack desenvolvido para simular e demonstrar o funcionamento do sistema eleitoral proporcional brasileiro (o chamado "Efeito Arrastão"). A aplicação permite consultar candidatos e entender como um voto individual pode ajudar a eleger outros candidatos da mesma federação ou partido.

## 🚀 Tecnologias Utilizadas

**Back-end:** https://github.com/iagocdev/voto-backend
* Java
* Spring Boot
* PostgreSQL
* Maven

**Front-end:** https://github.com/iagocdev/voto-frontend
* Angular
* TypeScript
* PWA (Progressive Web App) - *Instalável nativamente em Mobile e Desktop*
* HTML5 / CSS3

##  Arquitetura e Funcionalidades

O projeto foi construído com foco em boas práticas de engenharia de software e performance:

* **Consulta Síncrona Rigorosa:** Comunicação fluida entre a interface Angular e a API REST em Spring Boot para cálculo em tempo real do destino dos votos.
* **Pipeline de Ingestão de Dados (Data Ingestion):** Motor customizado no back-end para processamento em lote (Batch Processing) e sanitização de arquivos `.csv` gigantescos contendo os dados oficiais abertos do TSE, persistindo os registros de forma otimizada no PostgreSQL.
* **Controle de Renderização Avançado:** Utilização do `ChangeDetectorRef` no Angular para garantir a sincronia exata da interface do usuário com a resolução de requisições assíncronas complexas.
* **PWA Instalável:** O sistema front-end foi empacotado com Service Workers e um Web Manifest customizado, garantindo uma experiência de aplicativo nativo para o usuário final, com tempos de carregamento reduzidos (caching).

## Como Executar o Projeto Localmente

### Pré-requisitos
* Java 17+
* Node.js (v18+) e Angular CLI
* PostgreSQL rodando localmente

### 1. Configurando o Back-end (Spring Boot)
1. Crie um banco de dados no PostgreSQL (verifique o nome e as credenciais no arquivo `application.properties`).
2. Abra o projeto na sua IDE Java favorita.
3. Execute a classe principal da aplicação para levantar o servidor na porta padrão (`8080`).

### 2. Configurando o Front-end (Angular)
1. Abra o terminal na pasta do front-end.
2. Instale as dependências:
   ```bash
   npm install
