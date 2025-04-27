#!/bin/bash
cd secure-chat-frontend-vite || { echo "❌ Frontend folder not found"; exit 1; }

# Clean old build output
rm -rf dist

# Reinstall & rebuild frontend
npm install
npm run build

# Build Docker image (fresh)
docker build --no-cache -t frontend:latest .

# Load into Minikube
minikube image load frontend:latest

# Optionally restart the pod
# kubectl rollout restart deployment frontend-deployment

