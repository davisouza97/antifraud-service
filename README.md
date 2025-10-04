# Antifraud Service

## Descrição
O **Antifraud Service** é um microserviço que atua dentro de um ecossistema bancário digital, responsável por **analisar transações financeiras em tempo real** e identificar atividades suspeitas.

Ele se integra com o sistema principal do banco via **REST** e consome eventos de transações através do **Kafka**, aplicando regras de negócio para determinar possíveis fraudes.

---

## Tecnologias
- Java 21
- Spring Boot 3.5.6
    - Spring Web
    - Spring Data JPA
    - Spring Data Redis
    - Spring Kafka
    - Spring Cloud OpenFeign
- MySQL 8
- Redis 7
- Apache Kafka 7.6.0 (Confluent)
- Zookeeper
- Kafdrop (monitoramento do Kafka)
- JUnit 5 / Mockito (testes)

---

## Estrutura do Projeto
```
antifraud-service/
├── src/ # Código-fonte da aplicação
├── docker-compose.yml # Infraestrutura para ambiente local
├── antifraud-service-api.yaml # Especificação OpenAPI (endpoints do serviço)
├── análise-fraude.postman_collection.json # collection do postman
├── pom.xml # Dependências Maven
├── README.md # Este arquivo
└── ... # Demais arquivos
```

---
## Configuração

### Pré-requisitos
- Java 21+
- Maven 3.9+
- Docker e Docker Compose

### Variáveis de ambiente (opcionais)
O `application.yaml` já vem configurado com valores padrão.  
Mas você pode sobrescrever via variáveis de ambiente:

| Variável                           | Descrição                                      | Default |
|------------------------------------|------------------------------------------------|---------|
| `ACCOUNT_URL`                      | URL da API de contas                           | `https://13289d5f-2661-4b2b-afad-5a2fdde5d431.mock.pstmn.io` |
| `TRANSACTION_LOOKBACK_DAYS`        | Dias retroativos para análise de transações    | `7` |
| `MAX_SAME_TYPE_TRANSACTIONS`       | Limite de transações do mesmo tipo             | `3` |
| `SAME_TYPE_TRANSACTION_WINDOW_IN_MINUTES` | Janela de tempo (min) para análise          | `3` |

---

## Como Rodar

### 1. Subir a infraestrutura
Na raiz do projeto, execute:
```bash
docker-compose up -d
```
Isso irá subir:

- MySQL em localhost:3306

- Redis em localhost:6379

- Kafka em localhost:29092

- Kafdrop em localhost:9000

### 2. Rodar a aplicação

Após a subida dos containers, execute o comando:

```bash
mvn spring-boot:run
```
Esse comando já baixa as dependências necessárias automaticamente.
No entanto, se ocorrer algum erro de resolução de dependências (por exemplo, cache corrompido no Maven), rode:

```bash
mvn clean install
```

e após isso volte a rodar o primeiro comando da etapa 2.

A aplicação estará disponível em:
http://localhost:8080

## Informações gerais e escolhas técnicas

- Para fins do desafio, o Spring fica encarregado de montar a estrutra do banco a partir da estrutura da(s) entidade(s),
em uma aplicação real(ou com um tempo maior para desenvolvimento uma solução mais robusta e adequada como por exemplo **flyway** seria utilizada).

- Por padrão os logs estão configurados para **DEBUG** e podem ser alteradas no `application.yaml`.

- Como boa prática, as regras de negócio que se baseia em valores fixos(prazo em dias/minutos, quantidade de transações suspeitas aceitáveis)
estão presentes no `application.yaml` com os valores descritos no desafio, com isso essas regras podem ser mais facilmente modificáveis.

- Foi utilizado **rest-proxy** para criar uma ponte para o **kafka**, com isso é possível ler e publicar mensagens no **kafka** 
via Postman, essa parte está na collection do postman na pasta raíz do projeto.
  - `publish transacoes.events` publica uma mensagem no `transacoes.events`.
  - `create consumer antifraude-group` necessário para criar um consumer.
  - `subscribe consumer` necessário se inscrever no consumer para ler as mensagens.
  - `read fraud.suspictions.notifications` lê as mensagens publicadas pelo **Antifraud Service**.
- Na pasta `API Rest` da collection é possível ver os exemplos de chamada aos endpoints da aplicação, necessário mudar os dados de acordo com a massa criada.

- É possível se conectar no banco a partir da url: `jdbc:mysql://localhost:3306/antifraud` com o username "user" e password "password"

- 



## Endpoints da API

O serviço antifraude expõe os seguintes endpoints REST:

| Método | Endpoint                | Descrição                                                                 |
|--------|-------------------------|---------------------------------------------------------------------------|
| GET    | /analises/{idTransacao} | Retorna o status da análise de uma transação (ex: APROVADA, SUSPEITA)     |
| GET    | /analises/suspeitas     | Retorna lista de transações suspeitas, com filtros por data e id da conta |
| PATCH  | /analises/{idTransacao} | Atualiza o status de uma transação (de suspeita para aprovada e vice-versa) |


## Documentação da API

A especificação dos endpoints está descrita em antifraud-service-api.yaml (formato OpenAPI/Swagger).

Você pode visualizar a documentação de forma interativa utilizando ferramentas como:
- Swagger Editor
- Insomnia
- Postman

Basta importar o arquivo para explorar e testar a API.

## Integração com Kafka

O serviço consome e publica eventos no Kafka:

### Entrada: 
- transacoes.events

### Saída: 
- fraud.suspictions.notifications

## Testes

Rodar os testes automatizados:

```
mvn test
```

## Mock da API do Banco

Um mock da API do sistema bancário está disponível via Postman, permitindo simular chamadas ao serviço antifraude.

Caso o mock pare de funcionar é possível importar a collection do postman(análise-fraude.postman_collection.json na pasta raíz do projeto) e criar um mock novo, nesse caso será necessário mudar a url no application.yaml