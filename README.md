# Opção 1:  FiadoPay (backend)

Este projeto é uma refatoração completa do simulador FiadoPay, seguindo as instruções da disciplina de AVI/POOA, com foco em:
* Anotações personalizadas (@PaymentMethod, @AntiFraud, @LoggedOperation…)

* Uso de reflexão para montar registro de estratégias

* Concorrência com ExecutorService (pagamentos + webhooks assíncronos)

* Manutenção fiel do contrato original da API

* H2 em memória com modelo PostgreSQL-like

* Webhooks com assinatura HMAC

## 1 FiadoPay funciona como um gateway de pagamentos fake, simulando:

* criação de comerciantes

* autenticação com token falso (FAKE-<id>)

* criação de pagamentos (com idempotência)

* consulta

* reembolso

* webhooks assíncronos

* juros de parcelamento (1% ao mês)

Este projeto utiliza arquitetura limpa, injeção de dependência, reflexão para estratégias de pagamento e threads para processamento background.

## 2 Decisões de Design

* Estratégia de Pagamento via Reflection
Anotações personalizadas @PaymentMethod(type="...") foram criadas.
O sistema escaneia automaticamente todos os beans com essa anotação e constrói o registry. Isso permite adicionar novos métodos como PIX/DEBIT/BOLETO apenas criando novas classes.

* Uso de Threads (Concorrência)
Foi criado um ExecutorService configurável: fiadopay.executor.threads=8
Usado em processamento assíncrono de pagamentos (aprovação/recusa) e envio de webhooks com retentativas progressivas
Isso evita bloquear requisições HTTP e simula gateways reais.

* Idempotência real
Pagamentos criados com Idempotency-Key retornam sempre o mesmo resultado.

* HMAC para assinaturas de webhook
O payload do webhook é assinado

* Logging Aspect via AOP
A anotação @LoggedOperation  Gera logs de início, fim e tempo de execução.

* AntiFraud (não utilizada, mas implementada)
Afim de ser utilizada em cenários futuros.

## 3 Anotações Criadas

* @PaymentMethod(type, monthlyInterest, allowInstallments): Identifica e configura regras de métodos de pagamento via reflexão

* @AntiFraud(name, threshold): Marca classes que deveriam passar por verificação antifraude

* @LoggedOperation(value): Marca métodos que devem ser logados via AOP

## 4 Como funciona a reflexão no projeto

O projeto utiliza reflexão para registrar automaticamente todas as estratégias de pagamento presentes no pacote edu.ucsal.fiadopay.payment.
Cada classe marcada com a anotação @PaymentMethod é detectada automaticamente pelo Spring durante o startup.

## 5 Arquitetura Principal
<img width="295" height="220" alt="Captura de tela 2025-11-19 152715" src="https://github.com/user-attachments/assets/066f12f8-18a1-4030-adba-c425af1ada61" />

* controller: REST API
* domain: Entidades JPA
* repo: Repositórios Spring Data
* service: Lógica de negócio
* payment: Estratégias + reflexão
* config: Swagger, ExecutorService
* aspects: Logging AOP
* exception: Handler global

## 6 Padrões de Projeto Aplicados
* Strategy Pattern — usado para métodos de pagamento (CARD, PIX, BOLETO…)
* Factory via Reflection — criação automática das estratégias anotadas
* AOP (Aspect-Oriented Programming) — logs com @LoggedOperation
* Dependency Injection — serviços e strategies geridas pelo Spring
* Template de Retentativa — entrega de webhooks com backoff progressivo

## 7 Execução

* Clonar & rodar: mvn spring-boot:run
* Console do H2: http://localhost:8080/h2; JDBC URL: jdbc:h2:mem:fiadopay
* Swagger: http://localhost:8080/swagger-ui.html

## 8 Fluxo

* Criar merchant: curl -X POST "http://localhost:8080/fiadopay/admin/merchants" -H "Content-Type: application/json" -d "{\"name\":\"Loja Teste3\",\"webhookUrl\":\"http://localhost:8080/meu-webhook\"}"
<img width="1110" height="77" alt="Captura de tela 2025-11-19 160202" src="https://github.com/user-attachments/assets/05916e66-b488-4400-bd4f-beaae1fb05c7" />

* Obter token: curl -X POST "http://localhost:8080/fiadopay/auth/token" -H "Content-Type: application/json" -d "{\"client_id\":\"4db3d8e6-4f45-4573-b791-d1ba0c999693\",\"client_secret\":\"6f1103541ebc4599bcf42b1ae8eae130\"}"
OBS: (Substitua clientId e clientSecret pelos SEUS gerados)
<img width="1110" height="88" alt="Captura de tela 2025-11-19 160311" src="https://github.com/user-attachments/assets/66b0e435-3ca9-4d00-a5d1-b9ff36f1162d" />

* Criar pagamento: curl -X POST "http://localhost:8080/fiadopay/gateway/payments" -H "Content-Type: application/json" -H "Authorization: Bearer FAKE-1" -d "{\"method\":\"CARD\",\"currency\":\"BRL\",\"amount\":150.0,\"installments\":2,\"metadataOrderId\":\"pedido123\"}"
<img width="1111" height="100" alt="Captura de tela 2025-11-19 160430" src="https://github.com/user-attachments/assets/c4077adf-b141-4277-8431-00ccd88d06cd" />

* Status de Pagamento: curl -X GET "http://localhost:8080/fiadopay/gateway/payments/pay_3b6c9243" -H "Authorization: Bearer FAKE-1"
OBS: (Substitua pelo paymentId que retornou)
<img width="1106" height="83" alt="Captura de tela 2025-11-19 160534" src="https://github.com/user-attachments/assets/0944704a-b444-4dec-ac9f-d1f7a6bf5177" />

* Teste de Idempotência: curl -X POST "http://localhost:8080/fiadopay/gateway/payments" -H "Content-Type: application/json" -H "Authorization: Bearer FAKE-1" -H "Idempotency-Key: pedido123" -d "{\"method\":\"CARD\",\"currency\":\"BRL\",\"amount\":150.0,\"installments\":2,\"metadataOrderId\":\"pedido123\"}"
<img width="1126" height="105" alt="Captura de tela 2025-11-19 160625" src="https://github.com/user-attachments/assets/4aa96d54-3440-4185-ab5e-675ea79e7b78" />

* Teste de Concorrência (Threads): for /l %i in (1,1,10) do start cmd /c curl -X POST "http://localhost:8080/fiadopay/gateway/payments" -H "Authorization: Bearer FAKE-1" -H "Content-Type: application/json" -d "{\"method\":\"CARD\",\"currency\":\"BRL\",\"amount\":10,\"installments\":1}"
<img width="1362" height="636" alt="Captura de tela 2025-11-19 161035" src="https://github.com/user-attachments/assets/1918a545-a47d-450d-814f-79f32d7e10c8" />
<img width="1621" height="484" alt="Captura de tela 2025-11-19 161205" src="https://github.com/user-attachments/assets/dd6ff013-c841-41ac-bd37-363278ceff2e" />

## 9 Limitações

* Não valida X-Signature no retorno do cliente
* AntiFraud existe mas ainda não possui engine funcionando, seria pra uso futuro.
* Webhook não possui dead-letter queue real, apenas retentativas simples.

## OBSERVAÇÕES

Toda a API original fornecida pelo professor foi preservada (rotas, payloads, fluxo de autenticação FAKE, idempotência e webhook).
