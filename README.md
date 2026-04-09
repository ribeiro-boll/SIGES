<img width="456" height="290" alt="telaInicial" src="https://github.com/user-attachments/assets/3938b09e-c876-4398-9ff2-64492bc43aad" />


# (SIGES) Sistema Integrado de Gestão de Serviços 

O **SIGES** é uma aplicação fullstack para **gestão de serviços, insumos, precificação e histórico de vendas**, com foco em profissionais e pequenos negócios que trabalham com prestação de serviços recorrentes, por exemplo, salão, barbearia, estética e atendimento individual.

O projeto centraliza:
- cadastro e autenticação de usuários
- cadastro de **insumos variáveis**
- cadastro e atualização de **custos fixos mensais**
- registro de **serviços realizados**
- cálculo de **despesas, preço sugerido e lucro líquido**
- emissão de **extrato mensal em PDF**
- frontend servido pelo próprio backend Spring Boot

---

## Índice

- [Visão geral](#visão-geral)
- [Objetivo do projeto](#objetivo-do-projeto)
- [Arquitetura geral](#arquitetura-geral)
  - [1. Backend](#1-backend)
  - [2. Frontend](#2-frontend)
  - [3. Persistência](#3-persistência)
  - [4. Relatórios em PDF](#4-relatórios-em-pdf)
- [Principais funcionalidades](#principais-funcionalidades)
- [Regras de negócio centrais](#regras-de-negócio-centrais)
- [Fluxo da aplicação](#fluxo-da-aplicação)
- [Telas e rotas do frontend](#telas-e-rotas-do-frontend)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Configuração e execução](#configuração-e-execução)
  - [Opção 1 — Docker Compose](#opção-1--docker-compose)
  - [Opção 2 — Execução local com perfil `test` e H2](#opção-2--execução-local-com-perfil-test-e-h2)
  - [Opção 3 — Execução local com PostgreSQL](#opção-3--execução-local-com-postgresql)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Banco de dados e perfis](#banco-de-dados-e-perfis)
- [Endpoints implementados](#endpoints-implementados)
  - [1. Usuário](#1-usuário)
  - [2. Insumos variáveis](#2-insumos-variáveis)
  - [3. Insumos fixos](#3-insumos-fixos)
  - [4. Serviços](#4-serviços)
  - [5. Métricas e relatórios](#5-métricas-e-relatórios)
- [Exemplos de payload](#exemplos-de-payload)
- [Testes automatizados](#testes-automatizados)
- [CI/CD e containerização](#cicd-e-containerização)
- [Observações importantes de implementação](#observações-importantes-de-implementação)
- [Melhorias futuras](#melhorias-futuras)
- [Galeria](#galeria)

---

## Visão geral

O SIGES foi estruturado como um **monólito fullstack**:
- o **backend** expõe endpoints REST com Spring Boot
- o **frontend** consome esses endpoints e é servido pelo próprio servidor
- a **persistência** usa JPA/Hibernate
- o **relatório mensal** é renderizado com Thymeleaf e convertido em PDF

Na prática, o sistema ajuda o usuário a responder perguntas como:
- quanto custa executar cada serviço
- qual seria o preço sugerido com base em meta de renda e margem de lucro
- quanto sobrou de lucro líquido por serviço
- qual foi o desempenho consolidado do mês

---

## Objetivo do projeto

O objetivo do SIGES é funcionar como:
- sistema de apoio à **precificação de serviços**
- ferramenta de **controle de custos variáveis e fixos**
- registro histórico de serviços realizados
- base para geração de **extratos gerenciais mensais**
- projeto fullstack de portfólio com backend Java/Spring, autenticação JWT, persistência relacional, frontend web e Docker

---

## Arquitetura geral

### 1. Backend

Aplicação principal em **Java + Spring Boot**, responsável por:
- autenticação e emissão de JWT
- cadastro e atualização de usuário
- cadastro de insumos variáveis e fixos
- registro e remoção de serviços
- cálculo de preço sugerido, despesas e lucro líquido
- listagem paginada de serviços e insumos
- geração de PDF mensal
- entrega do frontend compilado

Pacotes principais:
- `com.bolota.historicodevendas.Controller`
- `com.bolota.historicodevendas.Entities`
- `com.bolota.historicodevendas.Resource`
- `com.bolota.historicodevendas.Security`
- `com.bolota.historicodevendas.Service`

---

### 2. Frontend

O frontend está **embutido no backend** e é servido pelo Spring por meio de:
- `src/main/resources/static/`
- `src/main/resources/templates/`

Rotas e telas identificadas no bundle atual:
- `/login`
- `/cadastro`
- `/painel`
- `/servicos`
- `/servicos/novo`
- `/insumos-variaveis`
- `/insumos-variaveis/novo`
- `/insumos-fixos`
- `/insumos-fixos/novo`
- `/extrato`
- `/perfil`

> Observação importante: neste snapshot do projeto, o repositório contém o **build do frontend** já compilado. O código-fonte do frontend (componentes React/TSX, por exemplo) **não aparece neste ZIP**; o que está presente são os assets finais servidos pelo backend.

---

### 3. Persistência

A persistência é feita com **Spring Data JPA**.

Perfis encontrados:
- **padrão (`application.yaml`)**: PostgreSQL
- **teste (`application-test.yaml`)**: H2 em memória

Entidades persistidas:
- `UserEntity`
- `SuppliesEntityPersistent`
- `FixedSuppliesEntityPersistent`
- `ServiceEntityPersistent`

---

### 4. Relatórios em PDF

O sistema gera um **extrato mensal em PDF** a partir de:
- um template Thymeleaf: `monthly_report_template_compact.html`
- renderização HTML
- conversão para PDF via `openhtmltopdf-pdfbox`

O relatório consolidado inclui:
- mês e ano
- período do relatório
- total bruto
- lucro total
- ticket médio
- total de serviços
- lista dos serviços do mês com dados financeiros resumidos

---

## Principais funcionalidades

- autenticação de usuário com JWT
- cadastro de usuário já retornando token válido
- edição dos dados profissionais do usuário
- cadastro de insumos variáveis com cálculo automático de custo por medida
- cadastro de custos fixos mensais com cálculo automático de custo por minuto
- atualização de insumos fixos já cadastrados
- bloqueio de remoção de insumos que estejam em uso por serviços
- cadastro de serviços com vínculo a insumos variáveis e/ou fixos
- cálculo de preço sugerido com base em renda desejada, carga horária e margem de lucro
- cálculo de lucro líquido por serviço
- métricas paginadas de serviços e insumos
- extrato mensal em PDF
- frontend servido pelo próprio Spring Boot
- Dockerfile + Compose para execução com PostgreSQL
- GitHub Actions para testes e build/push de imagem

---

## Regras de negócio centrais

### Usuário

Cada usuário possui:
- `login`
- `passwordHash`
- `desiredMonthlyIncome`
- `daysWorkingWeekly`
- `hoursWorkingDaily`
- `profitMargin`
- listas de UUIDs de serviços e insumos associados

As informações profissionais do usuário alimentam a lógica de precificação.

---

### Insumos variáveis

Representam itens consumidos por quantidade medida, como:
- shampoo
- creme
- produto químico
- material descartável

Campos principais:
- nome
- descrição
- valor do produto
- medida total
- custo por medida (`productValue / measure`)

---

### Insumos fixos

Representam custos mensais distribuídos no tempo, como:
- aluguel
- energia
- água
- internet

Campos principais:
- nome
- descrição
- valor mensal total
- data de referência
- custo por minuto
- contador de uso em serviços
- flag de popup/atualização mensal

O custo por minuto é calculado por:

```text
supplyTotalCost / (diasDoMês * 24 * 60)
```

---

### Serviços

Ao registrar um serviço, o sistema calcula:
- despesas com insumos variáveis
- despesas com custos fixos proporcionalmente ao tempo do serviço
- custo do tempo de trabalho do usuário
- preço sugerido com margem
- lucro líquido final

Fórmulas implementadas:

```text
minutesWorking = hoursWorkingDaily * 60
monthlyWorkingMinutes = daysWorkingWeekly * 4.33 * minutesWorking
costPerMinute = desiredMonthlyIncome / monthlyWorkingMinutes

serviceExpenses = soma(custoPorMedidaDoInsumo * quantidadeUsada) + soma(custoPorMinutoDoFixo * duraçãoDoServiço)

suggestedPrice = (serviceExpenses + duração * costPerMinute) + ((serviceExpenses + duração * costPerMinute) * profitMargin / 100)

finalProfit = salePrice - (serviceExpenses + duração * costPerMinute)
```

Além disso:
- um serviço precisa ter **ao menos um insumo variável ou um insumo fixo**
- se um insumo é usado por um serviço, seu `counterInUseByServices` é incrementado
- ao remover o serviço, esses contadores são decrementados

---

## Fluxo da aplicação

1. O usuário cria a conta em `/user/register`
2. O backend retorna um JWT já autenticado
3. O usuário acessa o painel e cadastra insumos variáveis e/ou fixos
4. O usuário registra serviços informando duração, preço e insumos utilizados
5. O backend calcula preço sugerido, despesas e lucro líquido
6. O usuário consulta listagens paginadas de serviços e insumos em `/metrics/*`
7. O usuário pode atualizar seus dados profissionais em `/user/update`
8. O usuário pode gerar um PDF consolidado do mês em `/metrics/download_pdf`

---

## Telas e rotas do frontend

### Públicas
- `/`
- `/login`
- `/cadastro`

### Privadas
- `/painel` — visão geral do sistema
- `/servicos` — listagem de serviços registrados
- `/servicos/novo` — cadastro de novo serviço
- `/insumos-variaveis` — listagem de insumos variáveis
- `/insumos-variaveis/novo` — cadastro de insumo variável
- `/insumos-fixos` — listagem de insumos fixos
- `/insumos-fixos/novo` — cadastro de insumo fixo
- `/extrato` — geração do extrato mensal em PDF
- `/perfil` — atualização de meta de renda, dias/semana, horas/dia e margem de lucro

---

## Tecnologias utilizadas

### Backend
- Java
- Spring Boot `4.0.5`
- Spring Web MVC
- Spring Security
- OAuth2 Resource Server (JWT)
- Spring Data JPA
- JDBC
- Thymeleaf
- Lombok

### Banco de dados
- PostgreSQL
- H2 (perfil de teste)

### Relatórios
- OpenHTMLtoPDF (`openhtmltopdf-pdfbox`)

### Frontend servido pelo backend
- assets compilados em `resources/static` e `resources/templates`

### DevOps
- Docker
- Docker Compose
- GitHub Actions
- GHCR (GitHub Container Registry)

---

## Estrutura do projeto

```text
SIGES/
├── .github/workflows/
│   ├── docker.yaml
│   └── setup-java.yml
├── .mvn/
├── Dockerfile
├── compose.yaml
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/bolota/historicodevendas/
│   │   │   ├── Controller/
│   │   │   ├── Entities/
│   │   │   │   ├── DTO/
│   │   │   │   └── PersistentEntities/
│   │   │   ├── Resource/
│   │   │   ├── Security/
│   │   │   ├── Service/
│   │   │   └── HistoricoDeVendasApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-test.yaml
│   │       ├── static/
│   │       └── templates/
│   └── test/java/com/bolota/historicodevendas/
│       ├── FullTest/
│       ├── SuppliesTest/
│       └── UserTest/
└── mvnw
```

---

## Configuração e execução

### Pré-requisitos recomendados

Para este snapshot do projeto, a configuração mais segura é usar:
- **Java 21**
- Docker + Docker Compose

---

### Opção 1 — Docker Compose

Sobe a aplicação com PostgreSQL:

```bash
docker compose up --build
```

A aplicação ficará disponível em rede LAN, com o sistema printando a URL de acesso

Configuração usada no `compose.yaml`:
- banco: `postgres:16`
- database: `siges`
- usuário: `postgres`
- senha: `postgres`
- porta da aplicação: `8080`

---

### Opção 2 — Execução local com perfil `test` e H2

Essa é a forma mais simples de rodar localmente sem PostgreSQL.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

ou

```bash
SPRING_PROFILES_ACTIVE=test ./mvnw spring-boot:run
```

Nesse perfil:
- o banco é H2 em memória
- o console H2 fica disponível em `/h2-console`
- a aplicação continua na porta `8080`

---

### Opção 3 — Execução local com PostgreSQL

Se quiser rodar fora do Docker, ajuste as variáveis do datasource ou sobrescreva o `application.yaml`.

Exemplo:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/siges \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./mvnw spring-boot:run
```

> O `application.yaml` padrão aponta para `jdbc:postgresql://db:5432/siges`, ou seja, espera o hostname `db`, que é exatamente o serviço definido no Compose.

---

## Autenticação e segurança

A segurança é configurada em `EndpointSecurity` e `JwtConfig`.

### Rotas públicas
- `/`
- `/login`
- `/cadastro`
- `/user/login`
- `/user/register`
- `/assets/**`
- `/favicon.ico`
- `/h2-console/**`

### Rotas protegidas
Qualquer outra rota exige autenticação JWT.

### JWT
- algoritmo: **HS256**
- subject: login do usuário
- claim `roles`: `USER`
- expiração: **3 horas**

Exemplo de header:

```http
Authorization: Bearer <token>
```

### Senhas
As senhas são armazenadas com **BCrypt**.

---

## Banco de dados e perfis

### Perfil padrão
Arquivo: `src/main/resources/application.yaml`

- driver: PostgreSQL
- URL: `jdbc:postgresql://db:5432/siges`
- porta da app: `8080`
- bind: `0.0.0.0`

### Perfil de teste
Arquivo: `src/main/resources/application-test.yaml`

- driver: H2
- URL: `jdbc:h2:mem:testdb`
- console H2 habilitado
- ideal para testes e execução rápida local

---

## Endpoints implementados

## 1. Usuário

Base: `/user`

### `POST /user/register`
Cria um usuário e já retorna um JWT.

Validações principais:
- login e senha não podem estar vazios
- `daysWorkingWeekly` não pode ser `0` e não pode ser maior que `7`
- `hoursWorkingDaily` não pode ser `0` e não pode ser maior que `20`
- `desiredMonthlyIncome` não pode ser `0`
- login duplicado retorna `409`

Respostas comuns:
- `200` token JWT
- `400` payload inválido
- `406` valores profissionais inválidos
- `409` login já existente

---

### `POST /user/login`
Autentica o usuário e retorna um JWT.

Body esperado:
- `login`
- `password`

Respostas comuns:
- `200` token JWT
- `400` payload inválido
- `404` usuário não encontrado
- `401` senha incorreta

---

### `PATCH /user/update`
Atualiza:
- renda mensal desejada
- dias por semana
- horas por dia
- margem de lucro

Exige JWT.

Respostas comuns:
- `200` atualizado com sucesso
- `400` payload inválido
- `401` não autenticado
- `406` valores fora das regras mínimas

---

## 2. Insumos variáveis

Base: `/supplies`

### `POST /supplies/register`
Cadastra insumo variável.

Campos principais:
- `name`
- `description`
- `productValue`
- `measure`

Regras:
- valor e medida devem ser maiores que zero
- nome não pode ser vazio
- ao cadastrar, o UUID é retornado no body

Respostas comuns:
- `200` UUID do insumo
- `400` payload inválido
- `401` não autenticado

---

### `DELETE /supplies/remove`
Remove insumo variável.

> O corpo da requisição é a **string do UUID**, não um JSON estruturado.

Regras:
- se o insumo não existir, retorna `409`
- se estiver em uso por algum serviço, retorna `406`
- se o insumo não pertencer ao usuário autenticado, retorna `401`

Respostas comuns:
- `200` removido
- `401` não autenticado / sem posse
- `406` insumo em uso
- `409` UUID inexistente

---

## 3. Insumos fixos

Base: `/supplies`

### `POST /supplies/register_fixed`
Cadastra custo fixo mensal.

Campos principais:
- `name`
- `description`
- `suppliesValue`

Na criação, o sistema também define:
- `fixedSupplyDate = hoje`
- `counterInUseByServices = 0`
- `condUpdatePopup = true`
- `costPerMinute` calculado automaticamente

Respostas comuns:
- `200` UUID do insumo fixo
- `400` payload inválido
- `401` não autenticado

---

### `PATCH /supplies/edit_fixedSupply`
Atualiza um custo fixo já existente.

Payload esperado: `FixedSuppliesEntityPersistent` contendo pelo menos:
- `UUID`
- `name`
- `description`
- `supplyTotalCost`
- `fixedSupplyDate`
- `counterInUseByServices`
- `condUpdatePopup`

Após a atualização, o backend recalcula `costPerMinute`.

Respostas comuns:
- `200` atualizado
- `400` payload inválido
- `401` não autenticado
- `403` insumo não pertence ao usuário
- `404` UUID inexistente

---

### `DELETE /supplies/remove_fixed`
Remove insumo fixo.

> O corpo da requisição também é a **string do UUID**.

Regras:
- se não existir, retorna `409`
- se estiver em uso por algum serviço, retorna `406`
- se não pertencer ao usuário autenticado, retorna `401`

Respostas comuns:
- `200` removido
- `401` não autenticado / sem posse
- `406` insumo em uso
- `409` UUID inexistente

---

## 4. Serviços

Base: `/product`

> Observação: apesar do path ser `/product`, o recurso modelado aqui é **serviço**. Esse nome aparece como herança de nomenclatura do projeto e foi mantido na API atual.

### `POST /product/register`
Registra um serviço realizado.

Campos principais:
- `name`
- `description`
- `serviceType`
- `category`
- `quantity`
- `averageServiceDurationMinutes`
- `salePrice`
- `variableSuppliesUsedUUID`
- `variableSuppliesQuantityUsed`
- `fixedSuppliesUsedUUID`
- `serviceNotes`

Regras:
- payload não pode estar nulo
- campos essenciais não podem estar vazios
- quantidade, duração e preço devem ser maiores que zero
- os UUIDs de insumos informados precisam existir
- cada insumo variável usado precisa ter quantidade maior que zero
- é obrigatório ter pelo menos um insumo variável ou fixo
- ao registrar, o backend incrementa o contador de uso dos insumos envolvidos

Retorno:
- `200` com UUID do serviço criado

Erros comuns:
- `400` payload inválido ou insumo inexistente
- `401` não autenticado
- `406` quantidade de insumo inválida

---

### `DELETE /product/remove`
Remove um serviço.

> O corpo da requisição é a **string do UUID do serviço**.

Ao remover:
- o serviço sai da lista do usuário
- os contadores de uso dos insumos vinculados são decrementados
- o registro do serviço é removido do banco

Respostas comuns:
- `200` removido
- `401` não autenticado
- `404` serviço não encontrado

---

## 5. Métricas e relatórios

Base: `/metrics`

### `GET /metrics/services`
Retorna lista paginada de serviços do usuário autenticado.

- paginação padrão: `size = 10`
- retorno: `Page<ServiceEntityPersistent>`

Resposta comum:
- `200` página de serviços
- `401` não autenticado

---

### `GET /metrics/supplies`
Retorna lista paginada de insumos variáveis do usuário.

- paginação padrão: `size = 10`
- retorno: `Page<SuppliesEntityPersistent>`

---

### `GET /metrics/supplies_fixed`
Retorna lista paginada de insumos fixos do usuário.

- paginação padrão: `size = 10`
- retorno: `Page<FixedSuppliesEntityPersistent>`

---

### `GET /metrics/download_pdf`
Gera e retorna o extrato mensal em PDF.

Query params obrigatórios:
- `month`
- `year`

Exemplo:

```text
/metrics/download_pdf?month=4&year=2026
```

Regras:
- se `month` ou `year` forem zero, retorna `400`
- se o usuário não estiver autenticado, retorna `401`
- se não houver serviços no mês, retorna `404`

Resposta de sucesso:
- `200`
- `Content-Type: application/pdf`
- `Content-Disposition: attachment; filename="extrato-mensal-YYYY-MM.pdf"`

---

## Exemplos de payload

### Registro de usuário

```json
{
  "login": "tester_123",
  "passwordHash": "123456",
  "desiredMonthlyIncome": 6500.0,
  "daysWorkingWeekly": 5,
  "hoursWorkingDaily": 8.0,
  "profitMargin": 120.0
}
```

### Login

```json
{
  "login": "tester_123",
  "password": "123456"
}
```

### Cadastro de insumo variável

```json
{
  "name": "Shampoo profissional",
  "description": "Shampoo de limpeza suave para uso em lavatório",
  "productValue": 49.9,
  "measure": 1.0
}
```

### Cadastro de insumo fixo

```json
{
  "name": "Energia elétrica",
  "description": "Conta mensal de energia do salão",
  "suppliesValue": 320.0
}
```

### Cadastro de serviço

```json
{
  "name": "Corte feminino com escova",
  "description": "Atendimento completo com corte, lavagem e finalização escovada.",
  "serviceType": "SERVICE",
  "category": "Cabelo",
  "quantity": 1,
  "averageServiceDurationMinutes": 75,
  "salePrice": 120.0,
  "variableSuppliesUsedUUID": [
    "uuid-var-1",
    "uuid-var-2"
  ],
  "variableSuppliesQuantityUsed": {
    "uuid-var-1": 0.10,
    "uuid-var-2": 0.08
  },
  "fixedSuppliesUsedUUID": [
    "uuid-fix-1",
    "uuid-fix-2"
  ],
  "serviceNotes": "Cliente pediu finalização mais alinhada nas pontas."
}
```

---

## Testes automatizados

O projeto **tem testes automatizados**, organizados por pasta dentro de `src/test/java`.

Arquivos identificados:
- `FullTest/AllControllersLifeCycleFullTest.java`
- `SuppliesTest/SuppliesControllerFullTest.java`
- `UserTest/UserControllerFullTest.java`

### Cobertura identificada por inspeção do código

#### `AllControllersLifeCycleFullTest`
Valida um fluxo completo de uso:
- registro de usuário
- cadastro de insumo variável
- cadastro de insumo fixo
- cadastro de serviço
- consulta de métricas
- geração de PDF
- remoção de serviço
- remoção de insumo fixo
- remoção de insumo variável

#### `SuppliesControllerFullTest`
Valida:
- criação e remoção de insumo variável
- criação, atualização e remoção de insumo fixo
- bloqueio de remoção de insumo fixo quando `counterInUseByServices != 0`

#### `UserControllerFullTest`
Valida:
- registro de usuário
- login
- atualização do perfil profissional

### Como executar

```bash
./mvnw test
```

> Neste ambiente de auditoria, a execução automática do Maven Wrapper não pôde ser concluída porque o wrapper tenta baixar o binário do Maven da internet. Ainda assim, os testes existem no projeto, estão organizados em `src/test/java` e o escopo deles foi auditado diretamente no código.

---

## CI/CD e containerização

### Dockerfile
O projeto possui Dockerfile multi-stage:
- stage de build com Maven + Temurin 21
- stage final com `eclipse-temurin:21-jre`
- exposição da porta `8080`

### Compose
O arquivo `compose.yaml` sobe:
- aplicação Spring Boot
- banco PostgreSQL 16

### GitHub Actions
Foram encontrados dois workflows:

#### `setup-java.yml`
Executa:
- checkout
- setup Java 17
- `./mvnw test`
- `./mvnw package -DskipTests`

#### `docker.yaml`
Executa no push para `main`:
- checkout
- setup Java 21
- build do jar
- login no GHCR
- build e push da imagem `ghcr.io/ribeiro-boll/siges:latest`

---

## Observações importantes de implementação

- O path `/product/*` representa operações de **serviço**, não de produto físico.
- O frontend é servido pelo backend e aparece no repositório como **bundle compilado**, não como código-fonte bruto.
- O endpoint de download mensal gera um **PDF compacto**, não uma planilha detalhada.
- Os endpoints de remoção recebem o UUID como **string no corpo da requisição**, não como objeto JSON.
- A chave JWT está atualmente definida diretamente em `JwtConfig`. Para produção, o ideal é mover isso para variável de ambiente ou secret manager.
- O cálculo de dias do mês em `FixedSuppliesEntityPersistent` trata fevereiro como `28` dias.
- O perfil padrão da aplicação depende de um host chamado `db`, pensado para uso via Docker Compose.
- Existe uma lógica de **popup/aviso de atualização mensal** associada aos custos fixos (`condUpdatePopup`), refletindo o fluxo do frontend para atualização periódica desses valores.
- O extrato mensal retorna `404` quando não há serviços no período informado.

---

## Melhorias futuras

Algumas evoluções que fazem sentido para o projeto:
- exportação também em Excel/CSV
- dashboard com métricas agregadas mais ricas
- edição de serviços já cadastrados
- histórico mensal de atualização de custos fixos
- documentação OpenAPI/Swagger
- separação explícita do código-fonte do frontend no repositório
- externalização de segredos e configurações sensíveis
- cobertura maior de testes por controller, service e regra de negócio

---

## Galeria

<img width="609" height="272" alt="servicos" src="https://github.com/user-attachments/assets/2a397694-5def-4c3d-9888-4d8df3a14c52" />

<img width="609" height="272" alt="insumosVariaveis" src="https://github.com/user-attachments/assets/024de2e1-5abb-4d06-8039-8d3f2bb5f8e7" />

<img width="609" height="272" alt="insumosFixos" src="https://github.com/user-attachments/assets/9d9721be-ac54-431c-bc55-b6c3f67c11dd" />

<img width="609" height="272" alt="paginaDoExtratoMensal" src="https://github.com/user-attachments/assets/e635415b-7ca3-4d31-a3ca-733ad334c54d" />

<img width="609" height="272" alt="paginaEdicaoPerfil" src="https://github.com/user-attachments/assets/461a1187-96ba-4928-b35e-e2d4527739c1" />

---

### Exemplo de extrato (Apenas a primeira pagina) 

<img width="1304" height="594" alt="exemploExtratoMensal" src="https://github.com/user-attachments/assets/5ccc9e30-9fec-4dfa-be3c-1a2ba616d4b7" />

---

# TO-DO

## Agora

- [x] Implementar edição das informações do perfil do usuário
- [x] Alinhar com o front quais campos do perfil poderão ser editados
- [x] Definir o endpoint e o payload de atualização de perfil
- [x] Garantir que alterações no perfil reflitam corretamente nas configurações de precificação, quando aplicável

- [x] Criar o módulo de custos fixos mensais
- [x] Permitir cadastrar o valor do mês anterior para cada custo fixo
- [x] Separar conceitualmente custos fixos de insumos variáveis no backend
- [x] Alinhar com o front a interface de cadastro, edição e visualização dos custos fixos
- [x] Implementar lembrete visual no front para o usuário atualizar os valores mensalmente
- [x] Definir a regra do lembrete para aparecer a partir de uma data fixa, como o dia 5 de cada mês

- [x] Implementar extrato mensal
- [x] Definir o conteúdo do extrato mensal, incluindo:
    - [x] serviços/produtos cadastrados
    - [x] custos fixos do mês
    - [x] preços sugeridos
    - [x] resumo financeiro/gerencial
- [x] Escolher o formato inicial de exportação:
    - [x] PDF para visualização
    - [ ] Excel para análise e edição
- [x] Implementar a geração de PDF
- [x] Alinhar com o front a funcionalidade de exportação do extrato mensal
