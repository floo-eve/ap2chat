#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME=ap2chat-postgres

if ! podman container exists "$CONTAINER_NAME"; then
    echo "PostgreSQL container '$CONTAINER_NAME' does not exist."
    exit 0
fi

if [ "$(podman inspect -f '{{.State.Running}}' "$CONTAINER_NAME")" = "true" ]; then
    echo "Stopping PostgreSQL container '$CONTAINER_NAME'..."
    podman stop "$CONTAINER_NAME"
else
    echo "PostgreSQL container '$CONTAINER_NAME' is already stopped."
fi
