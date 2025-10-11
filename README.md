# Online Shop Backend

This is a modern e-commerce backend built with Spring Boot 3.3.0, providing user management, product catalog, shopping cart, order processing, and payment functionality.

## Technology Stack

- **Framework**: Spring Boot 3.3.0
- **Java**: JDK 17
- **Database**: Microsoft SQL Server
- **Cache**: Redis
- **Security**: Spring Security + JWT + OAuth2 (Google)
- **Mapping**: MapStruct 1.5.5.Final
- **Payment**: PayPal SDK 1.14.0
- **Build**: Maven 3.9.6
- **Containerization**: Docker + Tomcat 10.1.46

## Database Schema
![Schema](docs/schema.png)

## Getting Start

### Prerequisites

- JDK 17+
- Maven 3.9.6+
- Microsoft SQL Server
- Redis
- Docker (optional)

### Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/shu-ya318/online-shop-backend.git
   cd online-shop-backend
   ```

2. **Configure Environment Variables**
   
   The main configuration file is `src/main/resources/application.properties`. By default, it uses the `local` profile (`spring.profiles.active=local`).
   
   For local development, you can create an `application-local.properties` file in the same directory to override default settings (e.g., database credentials, JWT secret).
   
   For Docker deployment, create an `application-docker.properties` file.

### Development

Start the development server:

**Windows:**

```bash
.\\demo\\mvnw.cmd spring-boot:run
```

**Unix/Linux/macOS:**

```bash
./demo/mvnw spring-boot:run
```

### Building

**Windows:**

```bash
.\\demo\\mvnw.cmd clean package
```

**Unix/Linux/macOS:**

```bash
./demo/mvnw clean package
```

### Containerization (Docker)

This project is designed to be managed and deployed using Docker Compose from a parent directory. Please refer to the `docker-compose.yml` file in the parent directory for instructions on building and running the services.

## Project Structure

```
online-shop-backend/
├── .gitignore
├── Dockerfile
├── README.md
├── apache-tomcat-10.1.46.tar.gz
└── demo/
    ├── .mvn/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/project/demo/
    │   │   │   ├── config/
    │   │   │   ├── controller/
    │   │   │   ├── converter/
    │   │   │   ├── data/
    │   │   │   ├── dto/
    │   │   │   ├── enumeration/
    │   │   │   ├── exception/
    │   │   │   ├── gateway/
    │   │   │   ├── mapper/
    │   │   │   ├── model/
    │   │   │   ├── repository/
    │   │   │   ├── security/
    │   │   │   ├── service/
    │   │   │   ├── specification/
    │   │   │   └── DemoApplication.java
    │   │   └── resources/
    │   │       ├── application-docker.properties
    │   │       ├── application-local.properties
    │   │       ├── application.properties
    │   └── test/
    ├── mvnw
    ├── mvnw.cmd
    └── pom.xml
```

## Configuration

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

## Contact

- **Email**: shuyaHsieh318@gmail.com
- **Cake**: https://www.cake.me/me/shuyahsieh
- **Linkedin**: https://www.linkedin.com/in/%E6%B7%91%E9%9B%85-%E8%AC%9D-9906772b1/
