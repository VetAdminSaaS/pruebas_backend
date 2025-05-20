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
            tools {
                maven 'Maven 3'
            }
            steps {
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
               withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials-id']]) {
                   bat '''
                       aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 478039852035.dkr.ecr.us-east-1.amazonaws.com
                   '''
               }
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
