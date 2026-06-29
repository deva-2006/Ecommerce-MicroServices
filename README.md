# E-Commerce Microservices Backend

A production-aligned microservices backend system built with Spring Boot, Spring Cloud, and AWS DynamoDB. Designed to demonstrate real-world distributed systems concepts including service discovery, declarative inter-service communication, API gateway routing, and a complete e-commerce order lifecycle.

---

## Key Features

- **6 independent microservices** with clearly separated responsibilities
- **Eureka-based service discovery** — no hardcoded service URLs
- **OpenFeign declarative clients** for inter-service communication
- **API Gateway** as a single entry point for all client requests
- **AWS DynamoDB** as the NoSQL persistence layer (DynamoDB Local for development)
- **Price and product integrity** — Cart Service fetches product details from Product Service; clients cannot manipulate price or product name
- **Inventory isolation** — stock is managed exclusively by Inventory Service, not Product Service
- **Order lifecycle** — stock deduction and cart clearing happen only after payment success
- **Feign error handling** — custom `ErrorDecoder` and `GlobalExceptionHandler` across all services
- **Resume-ready architecture** — clean layered structure, DTOs, service interfaces, and validation throughout

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Inter-Service Communication | OpenFeign |
| Database | AWS DynamoDB (DynamoDB Local for dev) |
| AWS SDK | AWS SDK v2 (DynamoDB Enhanced Client) |
| Build Tool | Maven |
| Utilities | Lombok, Spring Validation |

---

## Architecture Overview

```
                          ┌─────────────────────────────┐
                          │        Eureka Server        │
                          │           :8761             │
                          │   (Service Registry)        │
                          └──────────────┬──────────────┘
                                         │ All services register here
                                         │
                    ┌────────────────────▼────────────────────┐
                    │              API Gateway                │
                    │                :8080                    │
                    │   (Single entry point — routes all      │
                    │    client requests to microservices)    │
                    └────┬──────┬──────┬──────┬──────┬────────┘
                         │      │      │      │      │
              ┌──────────▼ ┐  ┌──▼────┐ │  ┌──▼────┐ │   ┌─────▼─────┐
              │  Product   │  │ Cart  │ │  │Payment│ │   │ Inventory │
              │  Service   │  │Service│ │  │Service│ │   │  Service  │
              │   :8081    │  │ :8082 │ │  │ :8083 │ │   │   :8085   │
              └─────┬──────┘  └──┬────┘ │  └──┬────┘ │   └─────┬─────┘
                    │            │      │      │      │         │
                    │            │   ┌──▼──────▼──┐   │         │
                    │            │   │   Order    │   │         │
                    │            │   │  Service   │   │         │
                    │            │   │   :8084    │   │         │
                    │            │   └────────────┘   │         │
                    │            │                    │         │
              ┌─────▼────────────▼────────────────────▼─────────▼─────┐
              │                  AWS DynamoDB (Local :8000)           │
              │   Products | Cart | Payments | Orders | Inventory     │
              └───────────────────────────────────────────────────────┘
```

---

## Microservices

| Service | Port | Responsibility |
|---|---|---|
| **Eureka Server** | 8761 | Service registry — all services register and discover each other here |
| **API Gateway** | 8080 | Single entry point — routes all client requests to the correct service |
| **Product Service** | 8081 | Manages product catalog (name, description, category, price). No stock. |
| **Cart Service** | 8082 | Manages user carts. Fetches product details from Product Service and validates stock with Inventory Service before adding items. |
| **Payment Service** | 8083 | Manages payments. Notifies Order Service on payment status change. |
| **Order Service** | 8084 | Orchestrates order creation — fetches cart, validates products and stock, creates order, triggers payment. After payment success, deducts stock and clears cart. |
| **Inventory Service** | 8085 | Owns all stock data. Handles stock validation, addition, update, deduction, and deletion. |

---

## Inter-Service Communication

```
Cart Service
  └── ProductClient    → GET /products/{id}           (verify product, fetch price and name)
  └── InventoryClient  → GET /inventory/{id}/validate  (verify stock before add)

Order Service
  └── CartClient       → GET /cart/{userId}            (fetch cart items)
  └── ProductClient    → GET /products/{id}            (verify product exists)
  └── InventoryClient  → GET /inventory/{id}/validate  (validate stock)
  └── PaymentClient    → POST /payments                (create payment — PENDING)

Payment Service
  └── OrderClient      → PUT /orders/{id}/status       (update order to CONFIRMED/CANCELLED)
  └── OrderClient      → POST /orders/{id}/payment-success  (trigger stock deduction + cart clear)

Order Service (on payment success callback)
  └── InventoryClient  → PUT /inventory/{id}/deduct-stock   (deduct stock per item)
  └── CartClient       → DELETE /cart/{userId}              (clear cart)
```

---

## Order Lifecycle

```
1. Client sends POST /orders (userId, shippingAddress, paymentMethod)
        │
        ▼
2. Order Service fetches cart items  →  Cart Service
        │
        ▼
3. Order Service validates each product  →  Product Service
        │
        ▼
4. Order Service validates stock for each item  →  Inventory Service
        │
        ▼
5. Order saved with status: PENDING
        │
        ▼
6. Payment created with status: PENDING  →  Payment Service
        │
        ▼
7. Response returned: { orderId, paymentId }

════════════════════════════════════════════
  Client sends PUT /payments/{id}/status?status=SUCCESS
════════════════════════════════════════════
        │
        ▼
8. Payment updated  →  SUCCESS
        │
        ▼
9. Order status updated  →  CONFIRMED  (via OrderClient)
        │
        ▼
10. Stock deducted per item  →  Inventory Service
        │
        ▼
11. Cart cleared  →  Cart Service

STATUS MAPPING
──────────────────────────────
Payment SUCCESS   → Order CONFIRMED
Payment FAILED    → Order CANCELLED
Payment REFUNDED  → Order REFUNDED
```

---

## Project Structure

```
ecommerce-microservices/
├── eureka-server/
│   └── src/main/java/com/deva/eurekaserver/
├── api-gateway/
│   └── src/main/resources/application.yml
├── product-service/
│   └── src/main/java/com/deva/productservice/
│       ├── config/         DynamoDbConfig
│       ├── controller/     ProductController
│       ├── service/        ProductService, ProductServiceImpl
│       ├── repository/     ProductRepository
│       ├── entity/         Product
│       ├── dto/            ProductRequestDTO, ProductResponseDTO
│       └── exception/      ResourceNotFoundException, GlobalExceptionHandler
├── inventory-service/
│   └── src/main/java/com/deva/inventoryservice/
│       ├── config/         DynamoDbConfig
│       ├── controller/     InventoryController
│       ├── service/        InventoryService, InventoryServiceImpl
│       ├── repository/     InventoryRepository
│       ├── entity/         Inventory
│       ├── dto/            InventoryRequestDTO, InventoryResponseDTO, StockDeductRequestDTO
│       └── exception/      ResourceNotFoundException, GlobalExceptionHandler
├── cart-service/
│   └── src/main/java/com/deva/cartservice/
│       ├── client/         ProductClient, InventoryClient
│       ├── config/         DynamoDbConfig
│       ├── controller/     CartController
│       ├── service/        CartService, CartServiceImpl
│       ├── repository/     CartRepository
│       ├── entity/         Cart
│       ├── dto/            CartRequestDTO, CartResponseDTO, ProductResponseDTO
│       └── exception/      ResourceNotFoundException, FeignErrorDecoder, GlobalExceptionHandler
├── payment-service/
│   └── src/main/java/com/deva/paymentservice/
│       ├── client/         OrderClient
│       ├── config/         DynamoDbConfig
│       ├── controller/     PaymentController
│       ├── service/        PaymentService, PaymentServiceImpl
│       ├── repository/     PaymentRepository
│       ├── entity/         Payment
│       ├── dto/            PaymentRequestDTO, PaymentResponseDTO
│       └── exception/      ResourceNotFoundException, FeignErrorDecoder, GlobalExceptionHandler
└── order-service/
    └── src/main/java/com/deva/orderservice/
        ├── client/         CartClient, ProductClient, InventoryClient, PaymentClient
        ├── config/         DynamoDbConfig
        ├── controller/     OrderController
        ├── service/        OrderService, OrderServiceImpl
        ├── repository/     OrderRepository
        ├── entity/         Order, OrderItem
        ├── dto/            OrderRequestDTO, OrderResponseDTO, OrderItemDTO,
        │                   CartItemDTO, ProductResponseDTO,
        │                   PaymentRequestDTO, PaymentResponseDTO,
        │                   StockDeductRequestDTO
        └── exception/      ResourceNotFoundException, FeignErrorDecoder, GlobalExceptionHandler
```

---

## Running the Project

### Prerequisites

- Java 17
- Maven
- AWS CLI
- DynamoDB Local

### Step 1 — Start DynamoDB Local

```bash
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

### Step 2 — Create DynamoDB Tables

```bash
# Products
aws dynamodb create-table --table-name Products \
  --attribute-definitions AttributeName=productId,AttributeType=S \
  --key-schema AttributeName=productId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

# Cart (composite key)
aws dynamodb create-table --table-name Cart \
  --attribute-definitions AttributeName=userId,AttributeType=S AttributeName=productId,AttributeType=S \
  --key-schema AttributeName=userId,KeyType=HASH AttributeName=productId,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

# Payments
aws dynamodb create-table --table-name Payments \
  --attribute-definitions AttributeName=paymentId,AttributeType=S \
  --key-schema AttributeName=paymentId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

# Orders
aws dynamodb create-table --table-name Orders \
  --attribute-definitions AttributeName=orderId,AttributeType=S \
  --key-schema AttributeName=orderId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

# Inventory
aws dynamodb create-table --table-name Inventory \
  --attribute-definitions AttributeName=productId,AttributeType=S \
  --key-schema AttributeName=productId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000
```

### Step 3 — Start Services in Order

```
1. Eureka Server      (8761)  — wait for dashboard at http://localhost:8761
2. Product Service    (8081)
3. Inventory Service  (8085)
4. Cart Service       (8082)
5. Payment Service    (8083)
6. Order Service      (8084)
7. API Gateway        (8080)  — start last
```

Wait ~10 seconds after all services start. Verify all 5 services appear at `http://localhost:8761`.

### Step 4 — Build and Run Each Service

```bash
cd product-service
mvn clean install
mvn spring-boot:run
```

Repeat for each service.

---

## Postman — End-to-End Test Flow

All requests go through the API Gateway on port `8080`.

```
1.  POST   /products              → create a product, save productId
2.  POST   /inventory             → create stock for that productId
3.  POST   /cart                  → add item (userId, productId, quantity only)
4.  POST   /orders                → place order, save orderId + paymentId
5.  GET    /orders/{orderId}      → verify status = PENDING
6.  GET    /payments/{paymentId}  → verify status = PENDING
7.  GET    /inventory/{productId} → verify stock unchanged
8.  PUT    /payments/{paymentId}/status?status=SUCCESS  ← triggers downstream
9.  GET    /orders/{orderId}      → verify status = CONFIRMED
10. GET    /inventory/{productId} → verify stock deducted
11. GET    /cart/{userId}         → verify 404 (cart cleared)
```

---

## Future Improvements

- **JWT Authentication** — Spring Security at the API Gateway level
- **Circuit Breaker** — Resilience4j to handle downstream service failures gracefully
- **Kafka Event Streaming** — async communication for order and payment events
- **Docker + Docker Compose** — containerise all services for portable deployment
- **AWS Deployment** — EC2 or Elastic Beanstalk with real AWS DynamoDB
- **Notification Service** — email/SMS alerts on order and payment status changes

---

## Author

**Deva**
B.Tech — Artificial Intelligence & Data Science
Chennai, Tamil Nadu, India

---

> Built as a portfolio project to demonstrate distributed systems design, Spring Cloud microservices, and AWS-aligned backend architecture.
