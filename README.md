# SplitSmart – Expense Splitting Application

SplitSmart is a RESTful backend application built with Java and Spring Boot that helps users manage shared expenses within groups. Users can create groups, add members, create expenses, and divide expenses using different splitting methods.

## Features

- **User Authentication:** Secure authentication using Spring Security and JWT.
- **Group Management:** Create groups and retrieve groups belonging to the logged-in user.
- **Member Management:** Add members to groups and retrieve group members.
- **Expense Management:** Create and retrieve group expenses.
- **Multiple Splitting Methods:**
    - Equal split – divides an expense equally among selected participants.
    - Exact split – allows specific amounts for each participant.
    - Percentage split – divides an expense according to participant percentages.
- **Participant Tracking:** Stores and retrieves individual participant shares for each expense.
- **Validation and Exception Handling:** Centralized API exception handling and request validation.

## Technology Stack

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Spring Security
- JWT Authentication
- Maven

## Project Architecture

The application follows a layered architecture:

- **Controller:** Handles incoming REST API requests.
- **Service:** Contains application business logic.
- **Repository:** Provides database access using Spring Data JPA.
- **Entity:** Represents database tables.
- **DTO:** Defines request and response objects.
- **Config:** Contains application and security configuration.
- **Exception:** Handles API errors centrally.

## Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE splitsmart;
```

Configure the following environment variables before running the application:

| Variable | Description |
|---|---|
| DB_USERNAME | MySQL username |
| DB_PASSWORD | MySQL password |
| JWT_SECRET | Secret key used for JWT signing |

The application reads these values from environment variables through `application.properties`.

**Do not commit actual database passwords or JWT secrets to GitHub.**

## Running the Application

### Prerequisites

- JDK compatible with the Java version specified in `pom.xml`
- MySQL
- Git

### Build the project

On Windows:

```powershell
.\mvnw.cmd clean package
```

### Run the application

Configure the required environment variables in your IDE's Run Configuration, then run `SplitsmartApplication`.

The application uses:

```text
http://localhost:8080
```

## Main API Endpoints

All group and expense endpoints require authentication.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Authenticate a user |
| POST | `/api/groups` | Create a group |
| GET | `/api/groups` | Retrieve the logged-in user's groups |
| POST | `/api/groups/{groupId}/members` | Add a group member |
| GET | `/api/groups/{groupId}/members` | Retrieve group members |
| POST | `/api/groups/{groupId}/expenses` | Create an expense |
| GET | `/api/groups/{groupId}/expenses` | Retrieve group expenses with participant shares |

### Example: Create an Equal Split Expense

**POST** `/api/groups/1/expenses`

```json
{
    "description": "Lunch",
    "amountPaise": 120000,
    "paidByUserId": 1,
    "splitType": "EQUAL",
    "expenseDate": "2026-11-12",
    "participants": [
        {
            "userId": 1
        },
        {
            "userId": 2
        }
    ]
}
```

Amounts are stored in paise. For example, `120000` represents ₹1,200.

## Expense Splitting Example

A ₹1,200 expense shared equally between two participants results in:

| Participant | Share |
|---|---:|
| Participant 1 | ₹600 |
| Participant 2 | ₹600 |

Only the selected participants are included in that expense's split records.

## Future Improvements

- Settlement recording and balance calculation.
- Expense editing and deletion APIs.
- Frontend application.

## Author

Developed as a backend project using Java and Spring Boot.