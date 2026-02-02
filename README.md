#  OPD Token Allocation Engine

##  Project Overview

The **OPD Token Allocation Engine** is a backend system designed to manage hospital OPD (Outpatient Department) token allocation in a realistic, real-world environment.

The system supports:
- Slot-based doctor schedules
- Multiple token sources (online, walk-in, paid, emergency, follow-up)
- Priority-based token allocation
- Dynamic reallocation using waitlists
- Handling cancellations and no-shows with grace periods

This project was built as part of a **Backend Intern Assignment**, focusing on **real-world edge cases, correctness, and clean backend design**.

---

##  Tech Stack

- Java 17  
- Spring Boot  
- Spring Data JPA
- MySQL (used for persistent storage / production-like setup)
- H2 Database (in-memory, for demo)  
- Swagger / OpenAPI  
- Maven  
- Docker  

---

## Key Features

-  Slot-based OPD scheduling per doctor  
-  Priority-based token allocation & preemption  
-  Waitlist management with automatic promotion  
-  Token cancellation handling  
-  No-show handling with configurable grace period  
-  Paginated reporting APIs  
-  Concurrency-safe allocation logic  

---

##  API Documentation (Swagger)

Swagger UI is enabled using **springdoc-openapi**.

### When running locally:
This is URL For swagger:
http://localhost:8080/swagger-ui/index.html

Swagger provides:
- Complete API list
- Request/response schemas
- Try-it-out support

> Swagger is generated at runtime, so it does not appear as files in GitHub.

## 🗄 Database Configuration

This project supports **both H2 and MySQL**, depending on the environment.

---

### 🔹 H2 Database (Default – Local / Demo)

H2 is used for quick local development and demos.

**H2 Console URL:**


### H2 Console URL:
http://localhost:8080/h2-console


Note: H2 is an in-memory database, so all data is lost when the application restarts.

---

### 🔹 MySQL Database (Production / Persistent Storage)

The project also supports **MySQL** for persistent data storage.  
MySQL configuration is defined in `application.properties`.

**Sample MySQL Configuration:**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/opd_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

```
## API Endpoints

1.Allocate/Create Token

-Allocates a token based on slot availability and priority rules.

POST /api/v1/token

2.Get Booked Token Information

-Get Information Of Patient Who have Booked Appointment Based on Start And End Date.

GET /api/v1/token/booked

3.Cancel/Delete Token

-Cancels a booked token and promotes the next eligible waitlisted patient.

DELETE /api/v1/token/{id}

4.NO_SHOW

-Marks a token as NO_SHOW after the grace period and reallocates the slot.

POST /api/v1/token/{tokenId}/no-show


## How to Run Locally (Without Docker)

```bash
mvn clean package
java -jar target/opd-token-allocation-engine-0.0.1-SNAPSHOT.jar
Then open:
Swagger → http://localhost:8080/swagger-ui/index.html
H2 Console → http://localhost:8080/h2-console
```

## Run Using Docker

```bash
docker build -t opd-token-allocation-engine .
docker build -t opd-token-allocation-engine .
Then Open:
Swagger → http://localhost:8080/swagger-ui/index.html
H2 Console → http://localhost:8080/h2-console
```

## Project Documentation

Detailed system design, allocation logic, edge cases, and trade-offs are available in doc folder







