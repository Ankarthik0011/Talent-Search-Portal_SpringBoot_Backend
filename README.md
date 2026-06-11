# Talent Search Portal - Backend

## Overview

Talent Search Portal Backend is a RESTful Spring Boot application designed to manage candidate profiles, resume uploads, and candidate search operations for recruitment teams.

The application provides APIs for creating, updating, deleting, searching, and managing candidate information.

---

## Features

- Candidate Management (CRUD Operations)
- Candidate Search by Skills
- Candidate Search by Salary Range
- Resume Upload Support
- PostgreSQL Database Integration
- Swagger/OpenAPI Documentation
- Docker Containerization
- RESTful API Architecture
- Input Validation
- Spring Data JPA

---

## Tech Stack

| Technology | Version |
|------------|----------|
| Java | 17 |
| Spring Boot | 3.x |
| PostgreSQL | 17 |
| Maven | 3.x |
| Docker | Latest |
| Swagger/OpenAPI | 3 |
| Git/GitHub | Latest |

---

## Project Structure

src/main/java

├── controller

├── service

├── repository

├── entity

├── dto

└── TalentSearchBackendApplication

---

## API Endpoints

### Candidate APIs

| Method | Endpoint |
|----------|------------|
| GET | /api/candidates |
| GET | /api/candidates/{id} |
| POST | /api/candidates |
| PUT | /api/candidates/{id} |
| DELETE | /api/candidates/{id} |
| GET | /api/candidates/search |
| GET | /api/candidates/salary |
| POST | /api/candidates/upload/{id} |

---

## Swagger Documentation

Access Swagger UI:

http://localhost:9090/swagger-ui/index.html

---

## Database Configuration

application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/talentsearchdb
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

## Run Locally

```bash
git clone <repo-url>

cd talent-search-backend

mvn clean install

mvn spring-boot:run
```

Application runs at:

```text
http://localhost:9090
```

---

## Docker Setup

Build Image

```bash
docker build -t talent-search-backend .
```

Run Container

```bash
docker run -d -p 9090:9090 --name backend talent-search-backend
```

Check Container

```bash
docker ps
```

---

## Sample Response

```json
{
  "id": 1,
  "fullName": "Vikram Singh",
  "email": "vikram@gmail.com",
  "skills": "AWS,Docker,Kubernetes"
}
```

---

## Future Enhancements

- JWT Authentication
- Role Based Access Control
- Resume Parsing
- Jenkins CI/CD Pipeline
- Kubernetes Deployment
- AWS Deployment
- Email Notifications

---

## Author

Karthik A N

GitHub:
https://github.com/Ankarthik0011
