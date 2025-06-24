# Time Sheet Management System

A full-stack web application for managing employee timesheets, featuring a Spring Boot REST API backend and a React frontend.

---

## Features
- User registration and login (JWT authentication)
- Role-based access: User (Employee) and Manager
- Weekly timesheet entry, submission, and review
- Manager dashboard for reviewing and filtering timesheets
- Automated monthly report generation and email notifications
- Secure RESTful API

---

## Technology Stack
- **Backend:** Spring Boot (REST API, JWT, Spring Security, MySQL, Email)
- **Frontend:** React (in `client/` directory)
- **Database:** MySQL 8.0+
- **Build Tools:** Maven (backend), npm (frontend)

---

## Setup Instructions

### 1. Backend (Spring Boot)
1. **Configure MySQL:**
   - Create a database (e.g., `timesheet_db`).
2. **Configure application properties:** Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/timesheet_db
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   jwt.secret=your-very-long-secret-key
   jwt.expiration=86400000
   ```
3. **Build & run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The backend API will be available at `http://localhost:8080`.

### 2. Frontend (React)
1. **Navigate to the frontend directory:**
   ```bash
   cd client
   ```
2. **Install dependencies & run:**
   ```bash
   npm install
   npm start
   ```
   The frontend will be available at `http://localhost:3000`.

---

## API Authentication
- After login, you will receive a JWT token.
- For protected endpoints, include:
  ```
  Authorization: Bearer <your-jwt-token>
  ```
- Public endpoints: `/api/auth/register`, `/api/auth/login`, `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`
- Manager-only endpoint: `/api/timesheets/submitted`

---

## API Testing
- **Swagger UI:**
  - Visit `http://localhost:8080/swagger-ui.html` for interactive API docs and testing.
- **Postman:**
  - Register/login to get a JWT token.
  - Add `Authorization: Bearer <token>` header for protected requests.

---

## Troubleshooting
- **Database errors:** Ensure MySQL is running and credentials are correct.
- **Email issues:** Use an app password for Gmail. Check SMTP settings.
- **JWT errors:** Ensure your token is valid and not expired.
- **CORS issues:** Configure CORS in the backend if needed for frontend communication.

---

## License
MIT 