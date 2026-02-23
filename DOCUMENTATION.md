# Legal Scale Documentation

This document gives an overview of the domain models, workflows, and endpoints for the Legal Scale Backend system.

---

## 1. Authentication & Security

The system uses stateless JWT validation. On login, an Access Token is provided. Clients must pass this as a Bearer token in the `Authorization` header (`Authorization: Bearer <token>`).

**Key Endpoints:**
- `POST /api/auth/register` - Registers a new user.
- `POST /api/auth/login` - Authenticates and returns a JWT.
- `POST /api/auth/logout` - Endpoint to process logout logic.

**Roles defined (`Role` Enum):**
- `SYSTEM_ADMIN`: Core setup and user role management.
- `MANAGEMENT`: Views aggregated global statistics across cases and users.
- `LEGAL_SUPERVISOR`: Assigns cases to officers, approves critical tasks and Approves the final contract revisions mapping to a digital signature for agreements.
- `LEGAL_OFFICER`: Manages and works on specifically assigned cases and reviews agreements revisions.
- `USER`: Base role.Create agreement drafts reqeust reviews and add revisions.

---
## 2. System Administration

Administrators hold complete control over other user's roles and access constraints, enabling them to adjust system functionality to match the evolving organization hierarchy.

**Key Endpoints:**
- `GET /api/admin` - Retrieves all operational system users.
- `GET /api/admin/role-counts` - Aggregates counts of each distinct role and the individual approver logic mapped securely.
- `PATCH /api/admin/change-role` - Modifies a user's target role.
- `PATCH /api/admin/ban` / `PATCH /api/admin/unban` - Toggles database constraints to block system access.

---

## 3. Legal Case Handling (`/api/cases`)

Cases act as the foundational ledger. Regular members can create `INITIAL` cases which map safely over to Supervisors, who then assign them to specific Legal Officers. Legal Cases allow cross-communication through comments and supporting evidentiary document attachments via `DocumentService` (S3).

### Status Lifecycle
`NEW` -> `ACTIVE` -> `ON_HOLD` / `CLOSED`

**Key Endpoints:**
- `POST /api/cases` (Multipart) - Creates a new incoming legal issue, appending initial documentary attachments.
- `GET /api/cases/{id}` - Complete fetch containing the case details, assigned officer, comments, and attachments.
- `GET /api/cases/status-counts` & `GET /api/cases/officer-counts` - Analytics used by Management.
- `PATCH /api/cases/{id}/assign` (Supervisor) - Assigns to Legal Officer.
- `PATCH /api/cases/{id}/status` - Advances the working pipeline.
- `POST /api/cases/{id}/attachments` / `DELETE /api/cases/{id}/attachments/{docId}` - Safely mutations case files.

---

## 4. Agreement Approval (`/api/agreements`)

A structured module processing internal and external agreements via rigid approval checkpoints. It maps document versions tightly within `AgreementVersionEntity` permitting secure historical document access.

### Status Lifecycle
`DRAFT` -> `REVIEW_REQUESTED` -> `PENDING_APPROVAL` -> `APPROVED` -> `EXECUTED`

*(Also contains statuses: `REJECTED`, `EXPIRED`, `ARCHIVED`)*

**Key Endpoints:**
- `POST /api/agreements` (Multipart) - Initializes a new base agreement scope with underlying file parsing.
- `POST /api/agreements/{id}/revisions` (Multipart) - Puts a new revised file against an existing unapproved contract.
- `POST /api/agreements/{id}/request-review` - Triggers state transitions forwarding the flow over to the assigned `AGREEMENT_REVIEWER`.
- `POST /api/agreements/{id}/review` & `POST /api/agreements/{id}/approve` - Specialized decision mechanisms mapping notes to transitions.
- `POST /api/agreements/{id}/execute` - Wraps the flow mapping an automated crypto-key mapping via `digitallySignAgreement()`.

---

## 5. Core Services

### AWS S3 Document Management

The `DocumentService` is universally referenced for attaching binary objects to Cases and Agreements. It handles connection pooling securely against the injected environment AWS bucket. Revisions and initial loads securely pass multipart items scaling over standard storage into secure buckets, storing relational `Document` identities that track absolute URL resolution.

**Integration mapping:**
- Case attach -> AWS S3 upload (`bucketName`) -> `Document` repository tracking -> Attached Case reference table mapping.
