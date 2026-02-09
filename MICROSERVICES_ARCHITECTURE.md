# 🏗️ Rewards360 — Microservices Architecture

## 📊 Architecture Overview Diagram

```
                          ┌──────────────────────────────────┐
                          │         FRONTEND (React)         │
                          │        localhost:5173             │
                          └──────────┬───────────────────────┘
                                     │
                          ┌──────────▼───────────────────────┐
                          │       API GATEWAY (Optional)      │
                          │    Routes requests to services    │
                          └──┬───────────┬───────────────┬───┘
                             │           │               │
              ┌──────────────▼──┐  ┌─────▼──────────┐  ┌▼──────────────────┐
              │  AUTH SERVICE   │  │  USER SERVICE   │  │  ADMIN SERVICE    │
              │  Port: 8081    │  │  Port: 8082     │  │  Port: 8083       │
              │                │  │                 │  │                   │
              │ /api/auth/**   │  │ /api/user/**    │  │ /api/admin/**     │
              └───────┬────────┘  └────────┬────────┘  └─────────┬─────────┘
                      │                    │                     │
              ┌───────▼────────┐  ┌────────▼────────┐  ┌────────▼─────────┐
              │   auth_db      │  │   user_db       │  │   admin_db       │
              │  (MySQL)       │  │  (MySQL)        │  │  (MySQL)         │
              │                │  │                 │  │                  │
              │ • users        │  │ • users (read)  │  │ • campaigns      │
              │ • customer_    │  │ • customer_     │  │ • offers         │
              │   profile      │  │   profile       │  │ • fraud_alerts   │
              │                │  │ • offers        │  │ • anomalies      │
              │                │  │ • transactions  │  │ • audit_logs     │
              │                │  │ • redemptions   │  │                  │
              └────────────────┘  └─────────────────┘  └──────────────────┘
```

---

## 🔄 Monolith vs Microservices Mapping

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MONOLITH (Current)                                   │
│                                                                         │
│  backend/ (single Spring Boot app on port 8080)                        │
│  ├── AuthController     ──────────►   AUTH-SERVICE   (port 8081)       │
│  ├── UserController     ──────────►   USER-SERVICE   (port 8082)       │
│  ├── AdminController    ──────────►   ADMIN-SERVICE  (port 8083)       │
│  ├── JwtService         ──────────►   Shared in AUTH, validated in all │
│  ├── PointsService      ──────────►   USER-SERVICE                     │
│  ├── SecurityConfig     ──────────►   Each service has its own         │
│  └── All Models/Repos   ──────────►   Split per service domain         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Complete Microservices Project Structure

```
rewards360-microservices/
│
├── 📄 pom.xml                          ← Parent POM (multi-module Maven)
├── 📄 README.md
├── 📄 docker-compose.yml               ← Run all services together
│
├── 📂 common-lib/                      ← Shared library (DTOs, JWT, exceptions)
│   ├── 📄 pom.xml
│   └── src/main/java/com/rewards360/common/
│       ├── 📂 dto/
│       │   ├── AuthResponse.java
│       │   ├── LoginRequest.java
│       │   ├── RegisterRequest.java
│       │   ├── ClaimRequest.java
│       │   ├── RedeemRequest.java
│       │   └── UserDTO.java            ← For inter-service communication
│       ├── 📂 exception/
│       │   ├── ErrorResponse.java
│       │   ├── GlobalExceptionHandler.java
│       │   ├── UserNotFoundException.java
│       │   ├── OfferNotFoundException.java
│       │   └── InsufficientPointsException.java
│       ├── 📂 security/
│       │   ├── JwtService.java         ← Shared JWT token logic
│       │   └── JwtAuthFilter.java      ← Shared JWT filter
│       └── 📂 model/
│           └── Role.java               ← Shared enum
│
├── 📂 auth-service/                    ← 🔐 Authentication & Registration
│   ├── 📄 pom.xml
│   ├── 📄 Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/rewards360/auth/
│       │   │   ├── AuthServiceApplication.java
│       │   │   ├── 📂 config/
│       │   │   │   └── SecurityConfig.java
│       │   │   ├── 📂 controller/
│       │   │   │   └── AuthController.java
│       │   │   ├── 📂 model/
│       │   │   │   ├── User.java
│       │   │   │   └── CustomerProfile.java
│       │   │   ├── 📂 repository/
│       │   │   │   └── UserRepository.java
│       │   │   └── 📂 service/
│       │   │       └── CustomUserDetailsService.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│           └── java/com/rewards360/auth/
│               └── AuthControllerTest.java
│
├── 📂 user-service/                    ← 👤 User Profile, Points, Offers, Transactions
│   ├── 📄 pom.xml
│   ├── 📄 Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/rewards360/user/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── 📂 config/
│       │   │   │   └── SecurityConfig.java
│       │   │   ├── 📂 controller/
│       │   │   │   └── UserController.java
│       │   │   ├── 📂 model/
│       │   │   │   ├── User.java
│       │   │   │   ├── CustomerProfile.java
│       │   │   │   ├── Offer.java
│       │   │   │   ├── Transaction.java
│       │   │   │   └── Redemption.java
│       │   │   ├── 📂 repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── OfferRepository.java
│       │   │   │   ├── TransactionRepository.java
│       │   │   │   └── RedemptionRepository.java
│       │   │   └── 📂 service/
│       │   │       └── PointsService.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│           └── java/com/rewards360/user/
│               └── UserControllerTest.java
│
├── 📂 admin-service/                   ← 🛡️ Campaigns, Offers CRUD, Fraud, Audit
│   ├── 📄 pom.xml
│   ├── 📄 Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/rewards360/admin/
│       │   │   ├── AdminServiceApplication.java
│       │   │   ├── 📂 config/
│       │   │   │   └── SecurityConfig.java
│       │   │   ├── 📂 controller/
│       │   │   │   └── AdminController.java
│       │   │   ├── 📂 model/
│       │   │   │   ├── Campaign.java
│       │   │   │   ├── Offer.java
│       │   │   │   ├── FraudAlert.java
│       │   │   │   ├── Anomaly.java
│       │   │   │   └── AuditLog.java
│       │   │   └── 📂 repository/
│       │   │       ├── CampaignRepository.java
│       │   │       ├── OfferRepository.java
│       │   │       ├── FraudAlertRepository.java
│       │   │       ├── AnomalyRepository.java
│       │   │       └── AuditLogRepository.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│           └── java/com/rewards360/admin/
│               └── AdminControllerTest.java
│
└── 📂 api-gateway/                     ← 🌐 Spring Cloud Gateway (Optional)
    ├── 📄 pom.xml
    ├── 📄 Dockerfile
    └── src/
        ├── main/
        │   ├── java/com/rewards360/gateway/
        │   │   └── ApiGatewayApplication.java
        │   └── resources/
        │       └── application.yml
        └── test/
```

---

## 📋 Service Breakdown — What Goes Where

### 🔐 Service 1: `auth-service` (Port 8081)

| Component | Source File | Responsibility |
|-----------|-----------|----------------|
| **Controller** | `AuthController.java` | `/api/auth/register`, `/api/auth/login` |
| **Model** | `User.java` | User entity (write operations) |
| **Model** | `CustomerProfile.java` | Profile creation during registration |
| **Repository** | `UserRepository.java` | CRUD on `users` table |
| **Service** | `CustomUserDetailsService.java` | Load user for authentication |
| **Config** | `SecurityConfig.java` | Permit `/api/auth/**`, stateless sessions |
| **Shared** | `JwtService.java` (from common-lib) | Generate JWT tokens |
| **Shared** | `JwtAuthFilter.java` (from common-lib) | Validate JWT (minimal usage here) |

**API Endpoints:**
```
POST  /api/auth/register   → Register new user + create profile
POST  /api/auth/login      → Authenticate and return JWT token
```

---

### 👤 Service 2: `user-service` (Port 8082)

| Component | Source File | Responsibility |
|-----------|-----------|----------------|
| **Controller** | `UserController.java` | `/api/user/**` endpoints |
| **Model** | `User.java` | User entity (read for profile) |
| **Model** | `CustomerProfile.java` | Points, tier, preferences |
| **Model** | `Offer.java` | Read offers for redemption |
| **Model** | `Transaction.java` | Transaction records |
| **Model** | `Redemption.java` | Redemption records |
| **Repository** | `UserRepository.java` | Find user by email |
| **Repository** | `OfferRepository.java` | Query offers by tier |
| **Repository** | `TransactionRepository.java` | User's transactions |
| **Repository** | `RedemptionRepository.java` | User's redemptions |
| **Service** | `PointsService.java` | Claim points, redeem offers |
| **Config** | `SecurityConfig.java` | Require auth for `/api/user/**` |
| **Shared** | `JwtAuthFilter.java` (from common-lib) | Validate JWT on requests |

**API Endpoints:**
```
GET   /api/user/me            → Get current user profile
GET   /api/user/offers        → Get all active offers
GET   /api/user/offers/my-tier → Get tier-specific offers
POST  /api/user/claim         → Claim points
POST  /api/user/redeem        → Redeem an offer
GET   /api/user/transactions  → Get transaction history
GET   /api/user/redemptions   → Get redemption history
```

---

### 🛡️ Service 3: `admin-service` (Port 8083)

| Component | Source File | Responsibility |
|-----------|-----------|----------------|
| **Controller** | `AdminController.java` | `/api/admin/**` endpoints |
| **Model** | `Campaign.java` | Campaign management |
| **Model** | `Offer.java` | Offer CRUD (admin creates) |
| **Model** | `FraudAlert.java` | Fraud monitoring |
| **Model** | `Anomaly.java` | Anomaly detection |
| **Model** | `AuditLog.java` | Audit trail |
| **Repository** | `CampaignRepository.java` | Campaign CRUD |
| **Repository** | `OfferRepository.java` | Offer CRUD |
| **Repository** | `FraudAlertRepository.java` | Fraud alerts |
| **Repository** | `AnomalyRepository.java` | Anomaly records |
| **Repository** | `AuditLogRepository.java` | Audit log records |
| **Config** | `SecurityConfig.java` | Require `ROLE_ADMIN` for all |
| **Shared** | `JwtAuthFilter.java` (from common-lib) | Validate JWT + role check |

**API Endpoints:**
```
POST  /api/admin/campaigns     → Create campaign
GET   /api/admin/campaigns     → List all campaigns
POST  /api/admin/offers        → Create offer
GET   /api/admin/offers        → List all offers
GET   /api/admin/fraud/alerts  → Get fraud alerts
GET   /api/admin/fraud/anomalies → Get anomalies
GET   /api/admin/fraud/audit   → Get audit logs
```

---

## 🗃️ Database Strategy (Database-per-Service)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    auth_db       │    │    user_db       │    │    admin_db      │
│    (MySQL)       │    │    (MySQL)       │    │    (MySQL)       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • users          │    │ • users (read   │    │ • campaigns      │
│ • customer_      │    │   replica/sync) │    │ • offers         │
│   profile        │    │ • customer_     │    │ • fraud_alerts   │
│                  │    │   profile       │    │ • anomalies      │
│                  │    │ • offers        │    │ • audit_logs     │
│                  │    │ • transactions  │    │                  │
│                  │    │ • redemptions   │    │                  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

> **Note:** For simplicity, all 3 services can share the SAME database initially and split later. The structure above is the ideal eventual state.

---

## 🔧 Key Configuration Files Per Service

### `application.yml` for each service:

**auth-service** (port 8081):
```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:mysql://localhost:3306/rewards360_auth
    username: root
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: YXNka2p3ZWprYmZhc2RqazEyMzQ1Njc4OTBhYmNkZWY=
    expiryMillis: 86400000
```

**user-service** (port 8082):
```yaml
server:
  port: 8082

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:mysql://localhost:3306/rewards360_user
    username: root
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: YXNka2p3ZWprYmZhc2RqazEyMzQ1Njc4OTBhYmNkZWY=
    expiryMillis: 86400000
```

**admin-service** (port 8083):
```yaml
server:
  port: 8083

spring:
  application:
    name: admin-service
  datasource:
    url: jdbc:mysql://localhost:3306/rewards360_admin
    username: root
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: YXNka2p3ZWprYmZhc2RqazEyMzQ1Njc4OTBhYmNkZWY=
    expiryMillis: 86400000
```

---

## 🐳 Docker Compose

```yaml
version: '3.8'
services:
  auth-service:
    build: ./auth-service
    ports: ["8081:8081"]
    environment:
      - DB_PASSWORD=secret
    depends_on: [mysql]

  user-service:
    build: ./user-service
    ports: ["8082:8082"]
    environment:
      - DB_PASSWORD=secret
    depends_on: [mysql]

  admin-service:
    build: ./admin-service
    ports: ["8083:8083"]
    environment:
      - DB_PASSWORD=secret
    depends_on: [mysql]

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: secret
    ports: ["3306:3306"]
```

---

## 🔀 Inter-Service Communication

```
┌──────────────┐        REST/Feign         ┌──────────────┐
│ auth-service │ ◄──── JWT Token shared ──► │ user-service │
│              │  (same secret key)         │              │
└──────────────┘                            └──────────────┘
       │                                           │
       │            REST/Feign                     │
       └──────── JWT Token shared ─────────────────┘
                                            ┌──────────────┐
                                            │admin-service │
                                            └──────────────┘

Communication Pattern:
• Auth generates JWT → User & Admin validate the SAME JWT
• All services share the SAME jwt.secret via common-lib
• For cross-service calls: Use Spring Cloud OpenFeign or RestTemplate
```

---

## 📦 Required Files Per Service (Total Count)

| Service | Files Count | Key Files |
|---------|------------|-----------|
| **common-lib** | 12 | DTOs, JWT, Exceptions, Role enum |
| **auth-service** | 8 | Application, Controller, Config, Models(2), Repo, Service |
| **user-service** | 12 | Application, Controller, Config, Models(4), Repos(4), Service |
| **admin-service** | 10 | Application, Controller, Config, Models(5), Repos(5) |
| **api-gateway** | 2 | Application, Config |
| **Root** | 3 | Parent POM, Docker Compose, README |
| **Total** | **~47 files** | |

---

## 🚀 Migration Steps (Recommended Order)

1. **Create parent POM** → Multi-module Maven project
2. **Create `common-lib`** → Extract shared DTOs, JWT, exceptions
3. **Create `auth-service`** → Move AuthController + User model + registration
4. **Create `user-service`** → Move UserController + PointsService + user models
5. **Create `admin-service`** → Move AdminController + admin models/repos
6. **Create `api-gateway`** → Route `/api/auth/**`, `/api/user/**`, `/api/admin/**`
7. **Update frontend** → Point API calls to gateway or individual ports
8. **Dockerize** → Add Dockerfiles + docker-compose.yml
9. **Test** → Verify all endpoints work through the new architecture

---

## ✅ Summary

| Aspect | Monolith (Before) | Microservices (After) |
|--------|-------------------|----------------------|
| **Codebase** | 1 Spring Boot app | 3 services + 1 shared lib + 1 gateway |
| **Port** | 8080 | 8081, 8082, 8083 |
| **Database** | 1 shared DB | 3 databases (or shared initially) |
| **Deployment** | Single JAR | Independent JARs / Docker containers |
| **Scaling** | Scale everything | Scale only what's needed |
| **Failure** | All-or-nothing | Isolated failures |
