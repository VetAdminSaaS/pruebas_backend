pipeline {
    agent { label 'backend' }

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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Login to AWS ECR') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'SanFranciscoAWS'
                ]]) {
                    sh '''
                        aws ecr get-login-password --region $AWS_REGION | \
                        docker login --username AWS --password-stdin $ECR_REGISTRY
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                script {
                    dockerImage.push()
                    dockerImage.push('latest')
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'SanFranciscoAWS'
                ]]) {
                    script {
                        def kubeconfigPath = "${env.WORKSPACE}/.kube/config"

                        sh """
                            mkdir -p ${env.WORKSPACE}/.kube
                            aws eks update-kubeconfig \
                                --region $AWS_REGION \
                                --name eccomerceveterinariasanfrancisco \
                                --kubeconfig ${kubeconfigPath}
                        """

                        withEnv(["KUBECONFIG=${kubeconfigPath}"]) {
                            sh 'kubectl get nodes'
                            sh """
                                kubectl set image deployment/backend-deployment \
                                    backend-container=$ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG \
                                    -n default
                            """
                        }
                    }
                }
            }
        }

        stage('Verificar Deployments') {
            steps {
                sh 'kubectl get deployments -n default'
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
