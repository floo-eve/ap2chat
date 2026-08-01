# ap2-chat

A local LLM chat app built with Spring AI, Ollama, and Thymeleaf. Conversation history is persisted per-conversation in PostgreSQL via Spring AI's JDBC chat memory repository.

## Prerequisites

- Java 25 (`java-25-openjdk-devel` for `javac`)
- [Ollama](https://ollama.com) running locally with a chat model pulled, e.g. `ollama pull llama3.2`
- `podman` for running PostgreSQL in a container

## Running

```bash
./start-db.sh    # start the PostgreSQL container (idempotent)
./start-app.sh   # run the app (foreground) at http://localhost:8080
```

To stop:

```bash
./stop-app.sh
./stop-db.sh
```

## Login

The app requires sign-in (`/login`). Users are currently a fixed, hardcoded list configured in `application.properties` — this is a placeholder until real authentication (or SSO) is wired up. Each user only ever sees their own conversation history: chat memory is keyed by the authenticated username, not by anything the client sends.

Default demo credentials (change these before any real use):

- `floo` / `changeme123`
- `guest` / `changeme123`

## Configuration

See `src/main/resources/application.properties`:

- `spring.ai.ollama.*` — Ollama base URL and chat model
- `spring.datasource.*` — PostgreSQL connection (database `ap2chat`, user/password `ap2chat`)
- `spring.ai.chat.memory.repository.jdbc.initialize-schema=always` — forces schema creation on startup, since Spring Boot's automatic "embedded database" detection doesn't recognize a containerized Postgres as embedded
- `app.security.users[N].username` / `app.security.users[N].password` — the fixed demo users (see `SecurityConfig`/`SecurityProperties`); swap for a real `UserDetailsService` or OAuth2/OIDC login later without touching the rest of the app

## Querying the database directly

The PostgreSQL container has no exposed client on the host, so the simplest way in is via the container's own `psql`:

```bash
./start-db.sh   # if not already running
podman exec -it ap2chat-postgres psql -U ap2chat -d ap2chat
```

That drops into an interactive `psql` shell:

```sql
\dt                                    -- list tables
\d spring_ai_chat_memory               -- describe the chat memory table
SELECT conversation_id, type, content, timestamp
FROM spring_ai_chat_memory
ORDER BY conversation_id, sequence_id;
```

For a one-off query without the interactive shell:

```bash
podman exec ap2chat-postgres psql -U ap2chat -d ap2chat -c \
  "SELECT conversation_id, count(*) FROM spring_ai_chat_memory GROUP BY conversation_id;"
```

To connect from a native client on the host instead (e.g. a GUI tool or a locally installed `psql`), the container exposes the database on `localhost:5432` with database/user/password all set to `ap2chat`. That requires installing the `postgresql` client package (`sudo dnf install postgresql`), which needs an interactive terminal for the sudo password — `podman exec` above requires no local install.
