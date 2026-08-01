#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME=ap2chat-postgres

if podman container exists "$CONTAINER_NAME"; then
    if [ "$(podman inspect -f '{{.State.Running}}' "$CONTAINER_NAME")" = "true" ]; then
        echo "PostgreSQL container '$CONTAINER_NAME' is already running."
    else
        echo "Starting existing PostgreSQL container '$CONTAINER_NAME'..."
        podman start "$CONTAINER_NAME"
    fi
else
    echo "Creating PostgreSQL container '$CONTAINER_NAME'..."
    podman run -d \
        --name "$CONTAINER_NAME" \
        -e POSTGRES_DB=ap2chat \
        -e POSTGRES_USER=ap2chat \
        -e POSTGRES_PASSWORD=ap2chat \
        -p 5432:5432 \
        -v ap2chat-postgres-data:/var/lib/postgresql/data \
        docker.io/library/postgres:16
fi

echo "Waiting for PostgreSQL to accept connections..."
until podman exec "$CONTAINER_NAME" pg_isready -U ap2chat -d ap2chat >/dev/null 2>&1; do
    sleep 1
done
echo "PostgreSQL is ready on localhost:5432."
