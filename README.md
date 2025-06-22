# Time Sheet Management System

A comprehensive web application for managing employee timesheets with automatic report generation and email notifications.

## Features

### User Management
- User registration and login with role-based access control
- Two roles: **User** (Employee) and **Manager**
- Secure authentication using Spring Security

### Time Sheet Entry
- Weekly timesheet management with daily entries
- Track login/logout times, work type (Office/Home), leave status, and remarks
- Submit timesheets to admin (cannot be edited after submission)
- Visual indicators for submitted vs draft entries

### Admin/Manager Features
- View all users and their timesheets
- Filter timesheets by employee and date range
- Generate monthly reports
- View statistics and analytics

### Automated Features
- **Monthly Report Generation**: Automatically generates CSV reports on the 5th of every month
- **Email Notifications**: Sends reports to all managers via email
- **Database Management**: Automatic table creation and updates

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Frontend**: Thymeleaf templates with Bootstrap 5
- **Database**: MySQL 8.0
- **Security**: Spring Security with BCrypt password encoding
- **Email**: Spring Mail with SMTP
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

### 1. Database Setup

1. Install MySQL if not already installed
2. Create a database (optional - the application will create it automatically):
   ```sql
   CREATE DATABASE timesheet_db;
   ```

### 2. Configuration

1. Update the database configuration in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

2. Update the email configuration for report sending:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```
   
   **Note**: For Gmail, you'll need to use an App Password instead of your regular password.

3. Update the JWT secret key:
   ```properties
   jwt.secret=your-secret-key-here-make-it-long-and-secure
   ```

### 3. Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd time-sheet-management
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application at: `http://localhost:8080`

### 4. Initial Setup

1. Register a new user account
2. To create a manager account, you can either:
   - Modify the database directly to change a user's role to 'MANAGER'
   - Use the application's admin features (if you have manager access)

## API Endpoints

### Authentication
- `GET /` - Home page
- `GET /login` - Login page
- `POST /login` - Authenticate user
- `GET /register` - Registration page
- `POST /register` - Register new user
- `POST /logout` - Logout user

### User Dashboard
- `GET /timesheet/dashboard` - User's timesheet dashboard
- `POST /timesheet/update/{id}` - Update timesheet entry
- `POST /timesheet/submit/{id}` - Submit timesheet to admin
- `POST /timesheet/delete/{id}` - Delete timesheet entry

### Admin/Manager
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/timesheets` - View all timesheets with filtering
- `GET /admin/reports` - Generate and view reports

## Database Schema

### Users Table
- `id` - Primary key
- `username` - Unique username
- `password` - Encrypted password
- `full_name` - User's full name
- `email` - Unique email address
- `role` - USER or MANAGER

### Timesheets Table
- `id` - Primary key
- `user_id` - Foreign key to users table
- `date` - Date of entry
- `day` - Day of week
- `login_time` - Login time
- `logout_time` - Logout time
- `work_type` - OFFICE or HOME
- `is_leave` - Boolean for leave status
- `remark` - Additional remarks
- `submitted_to_admin` - Boolean for submission status

## Features in Detail

### Time Sheet Entry
Users can:
- View their current week's timesheet
- Enter login and logout times
- Select work type (Office or Home)
- Mark leave days
- Add remarks
- Save entries as drafts
- Submit entries to admin (final submission)

### Admin Features
Managers can:
- View all users and their information
- Filter timesheets by employee and date range
- View submitted timesheets
- Generate monthly reports
- Access analytics and statistics

### Automated Report Generation
- Runs on the 5th of every month at 9:00 AM
- Generates CSV reports for the previous month
- Sends reports to all managers via email
- Includes all timesheet data with user information

## Security Features

- Password encryption using BCrypt
- Role-based access control
- Session management
- CSRF protection (disabled for simplicity in this version)
- Input validation and sanitization

## Customization

### Adding New Features
1. Create new entity classes in `src/main/java/com/timesheet/entity/`
2. Add repository interfaces in `src/main/java/com/timesheet/repository/`
3. Implement service logic in `src/main/java/com/timesheet/service/`
4. Create controllers in `src/main/java/com/timesheet/controller/`
5. Add Thymeleaf templates in `src/main/resources/templates/`

### Styling
The application uses Bootstrap 5 with custom CSS. You can modify:
- `src/main/resources/templates/layout.html` for global styles
- Individual template files for page-specific styling

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in `application.properties`
   - Ensure database exists or auto-creation is enabled

2. **Email Not Working**
   - Verify email credentials in `application.properties`
   - For Gmail, use App Password instead of regular password
   - Check SMTP settings

3. **Application Won't Start**
   - Ensure Java 17+ is installed
   - Check Maven dependencies are resolved
   - Verify port 8080 is available

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please create an issue in the repository or contact the development team. 