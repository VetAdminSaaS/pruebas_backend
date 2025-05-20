pipeline {
    agent any
    environment {
        AWS_REGION = 'us-east-1'
        ECR_REGISTRY = '478039852035.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO = 'eccomerceveterinariasanfrancisco-backend'
        IMAGE_TAG = "${env.GIT_COMMIT.take(7)}"
    }
    tools {
        maven 'Maven 3'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Construir la imagen y asignarla a una variable local solo en este bloque
                    dockerImage = docker.build("${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}")
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'SanFranciscoAWS']]) {
                    bat """
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                    """
                }
            }
        }

        stage('Push Image to ECR') {
            steps {
                script {
                    // Obtener la referencia a la imagen con docker.image() porque la variable no persiste entre etapas
                    def dockerImage = docker.image("${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}")
                    dockerImage.push()
                    dockerImage.push('latest')
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'SanFranciscoAWS']]) {
                    bat """
                        aws eks update-kubeconfig --region ${AWS_REGION} --name eccomerceveterinariasanfrancisco
                        kubectl set image deployment/backend-deployment backend-container=${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG} -n default
                    """
                }
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
