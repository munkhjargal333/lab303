#!/bin/bash

CLASS=ATMProcess
JAVA_FILE=ATMProcess.java
FIFOS=(/tmp/atm1.fifo /tmp/atm2.fifo /tmp/atm3.fifo)
PIDS=()
STATUS=("RUNNING" "RUNNING" "RUNNING")

is_number() {
  [[ "$1" =~ ^[0-9]+$ ]]
}

next_active() {
  for i in 0 1 2; do
    if [ "${STATUS[$i]}" = "RUNNING" ]; then
      echo $((i+1))
      return
    fi
  done
  echo 1
}

cleanup_and_exit() {
  echo ""
  echo "Ctrl+C detected. Stopping all ATMs..."
  for i in 0 1 2; do
    if [ "${STATUS[$i]}" = "RUNNING" ]; then
      if kill -0 "${PIDS[$i]}" 2>/dev/null; then
        echo "exit" > "${FIFOS[$i]}"
      fi
    fi
  done
  echo "Controller exited."
  exit 0
}

trap cleanup_and_exit INT

pgrep -f "java $CLASS" | xargs -r kill
sleep 1

javac "$JAVA_FILE" || exit 1

for f in "${FIFOS[@]}"; do
  rm -f "$f"
  mkfifo "$f"
done

for i in 0 1 2; do
  java $CLASS "${FIFOS[$i]}" &
  PIDS[$i]=$!
done

ACTIVE=1

while true; do
  clear
  echo "====== ATM CONTROLLER ======"
  echo ""

  for i in 0 1 2; do
    pid=${PIDS[$i]}
    marker=" "
    [ $((i+1)) -eq "$ACTIVE" ] && marker="▶"

    if ! kill -0 "$pid" 2>/dev/null; then
      STATUS[$i]="STOPPED"
    fi

    echo "$marker [$((i+1))] ATM-$((i+1)) PID=$pid ${STATUS[$i]}"
  done

  echo ""
  echo "1/2/3 → ATM сонгох | тоо (>=10) → дүн | Ctrl+C → гарах"
  echo ""

  read -p "Withdrawal amount (default ATM=$ACTIVE): " input

  case "$input" in
    1|2|3)
      ACTIVE=$input
      continue
      ;;
    *)
      idx=$((ACTIVE-1))

      if [ "${STATUS[$idx]}" = "STOPPED" ]; then
        ACTIVE=$(next_active)
        continue
      fi

      if ! is_number "$input"; then
        STATUS[$idx]="STOPPED"
        echo "$input" > "${FIFOS[$idx]}"
        sleep 0.5
        ACTIVE=$(next_active)
        continue
      fi

      if [ "$input" -lt 10 ]; then
        sleep 0.5
        continue
      fi

      echo "$input" > "${FIFOS[$idx]}"
      sleep 1
      continue
      ;;
  esac
done

