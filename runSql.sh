#!/bin/bash

# Name of the MySQL pod
POD_NAME=$(kubectl get pod -l app=mysql -o jsonpath="{.items[0].metadata.name}")

# Path to your SQL file (edit if needed)
SQL_FILE="./initialize.sql"

# Credentials — change if you updated them in your YAML
MYSQL_USER="root"
MYSQL_PASSWORD="Harshal@p0808"
MYSQL_DB="chat"

echo "📦 Copying $SQL_FILE into pod $POD_NAME..."
kubectl cp "$SQL_FILE" "$POD_NAME:/initialize.sql"

echo "🐚 Executing SQL script inside the pod..."
kubectl exec -it "$POD_NAME" -- bash -c "mysql -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DB < /initialize.sql"

echo "✅ SQL script executed!"

