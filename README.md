# CommitRisk — AI-Powered Git Commit Risk Analyzer

A developer tool that analyzes a Git commit diff, calculates a transparent, rule-based **risk score**, detects which sensitive areas of the codebase were touched, and uses an LLM to explain the risk in plain language and recommend targeted tests — all shown on a modern React dashboard.

> **What this tool does *not* do:** it does not detect actual bugs. It estimates **change risk** from measurable characteristics of a diff (size, files touched, sensitive areas modified) and augments that with AI-assisted reasoning. Treat the output as a prioritization signal for code review and testing effort, not a defect report.

---

## 1. Problem Statement

Not all commits carry equal risk. A one-line typo fix in a README and a 300-line change touching authentication, payments, and the database schema are treated identically by most CI pipelines — reviewers have to manually notice which commits deserve extra scrutiny. Small teams and students rarely have static-analysis or risk-scoring tooling wired into their workflow.

## 2. Solution

CommitRisk parses a pasted Git diff, runs it through a fully transparent, configurable scoring engine, and produces:

- A **0–100 risk score** and a **LOW / MEDIUM / HIGH / CRITICAL** classification
- A breakdown of exactly **which factors** contributed to the score and by how much
- An **AI-generated explanation** in plain English (with an automatic, deterministic fallback if no AI key is configured, so the tool always works)
- A list of **recommended tests** targeted at what actually changed
- A persisted **history** of every analysis, with dashboard-level aggregates

## 3. Features

- ✅ Paste-a-diff analysis (no Git server access required)
- ✅ Transparent, additive rule-based risk engine — every point is explainable
- ✅ Sensitive-area detection: auth, security, payments, database, API, config, exception handling
- ✅ AI explanation layer with a provider-agnostic interface and automatic fallback
- ✅ Full analysis history with search/filter
- ✅ Dashboard with aggregate stats and a recent-scores chart
- ✅ Responsive, dark-themed developer-tool UI
- ✅ Centralized error handling — no stack traces or secrets ever reach the frontend
- ✅ Unit tests for parsing and scoring, covering LOW/MEDIUM/HIGH/CRITICAL cases

---

## 4. Architecture

```
                    ┌─────────────────────────┐
                    │   React (Vite) Frontend │
                    │  Dashboard · Analyze ·   │
                    │  History · Detail views  │
                    └────────────┬─────────────┘
                                 │ REST (JSON) / axios
                                 ▼
                    ┌─────────────────────────┐
                    │   Spring Boot Backend    │
                    │                          │
                    │  Controller              │
                    │      │                   │
                    │      ▼                   │
                    │  CommitAnalysisService    │───────┐
                    │      │                   │        │
                    │      ▼                   │        ▼
                    │  DiffParser  →  RiskAnalyzer   AIAnalysisService
                    │                          │   (LLM call, or rule-based
                    │      │                   │    fallback on failure)
                    │      ▼                   │
                    │  CommitAnalysisRepository │
                    └────────────┬─────────────┘
                                 │ JPA / Hibernate
                                 ▼
                    ┌─────────────────────────┐
                    │      MySQL Database      │
                    │  commit_analysis         │
                    │  risk_factor             │
                    │  recommended_test        │
                    └─────────────────────────┘
```

### Backend package layout

```
backend/src/main/java/com/commitrisk/
├── controller/      REST endpoints
├── service/         Orchestrates the analysis pipeline
├── parser/          Unified diff -> ParsedDiff / FileChange
├── analyzer/        Rule-based scoring engine + configurable constants
├── ai/               AIAnalysisService interface, LLM impl, rule-based fallback
├── model/            JPA entities
├── repository/       Spring Data JPA repositories
├── dto/              API request/response contracts
├── config/           CORS, RestTemplate, AI properties binding
└── exception/        Custom exceptions + centralized @RestControllerAdvice
```

---

## 5. Tech Stack

| Layer     | Technology                                              |
|-----------|----------------------------------------------------------|
| Frontend  | React 18, Vite, Tailwind CSS, Recharts, Axios, React Router |
| Backend   | Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Maven |
| Database  | MySQL (H2 in-memory for zero-setup local dev)            |
| AI        | Any LLM reachable via HTTP (defaults to Anthropic's Messages API), configured entirely through environment variables |

---

## 6. Risk Calculation Methodology

The engine is a simple, auditable **additive point system** — there is no hidden ML model behind the number. Every constant lives in `RiskScoringConfig` and is overridable via environment variables, so the model can be tuned without touching Java code.

| Factor                              | Points (default) |
|--------------------------------------|-------------------|
| Large change (≥ 300 lines)           | +15               |
| Moderate change (≥ 100 lines)        | +8                |
| Many files touched (≥ 6)             | +10               |
| Several files touched (≥ 3)          | +5                |
| Database modification                | +15               |
| Security-related change              | +20               |
| Payment/transaction logic            | +20               |
| API/controller change                | +10               |
| Exception handling change            | +10               |
| Configuration/dependency change      | +10               |
| Sensitive file area (generic)        | +15               |

The raw total is capped to **0–100**, then classified:

| Score range | Level     |
|-------------|-----------|
| 0 – 30      | LOW       |
| 31 – 60     | MEDIUM    |
| 61 – 80     | HIGH      |
| 81 – 100    | CRITICAL  |

**These thresholds are project-defined heuristics for this tool, not an industry standard.**

Detection uses case-insensitive keyword/pattern matching against file paths and added/removed diff content (e.g. `SELECT`/`UPDATE`/`Repository`/`JPA` for database changes; `JWT`/`password`/`OAuth`/`token` for security; `@PostMapping`/`@RestController` for API changes). See `PatternLibrary.java` for the full keyword sets.

---

## 7. AI Integration

`AIAnalysisService` is an interface — the rest of the app never talks to a specific vendor directly. `LlmAIAnalysisService` sends the parsed diff, the rule-based score, and the detected factors to an LLM and asks for strict JSON back:

```json
{
  "summary": "...",
  "potentialRisks": ["...", "..."],
  "recommendedTests": ["...", "..."]
}
```

**Reliability guarantee:** if `AI_API_KEY` is unset, the AI provider is disabled, the network call fails, or the response can't be parsed, the service transparently falls back to `RuleBasedExplanationGenerator`, which builds the same structured result directly from the detected risk factors. The API always returns a complete response — it never depends on the AI call succeeding.

No API key is ever hardcoded; everything is read from environment variables (see `.env.example`).

---

## 8. Database Design

```
commit_analysis                risk_factor                 recommended_test
─────────────────              ─────────────────           ─────────────────
id (PK)                        id (PK)                     id (PK)
commit_message                 analysis_id (FK) ───┐        analysis_id (FK) ───┐
risk_score                     factor_name          │        test_description    │
risk_level (enum)              description          │        priority (enum)     │
files_changed                  impact                │                            │
lines_added                                          │                            │
lines_deleted           ◄────────────────────────────┘ ◄──────────────────────────┘
ai_summary                       (one-to-many, cascade all, orphan removal)
ai_explanation
ai_generated
created_at
```

Schema is created/updated automatically by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no manual migration step needed for this project's scope.

---

## 9. API Documentation

### `POST /api/analyze`
Analyze a diff and persist the result.

**Request**
```json
{
  "diff": "diff --git a/src/PaymentService.java ...",
  "commitMessage": "Add tax calculation to checkout"
}
```

**Response `201 Created`**
```json
{
  "id": 1,
  "commitMessage": "Add tax calculation to checkout",
  "riskScore": 55,
  "riskLevel": "MEDIUM",
  "filesChanged": 2,
  "linesAdded": 9,
  "linesDeleted": 1,
  "riskFactors": [
    { "name": "Payment/transaction logic", "description": "...", "impact": 20 },
    { "name": "Database modification", "description": "...", "impact": 15 }
  ],
  "aiSummary": "...",
  "potentialRisks": ["...", "..."],
  "recommendedTests": [
    { "description": "Test a successful end-to-end payment flow.", "priority": "MEDIUM" }
  ],
  "aiGenerated": true,
  "createdAt": "2026-09-20T10:15:00"
}
```

### `GET /api/analyses`
Returns a lightweight list of all past analyses, newest first.

### `GET /api/analyses/{id}`
Returns the full detail for one analysis (same shape as the `POST /api/analyze` response).

### `GET /api/dashboard/stats`
```json
{
  "totalAnalyses": 12,
  "highRiskCommits": 3,
  "averageRiskScore": 42.5,
  "latestAnalysis": { "id": 12, "commitMessage": "...", "riskScore": 55, "riskLevel": "MEDIUM", "filesChanged": 2, "createdAt": "..." }
}
```

### `GET /api/health`
Simple liveness check, returns `"OK"`.

All errors are returned as:
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "The diff content must not be empty." }
```

---

## 10. Installation & Running Locally

### Prerequisites
- Java 17+
- Node.js 18+
- Maven (or use the included `mvnw` if you add the wrapper)
- MySQL 8+ (optional — see the zero-setup dev mode below)

### Backend

```bash
cd backend
cp .env.example .env        # edit if needed
# Zero-setup mode (in-memory H2, no MySQL required):
mvn spring-boot:run

# With real MySQL:
# 1. CREATE DATABASE commit_risk_analyzer;
# 2. export SPRING_PROFILES_ACTIVE=mysql DB_USERNAME=root DB_PASSWORD=yourpassword
mvn spring-boot:run
```
Backend runs on **http://localhost:8080**.

To enable AI explanations, export your key before starting:
```bash
export AI_API_KEY=sk-ant-...
export AI_MODEL=claude-sonnet-4-6
```
Without a key, the app still works end-to-end using the rule-based explanation fallback.

### Frontend

```bash
cd frontend
npm install
npm run dev
```
Frontend runs on **http://localhost:5173** and proxies `/api/*` to the backend on port 8080.

### Running backend tests
```bash
cd backend
mvn test
```

---

## 11. Screenshots

_Add screenshots of the Dashboard, Analyze, and History pages here once you run the app locally._

---

## 12. Future Enhancements

- Optional GitHub integration: enter a repo URL, browse commits, fetch a diff, and analyze it directly (deliberately deferred — see project scope notes)
- Support for multiple AI providers with automatic failover between them
- Per-team configurable risk-scoring profiles stored in the database
- Export analysis reports as PDF
- Webhook support to auto-analyze commits on push

---

## 13. Project Scope Notes

This was built incrementally as a one-week, portfolio-focused MVP:

1. Project structure
2. Diff parsing
3. Risk scoring
4. REST APIs
5. Persistence
6. AI integration
7. React dashboard
8. Frontend/backend integration
9. Testing
10. Polish + documentation

Deliberately **out of scope** for this version: GitHub OAuth, microservices, Kubernetes, multi-provider AI failover, real-time collaboration, and enterprise deployment infrastructure — all noted as future enhancements above rather than half-implemented.
