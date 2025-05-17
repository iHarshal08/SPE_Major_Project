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

        stage('Build Docker Images') {
            steps {
                script {
                    def serviceDirs = [
                        login      : 'loginService',
                        frontend   : 'secure-chat-frontend-vite',
                        keyexchange: 'keyExhangeService',
                        messaging  : 'messagingService'
                    ]

                    serviceDirs.each { imageName, folder ->
                        dir(folder) {
                            echo "Building ${imageName} from ${folder}..."

                            if (fileExists('pom.xml')) {
                                sh 'mvn clean package -DskipTests'
                            } else {
                                echo "No pom.xml found in ${folder}, skipping Maven step."
                            }

                            sh "docker build -t iharshal/${imageName}:latest ."
                        }
                    }
                }
            }
        }

        stage('Trivy Scan') {
            steps {
                script {
                    def trivyDir = "${env.WORKSPACE}/.trivy"
                    def trivyBinary = "${trivyDir}/trivy"
                    def imageNames = ['login', 'frontend', 'keyexchange', 'messaging']

                    // Create .trivy folder in workspace if it doesn't exist
                    sh "mkdir -p ${trivyDir}"

                    // Download Trivy if not already present
                    sh """
                        if [ ! -x "${trivyBinary}" ]; then
                            echo "Downloading Trivy to ${trivyBinary}..."
                            curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b ${trivyDir}
                            chmod +x ${trivyBinary}
                        else
                            echo "Trivy already installed at ${trivyBinary}, skipping download."
                        fi
                    """

                    // Run Trivy scan on each image
                    imageNames.each { imageName ->
                        echo "Scanning iharshal/${imageName}:latest with Trivy..."
                        sh "${trivyBinary} image --severity HIGH,CRITICAL --no-progress iharshal/${imageName}:latest || true"
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
