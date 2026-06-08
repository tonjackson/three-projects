# Three Projects Portfolio

A monorepo containing three full-stack web applications, each demonstrating different architectural patterns and business domains.

## Projects

### [Smart BI](./smart-bi) — Intelligent Data Visualization Platform

A business intelligence platform that converts natural language queries into data charts using AI.

- **Backend**: Spring Boot + MyBatis-Plus + RabbitMQ + Redis + COS
- **Frontend**: React + Umi Max + Ant Design Pro
- **AI Integration**: Moonshot API for intelligent chart generation
- **Message Queue**: RabbitMQ for async chart processing

### [AI Quiz](./ai-quiz) — AI-Powered Scoring Application

An application platform supporting multiple scoring strategies (AI-based and custom rule-based) with a companion mini-program.

- **Backend**: Spring Boot + MyBatis-Plus + Redis + COS
- **Frontend**: Vue.js + Arco Design
- **Mini Program**: Taro + React (MBTI test)
- **Scoring**: Strategy pattern with AI and rule-based implementations

### [Online Judge](./online-judge) — Code Submission & Judging System

An online judge system supporting code submission, automated judging, and question management.

- **Backend**: Spring Boot + MyBatis-Plus + Redis + COS
- **Frontend**: Vue.js + Arco Design
- **Judge Engine**: Strategy pattern with sandbox isolation
- **Code Sandbox**: Extensible code execution environment

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React / Vue.js / Taro |
| Backend | Spring Boot / MyBatis-Plus |
| Database | MySQL 8.0 |
| Cache | Redis / Redisson |
| Message Queue | RabbitMQ |
| Object Storage | Tencent COS |
| AI | Moonshot API |
| Containerization | Docker / Docker Compose |

## Quick Start

### Prerequisites

- JDK 17+
- Node.js 18+
- MySQL 8.0
- Maven 3.8+

### Using Docker Compose

```bash
cp .env.example .env
# Edit .env with your credentials
docker-compose up -d
```

### Local Development

1. Start MySQL and create databases: `yubi`, `yudada`, `yuoj`
2. Execute SQL scripts in each project's `backend/sql/` directory
3. Start backend services (each on different ports)
4. Start frontend dev servers

| Project | Backend Port | Frontend Port |
|---------|-------------|---------------|
| Smart BI | 8080 | 8000 |
| AI Quiz | 8101 | 5173 |
| Online Judge | 8121 | 5175 |

## License

MIT
