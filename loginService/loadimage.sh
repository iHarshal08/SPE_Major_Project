./mvnw clean install
docker build -t login:latest .
minikube image load login:latest
