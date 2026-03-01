# Order Management System

A production-level **Order Management System** built with **Spring Boot**, demonstrating advanced Java concepts and enterprise design patterns. This system mimics a simplified trading/e-commerce backend.

## 🎯 Features

### ✅ Core Requirements Implemented

1. **REST API with Spring Boot + MVC**
   - RESTful endpoints for order operations
   - Spring MVC pattern with separation of concerns
   - Global exception handling with custom exceptions
   - API response wrapper for consistent responses

2. **In-Memory Data Storage (Collections + Generics)**
   - Thread-safe `ConcurrentHashMap` for order storage
   - Generic DAO pattern for data access
   - Type-safe collections management

3. **File I/O & Persistence**
   - Persistent order logging to text files
   - CSV audit logs for compliance
   - Timestamp-based logging entries
   - Log file management utilities

4. **Concurrent Processing (Multithreading)**
   - Fixed thread pool executor for async order processing
   - Non-blocking order status updates
   - Thread-safe operations with proper synchronization
   - Graceful shutdown mechanism

5. **Analytics using Java Streams**
   - Total order amount calculation
   - Buy vs Sell order statistics
   - Top customer by volume analysis
   - Grouping orders by status
   - Grouping orders by type and customer
   - Advanced stream operations with collectors

6. **Custom Exception Handling**
   - `OrderValidationException` - Validation errors
   - `OrderNotFoundException` - Order not found
   - `OrderProcessingException` - Processing failures
   - `OrderLogException` - File I/O errors
   - Global exception handler with meaningful error messages

7. **Spring IoC & Dependency Injection**
   - Constructor-based dependency injection
   - Spring component scanning
   - Service layer with proper responsibilities
   - Configuration classes for bean management

8. **Production-Level Design Patterns**
   - **MVC Architecture** - Separation of concerns
   - **DAO Pattern** - Data Access Object for repository operations
   - **DTO Pattern** - Data Transfer Objects for API requests/responses
   - **Service Layer** - Business logic encapsulation
   - **Builder Pattern** - Object creation with flexibility
   - **Factory Pattern** - Order creation with defaults

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/orderdemo/order/
│   │   ├── controller/           # REST Controllers (MVC)
│   │   │   ├── OrderController.java
│   │   │   └── OrderAnalyticsController.java
│   │   ├── service/              # Business Logic
│   │   │   ├── OrderService.java
│   │   │   └── OrderAnalyticsService.java
│   │   ├── dao/                  # Data Access Objects
│   │   │   └── OrderDAO.java
│   │   ├── entity/               # Domain Models
│   │   │   ��── Order.java
│   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── OrderDTO.java
│   │   │   ├── CreateOrderRequest.java
│   │   │   ├── OrderAnalyticsDTO.java
│   │   │   └── ApiResponse.java
│   │   ├── enums/                # Enumerations
│   │   │   ├── OrderType.java
│   │   │   └── OrderStatus.java
│   │   ├── exception/            # Custom Exceptions
│   │   │   ├── OrderValidationException.java
│   │   │   ├── OrderNotFoundException.java
│   │   │   ├── OrderProcessingException.java
│   │   │   ├── OrderLogException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── util/                 # Utilities
│   │   │   └── OrderLogUtil.java
│   │   ├── config/               # Configuration
│   │   │   └── AppConfig.java
│   │   └── OrderApplication.java # Main Application
│   └── resources/
│       └── application.properties
└── test/
    └── OrderApplicationTests.java
```

## 🔧 Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 25
- **Build Tool**: Maven
- **Libraries**:
  - Lombok (for reducing boilerplate)
  - Spring Web MVC
  - JUnit 5 (for testing)

## 📊 Order Model

```java
Order {
  orderId: String (UUID)
  customerId: String
  symbol: String (Trading symbol)
  orderType: OrderType (BUY/SELL)
  quantity: BigDecimal
  price: BigDecimal
  totalAmount: BigDecimal (quantity × price)
  status: OrderStatus (PENDING, PROCESSING, COMPLETED, CANCELLED, FAILED)
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
  notes: String
}
```

## 🚀 Getting Started

### Prerequisites
- Java 25+
- Maven 3.6+

### Installation

```bash
# Clone or navigate to project
cd Order

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Demo Data
On startup, the application automatically creates 4 sample orders and displays comprehensive analytics.

## 📡 REST API Endpoints

### Order Management

#### Create Order
```http
POST /api/v1/orders
Content-Type: application/json

{
  "customerId": "CUST001",
  "symbol": "APPL",
  "orderType": "BUY",
  "quantity": 10,
  "price": 150.00
}

Response: 201 Created
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "customerId": "CUST001",
    "symbol": "APPL",
    "orderType": "BUY",
    "quantity": 10,
    "price": 150.00,
    "totalAmount": 1500.00,
    "status": "PENDING",
    "createdAt": "2026-03-01T10:30:00"
  }
}
```

#### Get Order by ID
```http
GET /api/v1/orders/{orderId}

Response: 200 OK
```

#### Get All Orders
```http
GET /api/v1/orders

Response: 200 OK
```

#### Get Orders by Customer
```http
GET /api/v1/orders/customer/{customerId}

Response: 200 OK
```

#### Get Orders by Status
```http
GET /api/v1/orders/status/{status}

Parameters:
- status: PENDING, PROCESSING, COMPLETED, CANCELLED, FAILED

Response: 200 OK
```

#### Get Orders by Symbol
```http
GET /api/v1/orders/symbol/{symbol}

Response: 200 OK
```

#### Update Order Status
```http
PATCH /api/v1/orders/{orderId}/status?status=PROCESSING&notes=Processing

Response: 200 OK
```

#### Cancel Order
```http
PUT /api/v1/orders/{orderId}/cancel?reason=User%20requested

Response: 200 OK
```

#### Delete Order
```http
DELETE /api/v1/orders/{orderId}

Response: 200 OK
```

### Analytics

#### Get Order Analytics
```http
GET /api/v1/analytics

Response: 200 OK
{
  "success": true,
  "message": "Analytics retrieved successfully",
  "data": {
    "totalOrderAmount": 15150.00,
    "totalBuyOrders": 3,
    "totalSellOrders": 1,
    "topCustomerByVolume": "CUST001",
    "topCustomerVolume": 7500.00,
    "totalOrders": 4,
    "ordersByStatus": {
      "PENDING": 1,
      "PROCESSING": 1,
      "COMPLETED": 2
    },
    "amountByOrderType": {
      "BUY": 7500.00,
      "SELL": 14000.00
    },
    "ordersByCustomer": {
      "CUST001": 2,
      "CUST002": 1,
      "CUST003": 1
    }
  }
}
```

## 🧪 Testing

Run the comprehensive test suite:

```bash
mvn test
```

### Test Coverage

- ✅ Order creation with validation
- ✅ Retrieve orders (by ID, all, by customer, by status)
- ✅ Update order status
- ✅ Cancel orders
- ✅ Delete orders
- ✅ Analytics calculations
- ✅ Exception handling

## 📝 Logging

### Log Files

1. **order_logs.txt** - Transaction and operational logs
   ```
   [2026-03-01 10:30:00] Order ID: 550e8400-e29b-41d4-a716-446655440000, 
   Customer: CUST001, Symbol: APPL, Type: BUY, Status: PENDING
   ```

2. **order_audit.csv** - Audit trail in CSV format
   ```
   OrderId,CustomerId,Symbol,OrderType,Quantity,Price,TotalAmount,Status,CreatedAt
   550e8400-e29b-41d4-a716-446655440000,CUST001,APPL,BUY,10,150.00,1500.00,PENDING,2026-03-01 10:30:00
   ```

## 🔑 Key Design Patterns

### 1. MVC Pattern
- **Controllers** handle HTTP requests
- **Services** contain business logic
- **DTOs** transfer data between layers

### 2. DAO Pattern
- Single responsibility for data access
- Abstract data storage implementation
- Easy to switch from in-memory to database

### 3. Dependency Injection
```java
@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final OrderLogUtil orderLogUtil;
    
    @Autowired
    public OrderService(OrderDAO orderDAO, OrderLogUtil orderLogUtil) {
        this.orderDAO = orderDAO;
        this.orderLogUtil = orderLogUtil;
    }
}
```

### 4. Stream API for Analytics
```java
public String getTopCustomerByVolume(List<Order> orders) {
    return orders.stream()
        .collect(Collectors.groupingBy(
            Order::getCustomerId,
            Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
        ))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse("N/A");
}
```

### 5. Multithreading for Async Processing
```java
private void processOrderAsync(String orderId) {
    executorService.submit(() -> {
        // Simulate order processing
        // Update status after completion
    });
}
```

## 🛡️ Exception Handling

Custom exceptions provide meaningful error messages:

```java
try {
    orderService.createOrder(request);
} catch (OrderValidationException e) {
    // Handle validation errors (400)
} catch (OrderNotFoundException e) {
    // Handle not found errors (404)
} catch (OrderProcessingException e) {
    // Handle processing errors (500)
} catch (OrderLogException e) {
    // Handle logging errors (500)
}
```

## 🔐 Thread Safety

- **ConcurrentHashMap** for thread-safe order storage
- **ExecutorService** for safe concurrent processing
- **Immutable DTOs** for data transfer
- **Synchronization** in critical sections

## 📈 Performance Considerations

1. **In-Memory Storage**: Fast operations without database latency
2. **Async Processing**: Non-blocking order operations
3. **Thread Pool**: Limited resource consumption
4. **Stream Operations**: Lazy evaluation for efficiency

## 🚀 Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] Authentication & Authorization (Spring Security)
- [ ] Pagination for large datasets
- [ ] Caching (Redis)
- [ ] Message queues (RabbitMQ/Kafka)
- [ ] Monitoring & Metrics (Micrometer)
- [ ] WebSocket for real-time updates
- [ ] Advanced search & filtering
- [ ] Order matching engine

## 📄 License

This project is for educational purposes.

## 👨‍💻 Author

Developed as a comprehensive demonstration of enterprise Java development patterns and best practices.

---

**Happy Trading! 📊**

