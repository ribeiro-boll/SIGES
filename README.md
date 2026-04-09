<img width="456" height="290" alt="telaInicial" src="https://github.com/user-attachments/assets/3938b09e-c876-4398-9ff2-64492bc43aad" />

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](#)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](#)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](#)
[![H2](https://img.shields.io/badge/H2-Tests_&_Dev-09476B?style=for-the-badge&logo=h2database&logoColor=white)](#)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](#)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)](#)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-PDF_Templates-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](#)
[![PDF Reports](https://img.shields.io/badge/PDF-OpenHTMLtoPDF-B30B00?style=for-the-badge&logo=adobeacrobatreader&logoColor=white)](#)
[![REST API](https://img.shields.io/badge/API-REST-FF6F00?style=for-the-badge&logo=fastapi&logoColor=white)](#)

# (SIGES) Sistema Integrado de Gestão de Serviços

Sistema full stack para **gestão de serviços, custos, precificação e histórico de vendas**.

A ideia do projeto é simples: ajudar profissionais autônomos e pequenos negócios a entenderem **quanto custa prestar um serviço**, **quanto cobrar**, **quanto realmente lucraram** e **como acompanhar isso ao longo do mês**.

Nesta versão refatorada, o SIGES reúne backend, frontend, autenticação, persistência, relatórios em PDF e testes automatizados em um único projeto.

---

## O que o sistema faz

- cadastro e login de usuários com **JWT**
- cadastro de **insumos variáveis**
- cadastro e edição de **custos fixos mensais**
- cadastro e remoção de **serviços realizados**
- cálculo automático de **despesas**, **preço sugerido** e **lucro líquido**
- listagens paginadas para serviços e insumos
- geração de **extrato mensal em PDF**
- frontend servido pelo próprio backend

---

## Ideia do projeto

O SIGES foi pensado para resolver um problema bem comum em negócios baseados em serviço: muita gente sabe quanto cobra, mas não sabe exatamente **quanto aquele atendimento custou**.

Aqui, o sistema junta três frentes:

- **custos variáveis**, como produtos usados no atendimento
- **custos fixos**, como aluguel, água, energia e internet
- **tempo de trabalho**, com base na meta de renda e rotina do usuário

Com isso, ele calcula um preço sugerido mais realista e também mostra o lucro final de cada serviço.

---

## Stack usada

### Backend
- Java
- Spring Boot
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Thymeleaf

### Banco
- PostgreSQL
- H2 para testes

### Relatórios
- OpenHTMLtoPDF

### DevOps
- Docker
- Docker Compose
- GitHub Actions

---

## Como o projeto está organizado

```text
SIGES/
├── src/main/java/com/bolota/historicodevendas/
│   ├── Controller/
│   ├── Entities/
│   ├── Resource/
│   ├── Security/
│   └── Service/
├── src/main/resources/
│   ├── application.yaml
│   ├── application-test.yaml
│   ├── static/
│   └── templates/
├── src/test/java/com/bolota/historicodevendas/
├── Dockerfile
├── compose.yaml
└── pom.xml
```

De forma resumida:

- `Controller` concentra os endpoints
- `Entities` guarda DTOs e entidades persistentes
- `Resource` faz a comunicação com o banco via JPA
- `Security` cuida de autenticação e proteção de rotas
- `Service` concentra regras auxiliares, autenticação e geração de PDF

---

## Principais regras de negócio

### Insumos variáveis
São itens consumidos por medida durante o serviço, como shampoo, creme ou material descartável.

O sistema calcula automaticamente o custo por medida com base no valor total do produto e na medida cadastrada.

### Insumos fixos
São custos mensais diluídos no tempo, como aluguel, internet, água e energia.

O sistema transforma esse custo em valor por minuto para considerar o impacto dele em cada atendimento.

### Serviços
Ao registrar um serviço, o sistema leva em conta:

- insumos variáveis usados
- custos fixos proporcionais ao tempo do serviço
- custo do tempo de trabalho do usuário
- margem de lucro configurada

Com isso, ele calcula:

- despesas do serviço
- preço sugerido
- lucro líquido final

### Regras de integridade

- um serviço precisa ter ao menos um insumo associado
- insumos em uso não podem ser removidos
- ao remover um serviço, o sistema ajusta o contador de uso dos insumos vinculados

---

## Autenticação e segurança

A aplicação usa **JWT** para autenticação.

### Rotas públicas
- `/`
- `/login`
- `/cadastro`
- `/user/login`
- `/user/register`
- `/h2-console/**`

### Rotas protegidas
As demais exigem token JWT no header:

```http
Authorization: Bearer <token>
```

As senhas são armazenadas com **BCrypt**.

---

## Como executar

### 1) Variável obrigatória

A aplicação precisa de um segredo JWT:

```properties
JWT_SECRET=coloque_aqui_um_segredo_com_pelo_menos_32_caracteres
```

Você pode colocar isso em um arquivo `.env` na raiz do projeto.

---

### 2) Subindo com Docker

```bash
docker compose up --build
```

A aplicação sobe na porta `8080`.

O banco padrão do `compose.yaml` é PostgreSQL.

---

### 3) Rodando localmente com perfil de teste

Para desenvolvimento rápido, o projeto também roda com H2 em memória:

```bash
SPRING_PROFILES_ACTIVE=test JWT_SECRET=seu_segredo ./mvnw spring-boot:run
```

Nesse perfil:

- o banco é H2
- o console H2 fica disponível em `/h2-console`
- a aplicação sobe em `8080`

---

## Endpoints principais

### Usuário
Base: `/user`

- `POST /user/register` — cadastra usuário e retorna JWT
- `POST /user/login` — autentica e retorna JWT
- `PATCH /user/update` — atualiza renda desejada, dias trabalhados, horas diárias e margem de lucro

### Insumos
Base: `/supplies`

- `POST /supplies/register` — cadastra insumo variável
- `POST /supplies/register_fixed` — cadastra insumo fixo
- `PATCH /supplies/edit_fixedSupply` — edita insumo fixo
- `DELETE /supplies/remove` — remove insumo variável
- `DELETE /supplies/remove_fixed` — remove insumo fixo

### Serviços
Base: `/product`

- `POST /product/register` — registra serviço realizado
- `DELETE /product/remove` — remove serviço

### Métricas e relatório
Base: `/metrics`

- `GET /metrics/services` — lista serviços paginados
- `GET /metrics/supplies` — lista insumos variáveis
- `GET /metrics/supplies_fixed` — lista insumos fixos
- `GET /metrics/download_pdf?month={m}&year={y}` — gera extrato mensal em PDF

> Observação: o domínio é de serviços, mas o controller usa a base `/product`.

---

## Exemplo de payload

### Cadastro de usuário

```json
{
  "login": "teste_user",
  "passwordHash": "123456",
  "desiredMonthlyIncome": 6500.0,
  "daysWorkingWeekly": 5,
  "hoursWorkingDaily": 8.0,
  "profitMargin": 120.0
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
  "variableSuppliesUsedUUID": ["uuid-var-1"],
  "variableSuppliesQuantityUsed": {
    "uuid-var-1": 0.5
  },
  "fixedSuppliesUsedUUID": ["uuid-fix-1"],
  "serviceNotes": "Cliente pediu finalização mais alinhada nas pontas."
}
```

---

## Extrato mensal em PDF

O sistema gera um relatório mensal em PDF com foco em leitura rápida.

Ele resume:

- total bruto do mês
- lucro total
- ticket médio
- quantidade de serviços
- cards com resumo de cada atendimento

A proposta do PDF é ser mais executivo e menos poluído, deixando o detalhamento pesado para uma futura exportação em planilha.

---

## Testes

O projeto possui testes automatizados com **Spring Boot Test + MockMvc**, usando H2 no perfil de teste.

Suites identificadas no projeto:

- `UserControllerFullTest`
- `SuppliesControllerFullTest`
- `AllControllersLifeCycleFullTest`

Comando para rodar:

```bash
JWT_SECRET=seu_segredo ./mvnw test
```

Segundo o estado atual da versão refatorada, o projeto está **passando em todos os testes**.

---

## Observações importantes

### Versão do Java
Hoje existe uma diferença entre os ambientes:

- `pom.xml` e testes usam **Java 17**
- Dockerfile usa **Java 21**

Na prática, o caminho mais seguro é padronizar depois, mas hoje vale deixar isso documentado para evitar surpresa ao subir o projeto.

### Frontend
No snapshot atual, o frontend está presente já **buildado dentro do projeto**, sendo servido pelo próprio backend.

---

## Galeria

### Tela inicial
<img width="456" height="290" alt="telaInicial" src="https://github.com/user-attachments/assets/3938b09e-c876-4398-9ff2-64492bc43aad" />

### Serviços
<img width="609" height="272" alt="servicos" src="https://github.com/user-attachments/assets/2a397694-5def-4c3d-9888-4d8df3a14c52" />

### Insumos variáveis
<img width="609" height="272" alt="insumosVariaveis" src="https://github.com/user-attachments/assets/024de2e1-5abb-4d06-8039-8d3f2bb5f8e7" />

### Insumos fixos
<img width="609" height="272" alt="insumosFixos" src="https://github.com/user-attachments/assets/9d9721be-ac54-431c-bc55-b6c3f67c11dd" />

### Extrato mensal
<img width="609" height="272" alt="paginaDoExtratoMensal" src="https://github.com/user-attachments/assets/e635415b-7ca3-4d31-a3ca-733ad334c54d" />

### Perfil
<img width="609" height="272" alt="paginaEdicaoPerfil" src="https://github.com/user-attachments/assets/461a1187-96ba-4928-b35e-e2d4527739c1" />

### Exemplo do PDF
<img width="1304" height="594" alt="exemploExtratoMensal" src="https://github.com/user-attachments/assets/5ccc9e30-9fec-4dfa-be3c-1a2ba616d4b7" />
