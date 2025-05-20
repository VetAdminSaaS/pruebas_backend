pipeline {
    agent any
    environment {
        AWS_REGION = 'us-east-1'
        ECR_REGISTRY = '478039852035.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO = 'eccomerceveterinariasanfrancisco-backend'
        IMAGE_TAG = "${env.GIT_COMMIT.take(7)}"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                // Compilar el proyecto con Maven (asegúrate de que mvn esté en el PATH)
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${ECR_REPO}:${IMAGE_TAG}")
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                bat """
                    aws ecr get-login-password --region %AWS_REGION% | docker login --username AWS --password-stdin %ECR_REGISTRY%
                """
            }
        }

        stage('Push Image to ECR') {
            steps {
                script {
                    docker.withRegistry("https://${ECR_REGISTRY}", 'aws-credentials-id') {
                        docker.image("${ECR_REPO}:${IMAGE_TAG}").push()
                        docker.image("${ECR_REPO}:${IMAGE_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                bat """
                    kubectl set image deployment/backend-deployment backend-container=%ECR_REGISTRY%/%ECR_REPO%:%IMAGE_TAG% -n default
                """
            }
        }
    }

    post {
        failure {
            echo 'El pipeline falló.'
        }
        success {
            echo 'El pipeline se ejecutó correctamente.'
        }
    }
}
