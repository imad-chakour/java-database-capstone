# 🏥 Smart Clinic Management System

A full-stack web application for managing a medical clinic — handling doctors, patients, appointments, and prescriptions. Built with **Java Spring Boot** on the backend and **Vanilla JavaScript / HTML / CSS** on the frontend, with a hybrid **MySQL + MongoDB** persistence layer.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Docker](#docker)
- [Database Configuration](#database-configuration)
- [Authentication](#authentication)

---

## 🔍 Overview

The Smart Clinic Management System provides three distinct portals:

| Role | Access |
|------|--------|
| **Admin** | Manage doctors, view all appointments, generate reports |
| **Doctor** | View scheduled appointments, add prescriptions, manage availability |
| **Patient** | Browse doctors, book appointments, view prescriptions |

---

## ✨ Features

### 👨‍💼 Admin
- Secure login with JWT token
- Add, update, and delete doctor profiles
- View appointment statistics (monthly reports via stored procedures)
- Role management

### 👨‍⚕️ Doctor
- Secure login with JWT token
- View daily/weekly appointments
- Add prescriptions with medication, dosage, and notes
- Check availability by date

### 🧑‍🤝‍🧑 Patient
- Browse available doctors by name, specialty, or time slot
- Book appointments
- View and manage personal appointments
- Access prescription history

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core language |
| Spring Boot | 3.4.4 | Application framework |
| Spring Data JPA | — | ORM for MySQL (relational data) |
| Spring Data MongoDB | — | ODM for MongoDB (prescriptions) |
| Spring MVC | — | REST API + Thymeleaf MVC |
| Spring Validation | — | Bean validation (`@NotNull`, `@Size`, etc.) |
| Thymeleaf | — | Server-side HTML templates (admin/doctor dashboards) |
| JJWT | 0.12.6 | JWT token generation & validation |
| Spring Actuator | — | Health, info, metrics endpoints |
| Maven | 3.9.9 | Build tool & dependency management |

### Frontend
| Technology | Purpose |
|---|---|
| HTML5 | Page structure |
| CSS3 | Custom styling per dashboard |
| Vanilla JavaScript (ES6+) | Dynamic UI, API calls |
| Fetch API | REST communication with backend |

### Databases
| Database | Usage |
|---|---|
| **MySQL 8** | Relational data: `Admin`, `Doctor`, `Patient`, `Appointment` |
| **MongoDB** | Document store: `Prescription` collection |

### DevOps
| Technology | Purpose |
|---|---|
| Docker | Multi-stage containerized build |
| Git / GitHub | Version control |

---

## 🏗️ Architecture

```
┌───────────────────────────────────────────────────┐
│                    Frontend                        │
│  Static HTML/CSS/JS  │  Thymeleaf Templates        │
│  (Patient Portal)    │  (Admin & Doctor Portal)    │
└────────────┬──────────────────────┬────────────────┘
             │   REST API (JSON)    │  Server-side MVC
             ▼                      ▼
┌───────────────────────────────────────────────────┐
│                Spring Boot Backend                 │
│                                                   │
│  Controllers → Services → Repositories            │
│  (REST + MVC)   (Business Logic)  (Data Access)   │
│                                                   │
│  JWT Auth  │  Bean Validation  │  CORS Config     │
└──────┬─────────────────────────────────┬──────────┘
       │                                 │
       ▼                                 ▼
┌─────────────┐                 ┌─────────────────┐
│   MySQL 8   │                 │    MongoDB      │
│             │                 │                 │
│  - admins   │                 │  - prescriptions│
│  - doctors  │                 │                 │
│  - patients │                 │                 │
│  - appointments              │                 │
└─────────────┘                 └─────────────────┘
```

### Design Patterns
- **MVC** — Thymeleaf templates for admin/doctor dashboards
- **REST API** — JSON endpoints for patient portal (Vanilla JS)
- **DTO Pattern** — `AppointmentDTO`, `Login` for data transfer
- **Repository Pattern** — via Spring Data interfaces
- **Service Layer** — Business logic separated from controllers

---

## 📁 Project Structure

```
java-database-capstone/
├── app/
│   ├── src/main/java/com/project/back_end/
│   │   ├── BackEndApplication.java          # App entry point
│   │   ├── config/
│   │   │   └── WebConfig.java               # CORS configuration
│   │   ├── controllers/
│   │   │   ├── AdminController.java         # Admin REST endpoints
│   │   │   ├── DoctorController.java        # Doctor REST endpoints
│   │   │   ├── PatientController.java       # Patient REST endpoints
│   │   │   ├── AppointmentController.java   # Appointment REST endpoints
│   │   │   ├── PrescriptionController.java  # Prescription REST endpoints
│   │   │   └── RoleController.java          # Role management
│   │   ├── DTO/
│   │   │   ├── AppointmentDTO.java
│   │   │   └── Login.java
│   │   ├── models/
│   │   │   ├── Admin.java                   # JPA entity
│   │   │   ├── Doctor.java                  # JPA entity
│   │   │   ├── Patient.java                 # JPA entity
│   │   │   ├── Appointment.java             # JPA entity
│   │   │   └── Prescription.java            # MongoDB document
│   │   ├── mvc/
│   │   │   └── DashboardController.java     # Thymeleaf MVC controller
│   │   ├── repo/
│   │   │   ├── AdminRepository.java
│   │   │   ├── DoctorRepository.java
│   │   │   ├── PatientRepository.java
│   │   │   ├── AppointmentRepository.java
│   │   │   └── PrescriptionRepository.java
│   │   └── services/
│   │       ├── MainService.java             # Shared service (auth, filters)
│   │       ├── DoctorService.java
│   │       ├── PatientService.java
│   │       ├── AppointmentService.java
│   │       ├── PrescriptionService.java
│   │       └── TokenService.java            # JWT management
│   ├── src/main/resources/
│   │   ├── application.properties           # App configuration
│   │   ├── static/
│   │   │   ├── index.html                   # Landing page
│   │   │   ├── pages/                       # Patient portal HTML pages
│   │   │   ├── js/                          # JavaScript modules
│   │   │   │   ├── components/              # Reusable UI components
│   │   │   │   ├── services/                # API service calls
│   │   │   │   └── config/                  # Frontend config
│   │   │   └── assets/                      # CSS & images
│   │   └── templates/
│   │       ├── admin/adminDashboard.html    # Thymeleaf admin view
│   │       └── doctor/doctorDashboard.html  # Thymeleaf doctor view
│   ├── Dockerfile
│   └── pom.xml
├── schema-architecture.md
├── user_stories.md
└── README.md
```

---

## 🔌 API Endpoints

### Admin — `/admin`
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/admin/login` | Admin login | ❌ |
| `GET` | `/admin/validate/{token}` | Validate admin token | ✅ |

### Doctor — `/doctor`
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/doctor` | List all doctors | ❌ |
| `GET` | `/doctor/{id}` | Get doctor by ID | ❌ |
| `POST` | `/doctor/login` | Doctor login | ❌ |
| `POST` | `/doctor/{token}` | Add new doctor | Admin |
| `PUT` | `/doctor/{token}` | Update doctor | Admin |
| `DELETE` | `/doctor/{id}/{token}` | Delete doctor | Admin |
| `GET` | `/doctor/availability/{user}/{doctorId}/{date}/{token}` | Get available slots | ✅ |
| `GET` | `/doctor/filter/{name}/{time}/{speciality}` | Filter doctors | ❌ |

### Patient — `/patient`
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/patient` | Register patient | ❌ |
| `POST` | `/patient/login` | Patient login | ❌ |

### Appointment — `/appointment`
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/appointment/{token}` | Book appointment | Patient |
| `GET` | `/appointment/patient/{token}` | Get patient appointments | Patient |
| `GET` | `/appointment/doctor/{token}` | Get doctor appointments | Doctor |
| `PUT` | `/appointment/{token}` | Update appointment | Patient |
| `DELETE` | `/appointment/{id}/{token}` | Cancel appointment | Patient |

### Prescription — `/prescription`
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/prescription/{token}` | Add prescription | Doctor |
| `GET` | `/prescription/{patientId}/{token}` | Get patient prescriptions | Doctor/Patient |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8+
- MongoDB

### Run Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/imad-chakour/java-database-capstone.git
   cd java-database-capstone/app
   ```

2. **Configure the databases** — edit `src/main/resources/application.properties`:
   ```properties
   # MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/cms
   spring.datasource.username=your_mysql_user
   spring.datasource.password=your_mysql_password

   # MongoDB
   spring.data.mongodb.uri=mongodb://localhost:27017/prescriptions

   # JWT Secret
   jwt.secret=your_secret_key_here
   ```

3. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the app** at [http://localhost:8080](http://localhost:8080)

---

## 🐳 Docker

Build and run with Docker:

```bash
cd app

# Build the image
docker build -t smart-clinic .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/cms \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/prescriptions \
  smart-clinic
```

The Dockerfile uses a **multi-stage build**:
- Stage 1: `maven:3.9.9-eclipse-temurin-17` — compiles and packages the app
- Stage 2: `eclipse-temurin:17-jre` — lightweight runtime image

---

## 🗄️ Database Configuration

### MySQL — Relational Data
The JPA/Hibernate layer auto-creates/updates tables on startup (`ddl-auto=update`).

**Entities:**
- `Admin` — clinic administrators
- `Doctor` — medical staff with specialties and availability
- `Patient` — registered patients
- `Appointment` — links doctor + patient with datetime and status (`0=Scheduled`, `1=Completed`)

### MongoDB — Document Data
The `Prescription` model is stored as a document in the `prescriptions` collection.

**Fields:** `patientId`, `doctorId`, `patientName`, `appointmentId`, `medications`, `dosage`, `doctorNotes`, `status`

---

## 🔐 Authentication

The system uses **stateless JWT authentication** with role-based access:

- Tokens are signed with **HMAC-SHA256**
- Default expiration: **7 days**
- Three roles: `admin`, `doctor`, `patient`
- Tokens are validated on every protected endpoint via the `TokenService`

```
POST /admin/login  → { "username": "...", "password": "..." }
POST /doctor/login → { "identifier": "email@...", "password": "..." }
POST /patient/login → { "identifier": "email@...", "password": "..." }

Response: { "token": "<jwt_token>" }
```

---

## 📊 Monitoring

Spring Boot Actuator is enabled and exposes:

| Endpoint | URL |
|---|---|
| Health | `/actuator/health` |
| Info | `/actuator/info` |
| Metrics | `/actuator/metrics` |

---

## 📄 License

This project is licensed under the terms found in the [LICENSE](LICENSE) file.
