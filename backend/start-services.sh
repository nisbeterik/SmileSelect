#!/bin/bash

# Get the directory of the script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define service directories relative to the script's location
SERVICES=(
  "$SCRIPT_DIR/services/account-service"
  "$SCRIPT_DIR/services/appointment-service"
  "$SCRIPT_DIR/services/gateway-service"
  "$SCRIPT_DIR/services/notification-service"
  "$SCRIPT_DIR/services/dentist-service"
  "$SCRIPT_DIR/services/patient-service"
  "$SCRIPT_DIR/services/auth-service"
  "$SCRIPT_DIR/services/logging-service"

)

# Start each service
for SERVICE in "${SERVICES[@]}"; do
  echo "Starting service in $SERVICE..."
  (
    cd "$SERVICE" || exit
    mvn spring-boot:run &
  )
done

echo "All services are starting!"
