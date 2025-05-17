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
                    // Map Jenkins image name to actual directory
                    def serviceDirs = [
                        login      : 'loginService',
                        frontend   : 'secure-chat-frontend-vite',
                        keyexchange: 'keyExhangeService',
                        messaging  : 'messagingService'
                    ]

                    serviceDirs.each { imageName, folder ->
                        dir(folder) {
                            echo "Building ${imageName} from ${folder}..."

                            // Step 1: Maven package (only if pom.xml exists)
                            if (fileExists('pom.xml')) {
                                sh 'mvn clean package -DskipTests'
                            } else {
                                echo "No pom.xml found in ${folder}, skipping Maven step."
                            }

                            // Step 2: Docker build
                            sh "docker build -t iharshal/${imageName}:latest ."

                            // Step 3: Install Trivy if not already
                            sh '''
                                if ! [ -x "$(command -v trivy)" ]; then
                                    echo "Installing Trivy..."
                                    curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin
                                fi
                            '''

                            // Step 4: Trivy scan (non-blocking)
                            echo "Scanning iharshal/${imageName}:latest with Trivy..."
                            sh "trivy image --severity HIGH,CRITICAL --no-progress iharshal/${imageName}:latest || true"
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
