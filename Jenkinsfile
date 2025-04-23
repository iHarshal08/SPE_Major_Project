pipeline {
    agent any

    environment {
        DOCKER_REPO = 'iharshal/spe'
        DOCKER_CREDS = credentials('DockerHubCred')
        EMAIL_RECIPIENTS = 'Harshal.Purohit@iiitb.ac.in'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "📦 Checking out code from GitHub..."
                checkout scm
            }
        }

        stage('Build Docker Images') {
            parallel {

                stage('Build: Login Service') {
                     steps {
                         dir('loginService') {
                            script {
                                 echo "🔧 Building Login Service image..."
                                 sh "mvn clean package -DskipTests"
                                 sh "docker build -t ${DOCKER_REPO}:login ."
                            }
                         }
                     }
                }

                stage('Build: Key Exchange') {
                    steps {
                        dir('keyExhangeService') {
                            script {
                                echo "🔧 Building Key Exchange image..."
                                sh "mvn clean package -DskipTests"
                                sh "docker build -t ${DOCKER_REPO}:keyexchange ."
                            }
                        }
                    }
                }

                stage('Build: Messaging Service') {
                    steps {
                        dir('messagingService') {
                            script {
                                echo "🔧 Building Messaging Service image..."
                                sh "mvn clean package -DskipTests"
                                sh "docker build -t ${DOCKER_REPO}:messaging ."
                            }
                        }
                    }
                }

                stage('Build: Frontend') {
                    steps {
                        dir('secure-chat-frontend-vite') {
                            script {
                                echo "🔧 Building Frontend image..."
                                sh "docker build -t ${DOCKER_REPO}:frontend ."
                            }
                        }
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    echo "🔐 Logging into Docker Hub..."
                    sh """
                        echo ${DOCKER_CREDS_PSW} | docker login -u ${DOCKER_CREDS_USR} --password-stdin
                        docker push ${DOCKER_REPO}:keyexchange
                        docker push ${DOCKER_REPO}:messaging
                        docker push ${DOCKER_REPO}:login
                        docker push ${DOCKER_REPO}:frontend
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo "🚀 Deploying services to Kubernetes..."
                sh 'kubectl apply -f k8s/'
            }
        }
    }

    post {
        success {
            echo "✅ Build and deployment successful!"
            mail to: "${EMAIL_RECIPIENTS}",
                 subject: "✅ Jenkins Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Good news! The build and deployment of '${env.JOB_NAME}' (#${env.BUILD_NUMBER}) was successful.\n\nCheck it at: ${env.BUILD_URL}"
        }

        failure {
            echo "❌ Build failed."
            mail to: "${EMAIL_RECIPIENTS}",
                 subject: "❌ Jenkins Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Oops! The build of '${env.JOB_NAME}' (#${env.BUILD_NUMBER}) failed.\n\nCheck the logs: ${env.BUILD_URL}"
        }
    }

    triggers {
        githubPush()
    }
}
