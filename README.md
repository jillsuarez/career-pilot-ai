# Career Pilot AI

Career Pilot AI is a CSC325 capstone project developed by Team 01. The application is a full-stack desktop system designed to help students and job seekers organize the job application process. Users will be able to manage resume information, track job applications, generate tailored resume and cover letter drafts, and access interview preparation tools.

## Project Overview

Career Pilot AI is intended to reduce the time and effort involved in applying to multiple jobs by centralizing the application workflow into one organized desktop application. The system will allow users to store resume details, enter job descriptions, track application statuses, and generate job-specific application materials.

## Current MVP Status

Implemented now:

- JavaFX desktop frontend
- Spring Boot backend
- Firebase Cloud Firestore persistence
- Firestore Emulator support for local development
- Gemini AI resume and cover letter generation
- Resume profile builder
- Job description input
- Editable generated output
- Saved generated documents

Later phases:

- Login/register
- Application tracker
- Mock interview practice
- PDF or DOCX export

## Core Features

- User account/profile management
- Resume profile storage
- Job application tracker
- Application status updates
- Tailored resume draft generation
- Cover letter draft generation
- Interview question generation
- Saved generated documents
- JavaFX desktop user interface with CSS styling

## Technology Stack

- **Primary Language:** Java
- **Frontend:** JavaFX
- **Styling:** CSS
- **Backend:** Spring Boot
- **Database:** Firebase Cloud Firestore
- **Local Database Option:** Firestore Emulator
- **AI Integration:** Gemini API
- **Build Tool:** Maven wrapper
- **Version Control:** GitHub

## Project Structure

```text
career-pilot-ai/
├── backend/
├── frontend/
├── docs/
├── scripts/
├── secrets/
├── firebase.json
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── .gitignore
```

## Quickstart

Full setup instructions are in [docs/SETUP.md](docs/SETUP.md).

Mac:

```bash
chmod +x mvnw scripts/*.sh
cp .env.example .env
./scripts/run-firestore-emulator.sh
```

Open a second terminal:

```bash
./scripts/run-backend.sh
```

Open a third terminal:

```bash
./scripts/run-frontend.sh
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
.\scripts\run-firestore-emulator.ps1
```

Open a second PowerShell window:

```powershell
.\scripts\run-backend.ps1
```

Open a third PowerShell window:

```powershell
.\scripts\run-frontend.ps1
```

Useful URLs:

```text
Backend: http://localhost:8080
Firestore Emulator UI: http://localhost:4000
Firestore Emulator host: localhost:8081
```

## Required Tools

- Java 21 or newer
- Node.js and npm for Firebase CLI
- Firebase CLI for the Firestore Emulator
- Gemini API key for AI generation

Install Firebase CLI:

```bash
npm install -g firebase-tools
```

## Development Workflow

This project will use a branch-based workflow.

- `main` will contain stable, final code.
- `develop` will be used as the main integration branch.
- Team members should create feature branches for assigned tasks.
- Pull requests should be made into `develop`.
- Code should be reviewed before merging.

Example branch names:

```text
feature/javafx-login
feature/application-tracker
feature/database-models
feature/ai-integration
```

## Team Roles

| Team Member     | Role                                 | Main Responsibility                                                   |
| --------------- | ------------------------------------ | --------------------------------------------------------------------- |
| Jillian Suarez  | Project Manager / Integration / QA   | GitHub setup, task tracking, integration, documentation, final review |
| Sohan Pattanaik | Backend Lead                         | Spring Boot setup, backend logic, AI API integration                  |
| Keith Parisette | Database Lead                        | Firebase/database setup, data models, persistence                     |
| Fabian Vasquiez | Frontend Lead                        | JavaFX screens, navigation, CSS styling                               |
| Joseph Quillo   | Application Tracker Lead             | Job application tracker module and status workflow                    |
| Ana Garcia      | Testing / UI Support / Documentation | Testing, bug reports, UI support, documentation review                |

## Current Sprint Goal

The first development sprint focuses on setting up the project architecture and preparing the team to begin core feature development.

Sprint 1 priorities:

- Create GitHub repository
- Set up project folder structure
- Set up JavaFX frontend project
- Set up Spring Boot backend project
- Design initial database schema
- Create branch structure
- Draft initial wireframes

## MVP Goal

The minimum viable product should allow a user to:

1. Open the JavaFX desktop application.
2. Create or access a user profile.
3. Add resume information.
4. Add and track job applications.
5. Update application status.
6. Enter a job description.
7. Generate a tailored resume draft and cover letter draft.

## Future / Stretch Features

If time allows, the team may expand the project with:

- Interview practice question history
- Calendar reminders
- Job board import support
- Additional export formats
- Enhanced dashboard analytics

## Course Information

Course: CSC325 Capstone Project  
Team: Group 01  
Project Name: Career Pilot AI  
Prepared by: CSC325 Team 01
