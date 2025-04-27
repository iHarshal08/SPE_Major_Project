
./mvnw clean install
docker build -t keyexchange:latest .
minikube image load keyexchange:latest

