# SPEI-IN API — Keycloak + Apache Fineract

API REST en Java 21 + Spring Boot 3 para simular la recepción de un SPEI entrante y registrar tres depósitos en Apache Fineract: principal, spread y comisión.

## Arquitectura

Postman → Spring Boot API → Keycloak (JWT)\n                         └→ Fineract (Basic Auth, 3 depósitos)

La API valida el token, valida el request, aplica idempotencia, busca la cuenta de ahorro mediante la CLABE, calcula importes y orquesta los tres depósitos. Fineract genera los asientos contables.

## Requisitos

- Java 21
- Maven 3.9+
- Acceso de red al laboratorio de Keycloak/Fineract

## Configuración

Las credenciales de Fineract se mantienen en configuración/variables de entorno y no en Postman.

Variables principales:

```text
KEYCLOAK_ISSUER=https://auth.lab.dnu.mx/realms/fineract_keycloak
FINERACT_BASE_URL=https://fineract.lab.dnu.mx/fineract-provider/api/v1
FINERACT_USERNAME=mifos
FINERACT_PASSWORD=password
FINERACT_TENANT_ID=default
FINERACT_PAYMENT_TYPE_ID=3
SPEI_SPREAD=0.005
SPEI_COMMISSION=8.00
SERVER_PORT=8080
```

> `FINERACT_PAYMENT_TYPE_ID` debe sustituirse por el valor que entregue el entrevistador si es diferente. La CLABE no se configura: llega en cada request y se busca en Fineract.

## Ejecución

```bash
mvn clean test
mvn spring-boot:run
```

O:

```bash
mvn clean package
java -jar target/spei-in-api-0.0.1-SNAPSHOT.jar
```

## Endpoint principal

`POST /api/v1/spei/inbound`

Headers:

```text
Authorization: Bearer <access_token>
Idempotency-Key: <UUID>
Content-Type: application/json
```

Body:

```json
{
  "claveRastreo": "SPEI20261001120000",
  "monto": 1000.00,
  "cuentaBeneficiaria": "<CLABE-18-DIGITOS>",
  "concepto": "Abono cliente demo"
}
```

## Cálculo

Para monto `1000.00`:

- Principal = `1000.00`
- Spread = `1000.00 × 0.005 = 5.00`, HALF_UP a 2 decimales
- Comisión = `8.00`

Cada importe se registra mediante `POST /savingsaccounts/{savingsId}/transactions?command=deposit` con una nota identificable por `claveRastreo`.

## Idempotencia

La API persiste el `Idempotency-Key`. Si llega nuevamente la misma clave, devuelve el resultado original y no vuelve a ejecutar depósitos en Fineract.

## Errores esperados

- `401`: token ausente, inválido o expirado (Spring Security/Keycloak).
- `400`: body inválido.
- `422`: CLABE inexistente, cuenta inactiva o moneda distinta de MXN.
- `502`: error de Fineract.
- `404`: SPEI no encontrado en `GET /api/v1/spei/{id}`.

## Verificación en Fineract

Después de procesar el SPEI, verificar:

```text
GET /savingsaccounts/{savingsId}?associations=transactions
GET /journalentries?savingsId={savingsId}&transactionDetails=true&limit=50
```

El segundo endpoint es la evidencia del asiento contable generado por Fineract.

## Postman

Importar `postman/SPEI-IN.postman_collection.json` y ejecutar en este orden:

1. Keycloak - Login
2. SPEI - Inbound
3. SPEI - Idempotency retry
4. SPEI - Get
5. SPEI - No token (401)

## Fuera de alcance

No se implementa UI, SPEI real/Banxico, creación de productos/clientes/cuentas en Fineract ni escritura manual de journal entries.
