# Career Pilot AI

Career Pilot AI is a CSC325 capstone project developed by Team 01. It is a full-stack Java desktop application that helps users create a resume profile, generate tailored resumes and cover letters with Gemini AI, and save generated documents for later viewing.

## Project Overview

Career Pilot AI simplifies the process of preparing job application materials. Users can log in, maintain resume information, enter job details, generate tailored application documents, edit the generated output, and save completed documents.

The final project prioritizes a polished and functional minimum viable product rather than a larger set of partially completed features.

## Final MVP Status

The following features are implemented and working:

- JavaFX desktop frontend
- Spring Boot REST API backend
- Login screen and application navigation
- Resume profile creation and storage
- Resume profile retrieval
- Job description and job information input
- Gemini AI resume generation
- Gemini AI cover letter generation
- Editable generated output
- Generated document storage
- Saved document retrieval and display
- Firebase Cloud Firestore integration
- Firestore Emulator support for local development
- CSS-styled JavaFX interface
- User-friendly dashboard based on the final MVP

## Core Features

### Login

Users enter the application through the login screen.

### Resume Profile

Users can create and save profile information used when generating application materials.

### AI Document Generation

Users can enter company information, a job title, and a job description to generate:

- A tailored resume
- A tailored cover letter

Generated content can be reviewed and edited before saving.

### Saved Documents

Generated resumes and cover letters can be saved to Firestore and viewed from the Saved Documents screen.

## Final MVP Scope

The submitted MVP includes:

1. Login
2. Resume profile management
3. Resume generation
4. Cover letter generation
5. Editable generated output
6. Saved document storage
7. Saved document retrieval
8. Spring Boot backend integration
9. Firestore persistence
10. Gemini API integration

The following proposed features were intentionally removed from the final MVP:

- Application tracker
- Interview preparation module
- Dashboard analytics
- Multiple resume versions
- Full multi-user account support
- PDF or DOCX export

These features were excluded so the team could prioritize stability, integration, testing, and polish.

## Technology Stack

- **Primary Language:** Java
- **Frontend:** JavaFX
- **Styling:** CSS
- **Backend:** Spring Boot
- **Database:** Firebase Cloud Firestore
- **Local Development Database:** Firestore Emulator
- **AI Integration:** Gemini API
- **Build Tool:** Maven Wrapper
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

Complete installation, environment configuration, troubleshooting, and manual startup instructions are available in:

[docs/SETUP.md](docs/SETUP.md)

### Before You Begin

Install the following:

- Java 21 or newer
- Node.js and npm
- Firebase CLI
- Gemini API key

Create a local `.env` file from the example:

```bash
cp .env.example .env
```

Add your Gemini API key:

```text
GEMINI_API_KEY=your-gemini-api-key
```

---

## Running the Application

Three processes must be running:

1. Firestore Emulator
2. Spring Boot Backend
3. JavaFX Frontend

### Mac

Open Terminal 1:

```bash
./scripts/run-firestore-emulator.sh
```

Open Terminal 2:

```bash
./scripts/run-backend.sh
```

Open Terminal 3:

```bash
./scripts/run-frontend.sh
```

### Windows PowerShell

Open PowerShell 1:

```powershell
.\scripts\run-firestore-emulator.ps1
```

Open PowerShell 2:

```powershell
.\scripts\run-backend.ps1
```

Open PowerShell 3:

```powershell
.\scripts\run-frontend.ps1
```

### Local URLs

```text
Backend: http://localhost:8080
Firestore Emulator UI: http://localhost:4000
Firestore Emulator Host: localhost:8081
```

For complete setup instructions, troubleshooting, and environment configuration, see:

**docs/SETUP.md**

## Development Workflow

The project uses a branch-based GitHub workflow.

- `main` contains the stable, final submission code.
- `develop` serves as the primary integration branch.
- Team members complete work on individual feature branches.
- Pull requests are reviewed and merged into `develop`.
- Fully tested code is merged from `develop` into `main` for release.

## Team Roles

| Team Member | Role | Main Responsibility |
| --- | --- | --- |
| Jillian Suarez | Project Manager / Integration / QA | Project coordination, GitHub administration, integration, testing, documentation, and final submission |
| Sohan Pattanaik | Backend Lead | Spring Boot backend, Firestore integration, Gemini API integration, and setup documentation |
| Keith Parisette | Database Support | Firestore review, data models, and persistence support |
| Fabian Vasquiez | Frontend Lead | JavaFX screens, navigation, dashboard, and CSS styling |
| Joseph Quillo | UI Support | Interface improvements, layout updates, and usability enhancements |
| Ana Garcia | Testing / Documentation Support | Test checklist, bug reporting, UI support, and documentation review |

## Known Limitations

- Gemini may occasionally return a temporary `503 UNAVAILABLE` response during periods of high demand.
- A valid Gemini API key is required for resume and cover letter generation.
- The current MVP uses a single demo-user Firestore path for local development and demonstration.
- Full multi-user authentication is outside the scope of the final MVP.
- PDF and DOCX export are not included.
- The application tracker, interview module, and dashboard analytics were deferred to future development.

## Course Information

**Course:** CSC325 Capstone Project

**Team:** Group 01

**Project:** Career Pilot AI

**Prepared by:** CSC325 Team 01
