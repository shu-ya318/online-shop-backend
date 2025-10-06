# Online Shop Backend

A comprehensive e-commerce backend system built with Spring Boot 3.3.0, providing user management, product catalog, shopping cart, order processing, and payment functionality.

## **Technology Stack**

- **Framework**: Spring Boot 3.3.0
- **Java**: JDK 17
- **Database**: Microsoft SQL Server
- **Cache**: Redis
- **Security**: Spring Security + JWT + OAuth2 (Google)
- **Mapping**: MapStruct
- **Payment**: PayPal SDK
- **Build**: Maven
- **Containerization**: Docker + Tomcat

## **Project Structure**

```
demo/
├── src/main/java/com/project/demo/
│   ├── config/
│   │   ├── ApplicationConfig.java
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── CartController.java
│   │   ├── OrderController.java
│   │   ├── PaymentController.java
│   │   ├── ProductController.java
│   │   └── UserController.java
│   ├── converter/
│   │   ├── StringToCategoryConverter.java
│   │   └── StringToSortDirectionConverter.java
│   ├── data/
│   │   └── PathConstantData.java
│   ├── dto/
│   │   ├── cart/
│   │   ├── common/
│   │   ├── gateway/
│   │   ├── order/
│   │   ├── payment/
│   │   ├── product/
│   │   └── user/
│   ├── enumeration/
│   │   ├── AccountStatus.java
│   │   ├── AuthProvider.java
│   │   ├── AvailabilityStatus.java
│   │   ├── Category.java
│   │   ├── OrderStatus.java
│   │   ├── PaymentMethod.java
│   │   ├── PaymentStatus.java
│   │   └── Role.java
│   ├── exception/
│   │   ├── EntityNotFoundException.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── IncorrectPasswordException.java
│   │   ├── InsufficientStockException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── InvalidPaymentException.java
│   │   ├── InvalidTokenException.java
│   │   ├── OperationNotSupportedException.java
│   │   ├── PaymentGatewayException.java
│   │   ├── UserAlreadyExistsException.java
│   │   └── UserDeletedException.java
│   ├── gateway/
│   │   ├── CashOnDeliveryGateway.java
│   │   ├── PaymentGateway.java
│   │   ├── PaymentGatewayFactory.java
│   │   └── PayPalGateway.java
│   ├── mapper/
│   │   ├── util/
│   │   │   └── PriceCalculationUtils.java
│   │   ├── CartMapper.java
│   │   ├── OrderMapper.java
│   │   ├── PaymentMapper.java
│   │   ├── ProductMapper.java
│   │   └── UserMapper.java
│   ├── model/
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   ├── Product.java
│   │   ├── Sellable.java
│   │   └── User.java
│   ├── repository/
│   │   ├── CartRepository.java
│   │   ├── OrderRepository.java
│   │   ├── PaymentRepository.java
│   │   ├── ProductRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── JwtFilter.java
│   │   ├── JwtUtil.java
│   │   ├── LogoutResultHandler.java
│   │   └── OAuth2AuthSuccessHandler.java
│   ├── service/
│   │   ├── CartService.java
│   │   ├── OrderService.java
│   │   ├── PaymentService.java
│   │   ├── ProductService.java
│   │   ├── RedisService.java
│   │   └── UserService.java
│   ├── specification/
│   │   └── ProductSpecifications.java
│   └── DemoApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── static/
│   └── templates/
├── src/test/java/com/project/demo/
│   └── DemoApplicationTests.java
├── Dockerfile
├── pom.xml
└── README.md
```

## **Quick Start**

### Prerequisites

- JDK 17+
- Maven 3.9+
- Microsoft SQL Server
- Redis
- Docker (optional)

### Installation Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/shu-ya318/online-shop-backend.git
   cd online-shop-backend/demo
   ```

2. **Configure Environment Variables**

3. **Run the Application**

   **Windows:**

   ```bash
   mvnw.cmd spring-boot:run
   ```

   **Unix/Linux/macOS:**

   ```bash
   ./mvnw spring-boot:run
   ```

## **Development Guide**

### Building the Project

```bash
mvn clean package 
```

## **Docker Deployment**

### Building Docker Image

```bash
docker build -t online-shop-backend:latest .
```

### Running Container

```bash
docker run -d -p 8080:8080 --name online-shop-backend-dev -e "SPRING_PROFILES_ACTIVE=docker" online-shop-backend:latest
```

## **Configuration**

### Database Configuration

- Uses Microsoft SQL Server as the primary database
- Supports automatic table structure updates
- Connection pool configuration supports encrypted connections

### Security Configuration

- JWT token authentication
- Google OAuth2 integration
- Role-based access control
- CORS cross-origin support

### Cache Configuration

- Redis for session management and caching
- Supports password protection

### Payment Configuration

- PayPal payment gateway integration
- Supports both sandbox and production environments

# Contact

- **Email**: shuyaHsieh318@gmail.com
- **Cake**: https://www.cake.me/me/shuyahsieh
- **Linkedin**: https://www.linkedin.com/in/%E6%B7%91%E9%9B%85-%E8%AC%9D-9906772b1/
