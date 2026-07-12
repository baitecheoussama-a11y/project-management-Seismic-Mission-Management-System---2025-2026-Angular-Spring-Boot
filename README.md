# 🚀 Seismic Mission Management System

[![Angular](https://img.shields.io/badge/Angular-17+-red.svg)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A comprehensive web application for managing and monitoring seismic missions, developed as part of the Final Year Project (PFE) 2025/2026.

## 📋 Table of Contents

- [Overview](#-overview)
- [Screenshots](#-screenshots)
- [Context](#-context)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Project Structure](#-project-structure)
- [Installation](#-installation)
- [User Guide](#-user-guide)
- [Roles & Permissions](#-roles--permissions)
- [Dynamic Resource Management](#-dynamic-resource-management)
- [Decision Analysis](#-decision-analysis)
- [API Documentation](#-api-documentation)
- [License](#-license)

## 🎯 Overview

This project involves the design and development of an integrated seismic mission management system for **ENAGEO (National Geophysical Enterprise)**.

The application enables:
- Complete management of seismic missions, projects, and activities
- Tracking of teams, employees, and equipment
- Dynamic management of consumable resources (water, fuel, food, etc.)
- Decision analysis via Data Warehouse and OLAP tools
- Geographic visualization of missions on interactive maps
- Automatic cost and charge calculation

## 📸 Screenshots

### Mission Dashboard

![Mission Dashboard](uploads/localhost_4200_pages_mission-dashboard(abc).png)

### Analytics Dashboard

![Analytics Dashboard](uploads/localhost_4200_pages_analytics_dashboard(abc).png)

### Pivot Table Analysis

![Pivot Table](uploads/localhost_4200_pages_analytics_PivotTable(abc)%20(4).png)

### Geographic Map Visualization

![Map Visualization](uploads/localhost_4200_pages_maps_leaflet(abc).png)

### Ressources management

![Mission Details](uploads/localhost_4200_pages_mission-dashboard(abc)%20(1).png)

## 🏢 Context

ENAGEO is a subsidiary of **SONATRACH** specializing in geophysical prospecting and seismic data acquisition. Currently, seismic mission management relies on manual paper-based and Excel processes, which limits:

- Real-time visibility on project progress
- Accurate cost and consumption analysis
- Fast and informed decision-making

Our solution aims to **digitalize and optimize** this process by providing a centralized web platform with advanced analytical capabilities.

## ⚡ Features

### 👥 User Management
- Secure authentication (JWT)
- Role and permission management
- User account management

### 🗺️ Mission Management
- Seismic project planning
- Production progress tracking (vibration points)
- Geographic mission visualization on maps
- Daily production report generation

### 👨‍💼 Human Resources Management
- Employee management (personal info, medical status)
- Team and activity management
- Attendance tracking
- Incident and event management

### 🚗 Equipment Management
- Equipment fleet tracking (vehicles, gear)
- Breakdown and repair management (internal/external)
- Usage history

### ⛽ Dynamic Consumable Resource Management
- Semi-structured resource management
- Dynamic cost calculation rule configuration
- Consumption tracking (water, fuel, food, etc.)
- Automatic expense calculation

### 📊 Decision Analysis (Business Intelligence)
- Production analysis by project, mission, wilaya
- Consumption and cost analysis
- Dashboards and performance indicators
- Decision support reports

## 🏗️ Architecture

The application follows a **three-tier client/server architecture**:
```text
┌─────────────────────────────────────────────────────────────┐
│ WEB CLIENT (Angular) │
│ - Responsive user interface │
│ - Nebular UI components │
│ - REST API communication │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ APPLICATION SERVER (Spring Boot) │
│ - REST API │
│ - Business logic │
│ - Security (Spring Security + JWT) │
│ - Dynamic resource management │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ DATABASE SERVER (PostgreSQL) │
│ - Relational storage │
│ - ROLAP model for analysis │
│ - Snowflake schema │
└─────────────────────────────────────────────────────────────┘

```

### Database
- **OLTP Model**: Operational mission management
- **ROLAP Model**: Decision analysis (snowflake schema)
- **Analysis Tables**: Production, Resource Consumption

## 🛠️ Technologies

### Backend
| Technology | Description |
|------------|-------------|
| **Java 17** | Programming language |
| **Spring Boot 3.x** | Main framework |
| **Spring Security** | Authentication & authorization |
| **Spring Data JPA** | ORM and data access |
| **JWT** | Token management |
| **Maven** | Dependency management |

### Frontend
| Technology | Description |
|------------|-------------|
| **Angular 17+** | SPA framework |
| **TypeScript** | Main language |
| **Nebular** | UI components |
| **Leaflet** | Interactive mapping |
| **RxJS** | Reactive programming |

### Database
| Technology | Description |
|------------|-------------|
| **PostgreSQL 16** | Relational database |
| **DBeaver** | Administration interface |

### Development Tools
| Technology | Description |
|------------|-------------|
| **IntelliJ IDEA** | IDE for backend |
| **VSCodium** | IDE for frontend |
| **Postman** | API testing |
| **Git & GitHub** | Version control |

## 📁 Project Structure
```text
Pfe/
│
├── backend/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/enageo/
│ │ │ │ ├── controller/ # REST Controllers
│ │ │ │ ├── service/ # Business logic
│ │ │ │ ├── repository/ # Data access
│ │ │ │ ├── model/ # JPA Entities
│ │ │ │ ├── dto/ # Data Transfer Objects
│ │ │ │ ├── config/ # Configuration
│ │ │ │ ├── security/ # JWT Security
│ │ │ │ └── exception/ # Exception handling
│ │ │ └── resources/
│ │ │ ├── application.properties
│ │ │ └── db/migration/ # Flyway migrations
│ │ └── test/
│ │ └── java/
│ └── pom.xml # Maven dependencies
│
├── frontend/
│ ├── src/
│ │ ├── app/
│ │ │ ├── modules/ # Angular Modules
│ │ │ │ ├── auth/ # Authentication
│ │ │ │ ├── admin/ # Administration
│ │ │ │ ├── mission/ # Mission management
│ │ │ │ ├── resources/ # Resource management
│ │ │ │ └── dashboard/ # Dashboards
│ │ │ ├── shared/ # Shared components
│ │ │ ├── services/ # API services
│ │ │ ├── models/ # TypeScript models
│ │ │ └── guards/ # Authentication guards
│ │ ├── assets/ # Images, icons
│ │ ├── environments/ # Environment config
│ │ └── index.html
│ ├── angular.json
│ ├── package.json
│ └── tsconfig.json
│
├── database/
│ └── schema.sql # Creation scripts
│
├── docs/
│ ├── user-guide.pdf
│ └── technical-documentation.pdf
│
├── uploads/ # Screenshots and assets
│ ├── localhost_4200_pages_mission-dashboard(abc).png
│ ├── localhost_4200_pages_analytics_dashboard(abc).png
│ ├── localhost_4200_pages_analytics_PivotTable(abc) (4).png
│ ├── localhost_4200_pages_maps_leaflet(abc).png
│ └── localhost_4200_pages_mission-dashboard(abc) (1).png
│
├── README.md
├── LICENSE
└── .gitignore
```


## 🚀 Installation

### Prerequisites

- **Java 17** or higher
- **Node.js 14 or 16** and npm
- **PostgreSQL 16**
- **Maven 3.8+**
- **Angular CLI** (optional)

### 1. Clone the Repository

```bash
git clone https://github.com/baitecheoussama-a11y/pfe-seismic-mission-management.git
cd pfe-seismic-mission-management
```
### 2. Database Configuration

```bash
# Create PostgreSQL database
sudo -u postgres psql
CREATE DATABASE postgres;
```

### 3. Backend (Spring Boot)

> **Recommended**: Use **IntelliJ IDEA** for backend development - it provides excellent Spring Boot support, auto-completion, and debugging features.

```bash
# Navigate to backend
cd backend

# Configure application.properties
# Edit src/main/resources/application.properties
# Update spring.datasource.username and spring.datasource.password

# Build the project
mvn clean install

# Run the backend
mvn spring-boot:run
```

### 4. Frontend (Angular)

> **Recommended**: Use **Node.js 16** or higher for Angular development. The project was developed and tested with Node.js 16. Use **VSCodium** or **Visual Studio Code** for frontend development with Angular extensions.

```bash
# Navigate to frontend
cd ../frontend

# Install dependencies
npm install

# Configure environment
# Edit src/environments/environment.ts
# Update apiUrl: 'http://localhost:8080/api'

# Start the development server
ng serve
```
The frontend starts on http://localhost:4200

### 5. Access the Application

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/swagger-ui.html

### Default Credentials

The application comes with pre-configured user accounts for testing. Here are the default credentials:

| Role | Username | Password |
|------|----------|----------|
| **Administrator** | `admin` | `admin123` |
| **Director** | `directeur1` to `directeur3` | `password123` |
| **Mission Chief** | `chefmission1` to `chefmission13` | `password123` |
| **Field Chief** | `chefterrain1` to `chefterrain13` | `password123` |
| **Manager** | `gestionnaire1` to `gestionnaire30` | `password123` |

#### Available Accounts:

- **1 Administrator**: Full system access
- **3 Directors**: Strategic and cross-mission analysis
- **13 Mission Chiefs**: Mission and project management
- **13 Field Chiefs**: Daily field operations and reporting
- **30 Managers**: Resource and equipment management
- **500 Employees**: No login access (database only)

#### Test Credentials

```bash
# Admin Login
Username: admin
Password: admin123

# Director Login (Example)
Username: directeur1
Password: password123

# Mission Chief Login (Example)
Username: chefmission1
Password: password123

# Field Chief Login (Example)
Username: chefterrain1
Password: password123

# Manager Login (Example)
Username: gestionnaire1
Password: password123
```
## 🙏 Acknowledgments

- **University** - For the academic framework and supervision
- **Faculty of Sciences - Department of Computer Science** - For providing the academic environment and support

## 📧 Contact

**Author**: Oussama Baiteche
- **GitHub**: [@baitecheoussama-a11y](https://github.com/baitecheoussama-a11y)
- **Project Link**: [https://github.com/baitecheoussama-a11y/robot-maze-3d](https://github.com/baitecheoussama-a11y/robot-maze-3d)


---

<div align="center">
  <sub>Built with ❤️ as part of the Final Year Project (PFE) 2025/2026</sub>
  <br>
  <sub>Faculty of Sciences - Department of Computer Science</sub>
</div>
