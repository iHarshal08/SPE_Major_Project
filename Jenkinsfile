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
            when { expression { false } }
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
            when { expression { false } }
            steps {
                script {
                    def trivyDir = "${env.WORKSPACE}/.trivy"
                    def trivyBinary = "${trivyDir}/trivy"
                    def trivyVersion = "0.50.2"
                    def trivyTar = "${trivyDir}/trivy.tar.gz"
                    def trivyUrl = "https://github.com/aquasecurity/trivy/releases/download/v${trivyVersion}/trivy_${trivyVersion}_Linux-64bit.tar.gz"
                    def imageNames = ['login', 'frontend', 'keyexchange', 'messaging']

                    // Create .trivy folder
                    sh "mkdir -p ${trivyDir}"
                    // Download and extract Trivy if not present
                    sh """
                        if [ ! -x "${trivyBinary}" ]; then
                            echo "Downloading Trivy v${trivyVersion}..."
                            curl -L ${trivyUrl} -o ${trivyTar}
                            tar -xzf ${trivyTar} -C ${trivyDir}
                            chmod +x ${trivyBinary}
                        else
                            echo "Trivy already installed at ${trivyBinary}, skipping download."
                        fi
                        """

                    // Run Trivy scans
                    imageNames.each { imageName ->
                        echo "Scanning iharshal/${imageName}:latest with Trivy..."
                        sh "${trivyBinary} image --severity HIGH,CRITICAL --skip-java-db-update --scanners vuln --skip-db-update --offline-scan --no-progress iharshal/${imageName}:latest || true"
                        }
                    }
                }
            }
        stage('Push Docker Images') {
            when { expression { false } }
            steps {
                script {
                    def serviceDirs = [
                        login      : 'loginService',
                        frontend   : 'secure-chat-frontend-vite',
                        keyexchange: 'keyExhangeService',
                        messaging  : 'messagingService'
                    ]

                    // Login to Docker Hub
                    sh "echo ${DOCKER_CREDS_PSW} | docker login -u ${DOCKER_CREDS_USR} --password-stdin"

                    serviceDirs.each { imageName, folder ->
                        echo "Pushing iharshal/${imageName}:latest to Docker Hub..."
                        sh "docker push iharshal/${imageName}:latest"
                    }

                    // Logout after push
                    sh "docker logout"
                }
            }
        }

        stage('Kubernetes Deployment via Ansible') {
              steps {
                sh '''
                  export LANG=en_US.UTF-8
                  export LC_ALL=en_US.UTF-8
                  pip3 install --user ansible kubernetes
                  ansible-galaxy collection install kubernetes.core
                  mkdir -p ~/.kube
                  cp /home/harshal/.kube/config ~/.kube/config
                  chmod 600 ~/.kube/config
                  ansible-playbook -vvv ansible/playbook.yml
                '''
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
