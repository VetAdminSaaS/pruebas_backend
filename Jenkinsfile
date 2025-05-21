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
                    dockerImage = docker.build("${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}")
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'SanFranciscoAWS']]) {
                    bat "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                }
            }
        }

        stage('Push Image to ECR') {
            steps {
                script {
                    dockerImage.push()
                    dockerImage.push('latest')
                }
            }
        }

        stage('Check AWS Identity') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'SanFranciscoAWS']]) {
                    bat 'aws sts get-caller-identity'
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'SanFranciscoAWS']]) {
                    withEnv([
                        "AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}",
                        "AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}",
                        "AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}"
                    ]) {
                        script {
                            def kubeConfigPath = "${env.WORKSPACE}\\.kube\\config"
                            bat """
                                aws eks update-kubeconfig --region ${AWS_REGION} --name eccomerceveterinariasanfrancisco --kubeconfig ${kubeConfigPath}
                                mkdir C:\\Users\\jenkins\\.kube 2>NUL
                                copy ${kubeConfigPath} C:\\Users\\jenkins\\.kube\\config
                                kubectl get nodes
                                kubectl set image deployment/backend-deployment backend-container=${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG} -n default
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            echo 'El pipeline falló en algún lugar del proyecto.'
        }
        success {
            echo 'El pipeline se ejecutó correctamente.'
        }
    }
}
