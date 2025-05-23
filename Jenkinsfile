pipeline {
    agent any

    environment {
        DOCKER_CREDS = credentials('DockerHubCred')
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Jenkinsfile is updated!"
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
            def trivyVersion = "0.50.2"
            def trivyTar = "${trivyDir}/trivy.tar.gz"
            def trivyUrl = "https://github.com/aquasecurity/trivy/releases/download/v${trivyVersion}/trivy_${trivyVersion}_Linux-64bit.tar.gz"
            def trivyBinary = "${trivyDir}/trivy"
            def imageNames = ['login', 'frontend', 'keyexchange', 'messaging']

            sh """
                set -eux

                mkdir -p "${trivyDir}"

                if [ ! -f "${trivyBinary}" ]; then
                    echo "📥 Downloading Trivy to workspace..."
wget -O "${trivyTar}" "${trivyUrl}" || { echo '❌ wget download failed'; exit 1; }


                    echo "📦 Extracting Trivy..."
                    tar -xzf "${trivyTar}" -C "${trivyDir}" || { echo '❌ Extraction failed'; exit 1; }

                    echo "🎯 Making Trivy executable..."
                    chmod +x "${trivyDir}/trivy" || chmod +x ${trivyDir}/trivy_* || true
                    mv ${trivyDir}/trivy_* "${trivyBinary}" 2>/dev/null || true

                    echo "🔍 Verifying binary:"
                    ls -l "${trivyBinary}"

                    if [ ! -x "${trivyBinary}" ]; then
                        echo "❌ Trivy binary not executable"
                        exit 1
                    fi
                else
                    echo "✅ Trivy already staged at ${trivyBinary}"
                fi
            """

            // Run scans
            imageNames.each { imageName ->
                echo "🚨 Scanning iharshal/${imageName}:latest with Trivy..."
                sh "${trivyBinary} image --severity HIGH,CRITICAL --skip-java-db-update --scanners vuln --skip-db-update --offline-scan --no-progress iharshal/${imageName}:latest || true"
            }
        }
    }
}

        stage('Push Docker Images') {
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
             // Use a single shell script block
             sh '''
               export LC_ALL=en_US.UTF-8
               export LANG=en_US.UTF-8
               export LANGUAGE=en_US.UTF-8
               python3 -m pip install --upgrade --user pip
               python3 -m pip install --user ansible kubernetes
               minikube delete
               minikube start --driver=docker
               echo "Waiting for Minikube to stabilize..."
               sleep 20
               ansible-playbook -i ansible/inventory.ini -vvv ansible/playbook.yml
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
