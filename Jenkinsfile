pipeline {
    agent any

    environment {
        DOCKER_CREDS = credentials('DockerHubCred')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/iHarshal08/SPE_Major_Project.git'
            }
        }

        stage('Build and Trivy Scan') {
            steps {
                script {
                    def services = ['login', 'frontend', 'keyexchange', 'messaging']
                    services.each { service ->
                        dir(service) {
                            echo "Building ${service} service..."

                            // Step 1: Maven package
                            sh 'mvn clean package -DskipTests'

                            // Step 2: Docker build
                            sh "docker build -t iharshal/${service}:latest ."

                            // Step 3: Install Trivy if not already
                            sh '''
                                if ! [ -x "$(command -v trivy)" ]; then
                                    echo "Installing Trivy..."
                                    curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin
                                fi
                            '''

                            // Step 4: Trivy scan (non-blocking)
                            echo "Scanning iharshal/${service}:latest with Trivy..."
                            sh "trivy image --severity HIGH,CRITICAL --no-progress iharshal/${service}:latest || true"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed (success/failure).'
        }
        success {
            echo 'Build and scan completed successfully.'
        }
        failure {
            echo 'Build failed. Check logs above.'
        }
    }
}
