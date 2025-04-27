#!/usr/bin/env bash

# Mapping: folder_name=image_name
declare -A services=(
  ["loginService"]="login"
  ["keyExhangeService"]="keyexchange"
  ["messagingService"]="messaging"
  ["secure-chat-frontend-*"]="frontend"
)

set -e

for dir in "${!services[@]}"; do
  image="${services[$dir]}"
  folder=$(find . -maxdepth 1 -type d -name "$dir" | head -n 1)

  if [[ -d "$folder" ]]; then
    echo "🔧 Building Maven project in $folder"
    cd "$folder"
    ./mvnw clean install

    echo "🐳 Building Docker image: $image:latest"
    docker build -t "$image:latest" .

    echo "📦 Loading $image into Minikube"
    minikube image load "$image:latest"

    echo "✅ $image:latest ready in Minikube"
    cd ..
  else
    echo "⚠️ Folder $dir not found, skipping"
  fi
done

echo "🎉 All services built and loaded successfully!"

