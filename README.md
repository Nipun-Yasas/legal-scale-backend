# Legal Scale Backend

Legal Scale Backend is a Spring Boot application designed to serve as an integrated Legal Management System. It coordinates various legal workflows including authentication, role-based access control, legal case handling, and multi-stage agreement approvals, along with secure document storage on AWS S3.

## Features

- **Robust Authentication & Authorization**: Utilizes JSON Web Tokens (JWT) for stateless session management with comprehensive role-based access control (RBAC). Roles include `SYSTEM_ADMIN`, `MANAGEMENT`, `LEGAL_OFFICER`, `LEGAL_SUPERVISOR`, and base `USER`.
- **System Administration**: Provides functionalities for `SYSTEM_ADMIN` to manage users, assign roles, enforce account bans, and retrieve system analytics (role counts).
- **Legal Case Handling**: Enables users to create new legal cases with attachments, and allows supervisors to assign cases to specific legal officers. Track statuses, share comments, and upload supporting case documents.
- **Agreement Approval Workflow**: A comprehensive system for managing agreements and contracts through a multi-stage process (`DRAFT` → `REVIEW_REQUESTED` → `PENDING_APPROVAL` → `APPROVED` → `EXECUTED`). Includes support for uploading agreement revisions, commenting, and cryptographic digital signatures.
- **Secure Document Storage**: Integrates natively with **AWS S3** to provide robust and scalable document storage for case attachments and agreement versions.

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.11
- **Database**: PostgreSQL (via Spring Data JPA)
- **Security**: Spring Security & JJWT (JSON Web Token)
- **Storage**: AWS SDK (S3)
- **Utilities**: Lombok, Spring Dotenv
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 21 or higher installed.
- PostgreSQL running locally or remotely.
- AWS Account with an S3 Bucket and appropriate programmatic access (Access Key and Secret).

### Environment Variables

The project uses `spring-dotenv` to load environment variables. Create a `.env` file in the root of the project with the following configuration:

```env
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/legal_scale_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT Security
JWT_SECRET=your_base64_encoded_jwt_secret_key_here
JWT_EXPIRATION=86400000

# AWS S3 Configuration
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
AWS_REGION=your_aws_region
AWS_S3_BUCKET_NAME=your_s3_bucket_name
```

### Running the Application

1. Clone the repository and navigate into the project directory.
2. Ensure your `.env` file is properly configured.
3. Use the Maven wrapper to build the project and run it:
   
   **Windows:**
   ```bash
   .\mvnw.cmd clean spring-boot:run
   ```
   **macOS / Linux:**
   ```bash
   ./mvnw clean spring-boot:run
   ```

## Further Documentation

For an in-depth dive into the API structure, domain logic, and specific role capabilities, please see the [DOCUMENTATION.md](./DOCUMENTATION.md) file included in this repository.
