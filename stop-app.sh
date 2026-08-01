#!/usr/bin/env bash
set -euo pipefail

PIDS=$(pgrep -f "ch\.devzone\.ap2chat\.Ap2ChatApplication\$" || true)

if [ -z "$PIDS" ]; then
    echo "ap2-chat is not running."
    exit 0
fi

echo "Stopping ap2-chat (PID: $PIDS)..."
kill -TERM $PIDS

for _ in $(seq 1 30); do
    if ! kill -0 $PIDS 2>/dev/null; then
        echo "ap2-chat stopped."
        exit 0
    fi
    sleep 1
done

echo "ap2-chat did not stop in time, sending SIGKILL..."
kill -KILL $PIDS 2>/dev/null || true
