# Time Sheet Management System Presentation

---

## Slide 1: Title

**Time Sheet Management System**  
_A Modern Solution for Employee Time Tracking_

- Full-stack web application
- Backend: Spring Boot REST API
- Frontend: React

---

## Slide 2: Key Features

**Key Features**
- User registration & login (JWT authentication)
- Role-based access: User (Employee) & Manager
- Weekly timesheet entry, submission, and review
- Automated monthly report generation (CSV) & email notifications
- Manager dashboard for reviewing and filtering timesheets

---

## Slide 3: Technology Stack

**Technology Stack**
- **Backend:** Spring Boot, Spring Security, JWT, MySQL, Email (SMTP)
- **Frontend:** React (client directory)
- **API Testing:** Swagger UI, Postman
- **Build Tools:** Maven (backend), npm (frontend)

---

## Slide 4: Workflow & API

**Workflow & API**
- Users log daily work hours, work type, and remarks
- Submit timesheet for manager review (cannot edit after submission)
- On 5th of each month: system generates CSV report and emails to managers
- **API Endpoints:**  
  - `/api/auth/register`, `/api/auth/login`  
  - `/api/timesheets` (CRUD)  
  - `/api/timesheets/submitted` (Manager)  
  - `/swagger-ui.html` for API docs 