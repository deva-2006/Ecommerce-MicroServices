# API Documentation

## E-Commerce Microservices Backend

---

## Base URL

All requests route through the API Gateway:

```
http://localhost:8080
```

Direct service ports (for local development/debugging only):

| Service | Direct Port |
|---|---|
| Product Service | 8081 |
| Cart Service | 8082 |
| Payment Service | 8083 |
| Order Service | 8084 |
| Inventory Service | 8085 |

---

## Common HTTP Status Codes

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 204 | No Content (delete/deduct operations) |
| 400 | Bad Request — validation failed |
| 404 | Resource not found |
| 409 | Conflict — insufficient stock or invalid state |
| 503 | Service Unavailable — downstream service unreachable |
| 500 | Internal Server Error |

---

## Product Service

Base path: `/products`

> Manages product catalog only — name, description, category, price. Stock is managed separately by Inventory Service.

---

### POST /products
Create a new product.

**Request Body**
```json
{
  "name": "Gaming Laptop",
  "description": "High performance gaming laptop",
  "category": "Electronics",
  "price": 75000.00
}
```

**Response — 201 Created**
```json
{
  "productId": "a3f1c2d4-...",
  "name": "Gaming Laptop",
  "description": "High performance gaming laptop",
  "category": "Electronics",
  "price": 75000.00
}
```

---

### GET /products
Retrieve all products.

**Response — 200 OK**
```json
[
  {
    "productId": "a3f1c2d4-...",
    "name": "Gaming Laptop",
    "description": "High performance gaming laptop",
    "category": "Electronics",
    "price": 75000.00
  }
]
```

---

### GET /products/{id}
Retrieve a product by ID.

**Response — 200 OK**
```json
{
  "productId": "a3f1c2d4-...",
  "name": "Gaming Laptop",
  "description": "High performance gaming laptop",
  "category": "Electronics",
  "price": 75000.00
}
```

---

### PUT /products/{id}
Update product details.

**Request Body**
```json
{
  "name": "Gaming Laptop Pro",
  "description": "Updated high performance gaming laptop",
  "category": "Electronics",
  "price": 85000.00
}
```

**Response — 200 OK** — returns updated product object.

---

### DELETE /products/{id}
Delete a product.

**Response — 204 No Content**

---

## Inventory Service

Base path: `/inventory`

> Owns all stock data. Product Service has no stock fields. Every stock operation goes through this service.

---

### POST /inventory
Create an inventory record for a product. Call this immediately after creating a product.

**Request Body**
```json
{
  "productId": "a3f1c2d4-...",
  "quantity": 100
}
```

**Response — 201 Created**
```json
{
  "productId": "a3f1c2d4-...",
  "quantity": 100,
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### GET /inventory/{productId}
Get current stock for a product.

**Response — 200 OK**
```json
{
  "productId": "a3f1c2d4-...",
  "quantity": 98,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### GET /inventory
Get all inventory records.

**Response — 200 OK** — returns array of inventory objects.

---

### GET /inventory/{productId}/validate?quantity=N
Validate whether sufficient stock is available. Used internally by Cart Service and Order Service.

**Query Param** — `quantity` (integer, required)

**Response — 200 OK** — stock is sufficient.
**Response — 409 Conflict** — insufficient stock.

---

### PUT /inventory/{productId}/add-stock?quantity=N
Add stock to an existing inventory record.

**Query Param** — `quantity` (integer, required)

**Response — 200 OK**
```json
{
  "productId": "a3f1c2d4-...",
  "quantity": 150,
  "updatedAt": "2024-01-15T12:00:00"
}
```

---

### PUT /inventory/{productId}/update-stock?quantity=N
Set stock to an absolute value.

**Query Param** — `quantity` (integer, required)

**Response — 200 OK** — returns updated inventory object.

---

### PUT /inventory/{productId}/deduct-stock
Deduct stock. Called internally by Order Service after payment success.

**Request Body**
```json
{
  "quantity": 2
}
```

**Response — 204 No Content**

---

### DELETE /inventory/{productId}
Delete an inventory record.

**Response — 204 No Content**

---

## Cart Service

Base path: `/cart`

> Accepts only `userId`, `productId`, and `quantity` from the client. Fetches product name and price directly from Product Service. Validates stock with Inventory Service before adding. Clients cannot manipulate price or product name.

---

### POST /cart
Add a product to the cart.

**Request Body**
```json
{
  "userId": "user-001",
  "productId": "a3f1c2d4-...",
  "quantity": 2
}
```

> `productName` and `price` are not accepted from the client. They are fetched internally from Product Service.

**Response — 201 Created**
```json
{
  "userId": "user-001",
  "productId": "a3f1c2d4-...",
  "productName": "Gaming Laptop",
  "price": 75000.00,
  "quantity": 2,
  "totalPrice": 150000.00,
  "addedAt": "2024-01-15T10:35:00"
}
```

---

### GET /cart/{userId}
Get all cart items for a user.

**Response — 200 OK**
```json
[
  {
    "userId": "user-001",
    "productId": "a3f1c2d4-...",
    "productName": "Gaming Laptop",
    "price": 75000.00,
    "quantity": 2,
    "totalPrice": 150000.00,
    "addedAt": "2024-01-15T10:35:00"
  }
]
```

---

### PUT /cart/{userId}/{productId}?quantity=N
Update quantity of a cart item. Also validates updated quantity against Inventory Service.

**Query Param** — `quantity` (integer, required)

**Response — 200 OK** — returns updated cart item.

---

### DELETE /cart/{userId}/{productId}
Remove a single item from the cart.

**Response — 204 No Content**

---

### DELETE /cart/{userId}
Clear the entire cart for a user.

**Response — 204 No Content**

---

## Order Service

Base path: `/orders`

> Orchestrates the full order lifecycle. Fetches cart items, validates products and stock, saves the order, and triggers payment creation. Does not accept item details from the client — everything is sourced from other services.

---

### POST /orders
Place a new order.

**Request Body**
```json
{
  "userId": "user-001",
  "shippingAddress": "123, Anna Nagar, Chennai - 600040",
  "paymentMethod": "UPI"
}
```

> Item details are fetched from Cart Service. No `items` array in the request.

**Response — 201 Created**
```json
{
  "orderId": "b7e2d1f3-...",
  "paymentId": "c9a3f2e1-...",
  "userId": "user-001",
  "items": [
    {
      "productId": "a3f1c2d4-...",
      "productName": "Gaming Laptop",
      "quantity": 2,
      "price": 75000.00
    }
  ],
  "totalAmount": 150000.00,
  "status": "PENDING",
  "shippingAddress": "123, Anna Nagar, Chennai - 600040",
  "createdAt": "2024-01-15T10:40:00"
}
```

---

### GET /orders/{id}
Get an order by ID.

**Response — 200 OK** — returns order object including `paymentId` and current `status`.

---

### GET /orders/user/{userId}
Get all orders for a user.

**Response — 200 OK** — returns array of order objects.

---

### PUT /orders/{id}/status?status=VALUE
Manually update order status.

**Query Param** — `status`: `PENDING` | `CONFIRMED` | `SHIPPED` | `DELIVERED` | `CANCELLED`

**Response — 200 OK** — returns updated order object.

---

### POST /orders/{id}/payment-success?userId=VALUE
Internal endpoint called by Payment Service after a successful payment. Triggers stock deduction and cart clearing. Not intended for direct client use.

**Response — 204 No Content**

---

### DELETE /orders/{id}
Delete an order.

**Response — 204 No Content**

---

## Payment Service

Base path: `/payments`

> Manages payment lifecycle. On status update to SUCCESS, notifies Order Service to confirm the order, deduct inventory, and clear the cart.

---

### GET /payments/{id}
Get a payment by ID.

**Response — 200 OK**
```json
{
  "paymentId": "c9a3f2e1-...",
  "orderId": "b7e2d1f3-...",
  "userId": "user-001",
  "amount": 150000.00,
  "paymentMethod": "UPI",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:40:00"
}
```

---

### GET /payments/order/{orderId}
Get all payments for a given order.

**Response — 200 OK** — returns array of payment objects.

---

### GET /payments/user/{userId}
Get all payments made by a user.

**Response — 200 OK** — returns array of payment objects.

---

### PUT /payments/{id}/status?status=VALUE
Update payment status. This is the key trigger for the downstream order confirmation flow.

**Query Param** — `status`: `PENDING` | `SUCCESS` | `FAILED` | `REFUNDED`

**Response — 200 OK**
```json
{
  "paymentId": "c9a3f2e1-...",
  "orderId": "b7e2d1f3-...",
  "userId": "user-001",
  "amount": 150000.00,
  "paymentMethod": "UPI",
  "status": "SUCCESS",
  "createdAt": "2024-01-15T10:40:00"
}
```

**Side effects triggered on SUCCESS:**
- Order status updated to `CONFIRMED`
- Inventory deducted per order item
- Cart cleared for the user

**Side effects triggered on FAILED:**
- Order status updated to `CANCELLED`

**Side effects triggered on REFUNDED:**
- Order status updated to `REFUNDED`

---

## Payment Status → Order Status Mapping

| Payment Status | Order Status |
|---|---|
| `PENDING` | `PENDING` |
| `SUCCESS` | `CONFIRMED` |
| `FAILED` | `CANCELLED` |
| `REFUNDED` | `REFUNDED` |

---

## Error Response Format

All services return errors in the following format:

```json
{
  "error": "Descriptive error message"
}
```

Validation errors return a field-level map:

```json
{
  "userId": "userId is required",
  "quantity": "quantity must be at least 1"
}
```
