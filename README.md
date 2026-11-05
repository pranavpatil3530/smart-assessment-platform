# 🎓 Smart Online Examination & Assessment Platform

> **An enterprise-grade, full-stack assessment portal built with Java 17+, Spring Boot 3.3, Spring Security 6, JWT, Spring Data JPA, Hibernate, MySQL, and React.js.**

Designed from the ground up to demonstrate production-level architectural principles, defense-in-depth security, concurrent test session management, randomized question delivery, automated MCQ evaluation with negative marking, hands-on coding assessment workspaces, and performance analytics.

---

## 📑 Table of Contents

- [Architectural Overview](#-architectural-overview)
- [Database Design & Schema (ERD)](#-database-design--schema-erd)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Project Directory Structure](#-project-directory-structure)
- [REST API Specifications](#-rest-api-specifications)
- [Core Business Logic & Algorithms](#-core-business-logic--algorithms)
- [Unit Testing with JUnit 5 & Mockito](#-unit-testing-with-junit-5--mockito)
- [Local Setup & Execution Guide](#-local-setup--execution-guide)
- [Postman Testing Guide](#-postman-testing-guide)
- [Java Full Stack Interview Defense Guide](#-java-full-stack-interview-defense-guide)

---

## 🏛️ Architectural Overview

The application follows the **Clean Layered Architecture (Controller-Service-Repository)** pattern with strict separation of concerns, decoupling domain entities from external API contracts via Data Transfer Objects (DTOs).

```
                               ┌────────────────────────────────────────┐
                               │           React.js Frontend            │
                               │  (Axios, Context API, Custom Hooks)   │
                               └──────────────────┬─────────────────────┘
                                                  │ HTTP / REST / JWT
                                                  ▼
                               ┌────────────────────────────────────────┐
                               │       Spring Security 6 & JWT          │
                               │  - JwtAuthenticationFilter (OncePerReq)│
                               │  - SecurityFilterChain (Stateless)     │
                               │  - BCryptPasswordEncoder               │
                               └──────────────────┬─────────────────────┘
                                                  │
                                                  ▼
                               ┌────────────────────────────────────────┐
                               │           Controller Layer             │
                               │  (@RestController, @Valid, DTOs)       │
                               └──────────────────┬─────────────────────┘
                                                  │
                                                  ▼
                               ┌────────────────────────────────────────┐
                               │             Service Layer              │
                               │  (@Service, @Transactional boundaries) │
                               │  - ExamAttemptService (Evaluation)     │
                               │  - ExamService / QuestionService       │
                               │  - AnalyticsService                    │
                               └──────────────────┬─────────────────────┘
                                                  │
                                                  ▼
                               ┌────────────────────────────────────────┐
                               │        Repository / Data Layer         │
                               │  (Spring Data JPA, Hibernate ORM)      │
                               └──────────────────┬─────────────────────┘
                                                  │
                                                  ▼
                               ┌────────────────────────────────────────┐
                               │         MySQL 8.0+ Database            │
                               │  (InnoDB, Foreign Keys, Indexes)       │
                               └────────────────────────────────────────┘
```

### Architectural Highlights

1. **Stateless Authentication**: Eliminates server-side session memory overhead by using signed JSON Web Tokens (HMAC-SHA256).
2. **Data Transfer Object (DTO) Boundary**: Persistence models (`User`, `Exam`, `Question`) are never exposed directly to the REST layer. DTOs prevent infinite JSON recursion and data leakage (e.g., hiding `isCorrect` and explanations during active tests).
3. **Global Exception Handling**: Centralized `@RestControllerAdvice` intercepts `MethodArgumentNotValidException`, `ResourceNotFoundException`, `ExamAlreadySubmittedException`, and authentication errors, returning consistent `ErrorResponse` JSON objects.
4. **Resilient Session Management**: The examination session computes the deadline timestamp on the server (`endTime = startTime + durationMinutes`). Client refreshes re-sync to the server deadline, preventing timer tampering.

---

## 🗄️ Database Design & Schema (ERD)

The relational schema is normalized to 3NF and enforces referential integrity through foreign keys with cascading actions.

```
┌──────────────────┐          ┌──────────────────┐          ┌──────────────────┐
│      users       │          │    categories    │          │      exams       │
├──────────────────┤          ├──────────────────┤          ├──────────────────┤
│ id (PK)          │          │ id (PK)          │◄────┐    │ id (PK)          │
│ username (UQ)    │          │ name (UQ)        │     └────┼─ category_id (FK)│
│ email (UQ)       │          │ description      │          │ title            │
│ password         │          │ created_at       │          │ duration_minutes │
│ full_name        │          │ updated_at       │          │ total_marks      │
│ role             │          └──────────────────┘          │ passing_marks    │
│ enabled          │                                        │ negative_marking │
│ created_at       │                                        │ max_attempts     │
│ updated_at       │                                        │ is_published     │
└────────┬─────────┘                                        │ randomize_q      │
         │                                                  │ randomize_opt    │
         │                                                  └────────┬─────────┘
         │                                                           │
         │                                                           │ 1:N
         │                                                           ▼
         │ 1:N        ┌────────────────────────────────────────────────────────┐
         │            │                       questions                        │
         │            ├────────────────────────────────────────────────────────┤
         │            │ id (PK), exam_id (FK), question_type (MCQ/CODING)      │
         │            │ content, marks, negative_marks, explanation            │
         │            └───────────┬──────────────────────────────┬─────────────┘
         │                        │ 1:N                          │ 1:1
         │                        ▼                              ▼
         │            ┌──────────────────────┐       ┌────────────────────────┐
         │            │   question_options   │       │    coding_problems     │
         │            ├──────────────────────┤       ├────────────────────────┤
         │            │ id (PK)              │       │ id (PK)                │
         │            │ question_id (FK)     │       │ question_id (FK, UQ)   │
         │            │ option_text          │       │ starter_code, language │
         │            │ option_order         │       │ constraints, sample_in │
         │            │ is_correct           │       │ sample_out, test_cases │
         │            └───────────┬──────────┘       └────────────────────────┘
         ▼                        │
┌──────────────────┐              │
│  exam_attempts   │              │
├──────────────────┤              │
│ id (PK)          │              │
│ user_id (FK)     │              │
│ exam_id (FK)     │              │
│ start_time       │              │
│ end_time         │              │
│ submitted_time   │              │
│ status           │              │
│ score            │              │
│ percentage       │              │
│ is_passed        │              │
│ total_correct    │              │
│ total_wrong      │              │
│ total_unattempted│              │
└────────┬─────────┘              │
         │ 1:N                    │
         ▼                        │
┌─────────────────────────────────┴┐
│        attempt_responses         │
├──────────────────────────────────┤
│ id (PK)                          │
│ attempt_id (FK)                  │
│ question_id (FK)                 │
│ selected_option_id (FK, Nullable)│
│ submitted_code (Text)            │
│ is_correct (Boolean)             │
│ marks_awarded (Double)           │
└──────────────────────────────────┘
```

---

## ⚡ Key Features

| Requirement | Implementation Detail |
|---|---|
| **Role-Based Portals** | Distinct Admin & Student dashboards with route-level and endpoint-level authorization. |
| **JWT Authentication** | BCrypt password hashing, OncePerRequestFilter, claims token generation, stateless sessions. |
| **Full Exam Management** | Admin CRUD for exams: title, duration, total marks, passing cutoff, max attempts, publish toggle. |
| **MCQ & Coding Support** | Multiple choice questions with single/multiple options, plus hands-on code editors with starter code and test case specifications. |
| **Countdown Timer** | High-precision countdown synchronized against server deadline timestamp; visual warnings under 5m/1m. |
| **Auto-Submission** | Timer hook triggers automatic finalization when time hits `00:00`; grace-window handling on the server. |
| **Randomization** | Fisher-Yates shuffling for questions and option choices per student attempt to deter malpractice. |
| **Automated Evaluation** | Immediate MCQ grading with configurable negative marking deductions (e.g. -0.25 / -1.0) and passing validation. |
| **Performance Reports** | Detailed scorecards with accuracy rates, time taken, question breakdown, selected vs correct answers, and architectural explanations. |
| **Exam History** | Chronological record of student attempts with one-click report reviews. |
| **Pagination & Filtering** | Spring Data `Pageable` pagination, multi-column search, category filtering, and sorting. |
| **Unit Test Coverage** | JUnit 5 and Mockito tests covering exam creation, attempt limit validation, scoring logic, and security. |

---

## 🛠️ Technology Stack

### Backend
- **Language**: Java 17+ (fully verified with Java 17, 21, and 25)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6.x + JJWT (Java JWT 0.12.5)
- **Persistence**: Spring Data JPA, Hibernate 6.x
- **Database**: MySQL 8.0+ (Production) / H2 in MySQL-mode (Zero-config Dev)
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`)
- **Testing**: JUnit 5, Mockito, AssertJ, Spring Security Test
- **Build Tool**: Apache Maven

### Frontend
- **Library**: React 18
- **Routing**: React Router DOM v6
- **HTTP Client**: Axios (with Request & Response Interceptors)
- **UI System**: Bootstrap 5.3 + Bootstrap Icons
- **Typography**: Inter & JetBrains Mono Fonts

---

## 📂 Project Directory Structure

```
assesment_web/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/smartassessment/
│       │   │   ├── config/
│       │   │   │   └── DataInitializer.java         # Auto-seeds default admin, students, & exams
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java          # Login, Register, Profile
│       │   │   │   ├── CategoryController.java      # Subject categories
│       │   │   │   ├── ExamController.java          # Public & Admin exam CRUD
│       │   │   │   ├── QuestionController.java      # MCQ & Coding question bank
│       │   │   │   ├── ExamAttemptController.java   # Start, Submit, Result, History
│       │   │   │   └── AdminAnalyticsController.java# Dashboard overview & candidate tracking
│       │   │   ├── dto/
│       │   │   │   ├── auth/                        # LoginRequest, RegisterRequest, JwtAuthResponse
│       │   │   │   ├── exam/                        # ExamRequestDto, ExamResponseDto, CategoryDto
│       │   │   │   ├── question/                    # StudentQuestionDto (sanitized), QuestionRequestDto
│       │   │   │   ├── attempt/                     # SubmitExamRequestDto, ExamResultResponseDto
│       │   │   │   └── common/                      # ApiResponse<T>, PagedResponse<T>
│       │   │   ├── entity/                          # User, Exam, Question, ExamAttempt, etc.
│       │   │   ├── exception/                       # GlobalExceptionHandler, Custom exceptions
│       │   │   ├── repository/                      # Spring Data JPA interfaces
│       │   │   ├── security/                        # JwtTokenProvider, SecurityConfig, UserPrincipal
│       │   │   ├── service/                         # Business interfaces & Implementations
│       │   │   └── SmartAssessmentPlatformApplication.java
│       │   └── resources/
│       │       ├── application.yml                  # Dev profile (H2 MySQL mode)
│       │       ├── application-mysql.yml            # Production profile (MySQL 8)
│       │       └── schema.sql                       # Complete MySQL DDL
│       └── test/java/com/smartassessment/service/
│           ├── AuthServiceTest.java                 # Auth unit tests
│           ├── ExamServiceTest.java                 # Exam CRUD unit tests
│           └── ExamAttemptServiceTest.java          # Auto-grading & penalty unit tests
└── frontend/
    ├── package.json
    ├── public/
    │   └── index.html
    └── src/
        ├── api/                                     # Axios client & modular API methods
        ├── components/                              # Navbar, TimerBanner, QuestionPalette, CodeEditor
        ├── context/                                 # AuthContext (JWT, Role, Login, Logout)
        ├── hooks/                                   # useCountdownTimer (Anti-tamper countdown)
        ├── pages/
        │   ├── auth/                                # LoginPage, RegisterPage
        │   ├── admin/                               # AdminDashboard, ExamListPage, QuestionManagePage
        │   └── student/                             # StudentDashboard, ExamTakingPage, ExamResultPage
        ├── App.js                                   # Routing & role guards
        ├── index.css                                # Design system & custom UI variables
        └── index.js
```

---

## 📡 REST API Specifications

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new student candidate |
| `POST` | `/api/auth/login` | Public | Authenticate and obtain JWT token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve current authenticated user profile |

### Examinations (`/api/exams` & `/api/admin/exams`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/exams/published` | Public / Student | Paginated search of live published exams |
| `GET` | `/api/exams/{id}` | Authenticated | Get full examination details |
| `GET` | `/api/admin/exams` | `ROLE_ADMIN` | Paginated admin exam management list |
| `POST` | `/api/admin/exams` | `ROLE_ADMIN` | Create new examination |
| `PUT` | `/api/admin/exams/{id}` | `ROLE_ADMIN` | Update examination parameters |
| `DELETE` | `/api/admin/exams/{id}` | `ROLE_ADMIN` | Delete examination and related data |
| `PATCH` | `/api/admin/exams/{id}/publish?published={bool}` | `ROLE_ADMIN` | Toggle publish status |

### Question Management (`/api/admin`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/exams/{examId}/questions` | `ROLE_ADMIN` | List questions for an exam |
| `POST` | `/api/admin/exams/{examId}/questions` | `ROLE_ADMIN` | Add MCQ or Coding question |
| `PUT` | `/api/admin/questions/{id}` | `ROLE_ADMIN` | Update question parameters |
| `DELETE` | `/api/admin/questions/{id}` | `ROLE_ADMIN` | Delete question |

### Student Test Flow (`/api/student`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/student/exams/{examId}/start` | `ROLE_STUDENT` | Launch or resume an active exam attempt |
| `POST` | `/api/student/attempts/{attemptId}/submit?autoSubmit={bool}` | `ROLE_STUDENT` | Submit answers for evaluation |
| `GET` | `/api/student/attempts/{attemptId}/result` | `ROLE_STUDENT` | Retrieve score, breakdown, & explanations |
| `GET` | `/api/student/attempts/my-history` | `ROLE_STUDENT` | Paginated past assessment history |

### Analytics (`/api/admin/analytics`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/analytics/overview` | `ROLE_ADMIN` | Platform metrics, pass rates, student count |
| `GET` | `/api/admin/analytics/exams/{examId}/attempts` | `ROLE_ADMIN` | Student submissions for a specific exam |

---

## 🧠 Core Business Logic & Algorithms

### 1. Anti-Cheat Question & Choice Randomization (Fisher-Yates)
When a student launches an assessment, the backend executes an in-place shuffle algorithm:
```java
// Questions are shuffled per attempt
if (exam.isRandomizeQuestions()) {
    Collections.shuffle(questions, new Random());
}

// Answer options within each MCQ are independently shuffled
if (exam.isRandomizeOptions()) {
    Collections.shuffle(options, new Random());
}
```
Crucially, when mapping to `StudentQuestionDto`, `isCorrect` and `explanation` are completely stripped from the response payload. Even if a student inspects DevTools Network tabs, the answers cannot be obtained.

### 2. Automated Scoring & Negative Marking Formula
For every question $i$ in exam:
$$
\text{Marks}_i = \begin{cases}
+\text{marks}_i, & \text{if candidate answer is correct} \\
-\text{penalty}_i, & \text{if candidate answer is incorrect} \\
0, & \text{if question is unattempted}
\end{cases}
$$
Where $\text{penalty}_i = \text{negativeMarks}_i \text{ (or } \text{rate} \times \text{marks}_i)$.

The aggregate score is computed in a single transactional pass:
$$\text{Final Score} = \max\left(0.0, \sum \text{Marks}_i\right)$$
$$\text{Percentage} = \frac{\text{Final Score}}{\text{Total Marks}} \times 100$$
$$\text{Status} = \begin{cases} \text{PASSED}, & \text{if Final Score} \ge \text{Passing Marks} \\ \text{FAILED}, & \text{otherwise} \end{cases}$$

### 3. Server-Synchronized Timer & Auto-Submit
- Deadline calculation: `endTime = startTime + durationMinutes`
- The client receives `endTime` as an ISO timestamp. The custom hook `useCountdownTimer` calculates difference against `Date.now()`. If the user refreshes or re-enters, the countdown remains accurate.
- When `remainingSeconds <= 0`, the client immediately dispatches `submitExam(..., { autoSubmit: true })`.
- On the server, if `LocalDateTime.now().isAfter(attempt.getEndTime().plusSeconds(60))`, the server forcibly sets `status = AUTO_SUBMITTED`, preventing students from delaying submission via network throttling.

---

## 🧪 Unit Testing with JUnit 5 & Mockito

The project contains unit tests verifying core logic without requiring an external database:

1. **`ExamServiceTest.java`**:
   - `createExam_Success()`: Validates JPA entity mapping and repository persistence.
   - `createExam_InvalidMarks_ThrowsBadRequest()`: Validates that passing marks cannot exceed total marks.
   - `createExam_CategoryNotFound_ThrowsException()`: Asserts 404 response on missing foreign key.
2. **`ExamAttemptServiceTest.java`**:
   - `submitExam_CorrectAnswer_AwardsFullMarks()`: Tests positive score accumulation.
   - `submitExam_WrongAnswer_AppliesNegativeMarking()`: Verifies negative marking penalty deduction.
   - `startExam_MaxAttemptsExceeded_ThrowsBadRequest()`: Enforces the exam attempt limit per candidate.
3. **`AuthServiceTest.java`**:
   - `register_Success()`: Verifies password hashing and student role assignment.
   - `register_DuplicateUsername_ThrowsBadRequest()`: Enforces uniqueness constraint.
   - `login_Success()`: Mocks Spring Security `AuthenticationManager` and verifies JWT creation.

Run tests via Maven:
```bash
mvn test
```

---

## 🚀 Local Setup & Execution Guide

### Prerequisites
- **Java**: JDK 17, 21, or 25 installed (`java -version`)
- **Maven**: 3.8+ installed (`mvn -v`)
- **Node.js**: Node 18+ and npm installed (`node -v`)
- **MySQL**: (Optional) MySQL 8.0+ running on port 3306

---

### Step 1: Clone and Navigate to Project
```bash
cd assesment_web
```

---

### Step 2: Run the Backend

#### Option A: Zero-Config In-Memory Mode (Recommended for instant testing)
The default `application.yml` runs with H2 in MySQL-compatibility mode. The database, tables, and demo seed data are initialized automatically upon launch:
```bash
cd backend
mvn spring-boot:run
```
The backend starts on `http://localhost:8080`.
H2 Web Console is accessible at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:smart_assessment_db`, User: `sa`, Password: *blank*).

#### Option B: Standalone MySQL Database
1. Create the MySQL database:
   ```sql
   CREATE DATABASE smart_assessment_db;
   ```
2. Run backend with MySQL profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

### Step 3: Run the React Frontend
Open a second terminal window:
```bash
cd assesment_web/frontend
npm install
npm start
```
The application will launch automatically in your browser at `http://localhost:3000`.

---

### 🔑 Pre-Configured Demo Credentials

Upon startup, `DataInitializer.java` automatically seeds the database with the following demo accounts and assessments:

| Role | Username | Password | Purpose |
|---|---|---|---|
| **Administrator** | `admin` | `Admin@123` | Full access to Exam Management, Question Banks & Analytics |
| **Student** | `student1` | `Student@123` | Candidate access to Take Tests, View History & Scorecards |
| **Student** | `student2` | `Student@123` | Second candidate account for multi-user test simulation |

---

## 📬 Postman Testing Guide

### 1. Register Student
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Amit Kumar",
    "username": "amitk",
    "email": "amit@example.com",
    "password": "Password123",
    "role": "ROLE_STUDENT"
  }'
```

### 2. Login & Acquire JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin@123"
  }'
```
*Copy the `accessToken` from the JSON response and pass it as `Authorization: Bearer <TOKEN>` in subsequent requests.*

### 3. Fetch Published Exams
```bash
curl -X GET "http://localhost:8080/api/exams/published?page=0&size=5"
```

### 4. Start Exam Attempt
```bash
curl -X POST http://localhost:8080/api/student/exams/1/start \
  -H "Authorization: Bearer <TOKEN>"
```

### 5. Submit Answers
```bash
curl -X POST http://localhost:8080/api/student/attempts/1/submit \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      { "questionId": 1, "selectedOptionId": 2 },
      { "questionId": 2, "selectedOptionId": 7 }
    ]
  }'
```

---

## 🎯 Java Full Stack Interview Defense Guide

When discussing this project in technical interviews, use the following structured talking points:

### Q1: How did you implement security and role-based access control?
> **Answer**:
> "I implemented a stateless authentication architecture using Spring Security 6 and JSON Web Tokens (JJWT). Passwords are encrypted using `BCryptPasswordEncoder` with salted hashes. Incoming requests pass through a custom `JwtAuthenticationFilter` extending `OncePerRequestFilter`. The filter parses the `Authorization: Bearer <token>` header, verifies the cryptographic signature with a 256-bit secret key, extracts the subject and role authorities, and populates the `SecurityContextHolder`. Endpoint authorization is enforced both at the HTTP filter level using `requestMatchers('/api/admin/**').hasAuthority('ROLE_ADMIN')` and at the method level using `@PreAuthorize`."

### Q2: How did you handle negative marking and prevent cheating during exam sessions?
> **Answer**:
> "I designed the scoring engine to operate in an atomic database transaction. For each question, if the candidate's selected choice matches `isCorrect == true`, full marks are awarded. If incorrect, a negative marking deduction is applied according to the question or exam's `negativeMarkingRate`. To prevent client-side inspection, the `StudentQuestionDto` intentionally omits `isCorrect` and `explanation` flags from the payload. Additionally, questions and choices are shuffled using Fisher-Yates randomization per attempt, and countdown timers are validated against an immutable server-side deadline (`endTime`) so client clock modifications have no effect."

### Q3: How do you solve the Hibernate N+1 Query Problem in this project?
> **Answer**:
> "In default JPA relationships, accessing lazy collections like `@OneToMany List<Question>` across an exam list triggers $1$ query for the exams plus $N$ additional queries for each exam's questions. I resolved this by using custom JPQL queries with `JOIN FETCH` and Spring Data `@EntityGraph` when loading exams with their child associations, reducing $N+1$ database roundtrips down to a single optimized SQL join."

### Q4: Why did you separate Entities and DTOs?
> **Answer**:
> "Exposing JPA entities directly to the presentation layer creates three major risks:
> 1. **Circular Serialization**: Bidirectional relationships (`@ManyToOne` and `@OneToMany`) trigger infinite recursion during Jackson JSON serialization.
> 2. **Over-fetching & Security Leaks**: Sensitive fields like password hashes or correct question answers would be leaked to the client.
> 3. **Coupled Schema Evolution**: Any database schema change breaks existing API clients.
> DTOs create an explicit boundary, allowing validation annotations (`@NotBlank`, `@DecimalMin`) to be verified before business logic executes."

### Q5: How would you scale this platform to support 100,000 concurrent students?
> **Answer**:
> "To scale this to an enterprise level:
> 1. **Read-Heavy Caching**: Cache exam metadata, categories, and question banks in **Redis** with TTL.
> 2. **Database Connection Pooling**: Tune HikariCP pool sizes and implement read-replicas for catalog queries.
> 3. **Asynchronous Evaluation**: Offload exam submission processing to a message queue like **Apache Kafka** or **RabbitMQ**, enabling worker nodes to score attempts asynchronously without blocking the user thread.
> 4. **Stateless Web Tier**: Since JWT authentication is stateless, backend Spring Boot instances can be containerized with Docker and horizontally scaled across Kubernetes behind an NGINX load balancer."

---

## 📄 License
This project is licensed under the MIT License - open for educational and professional demonstration purposes.
