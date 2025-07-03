pipeline {
    agent {
        label 'backend'
    }

    environment {
        AWS_REGION   = 'us-east-1'
        ECR_REGISTRY = '478039852035.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO     = 'eccomerceveterinariasanfrancisco-backend'
        IMAGE_TAG    = "${GIT_COMMIT.take(7)}"
        PATH         = "/usr/local/bin:$PATH" // 🔧 Asegura que aws y kubectl funcionen
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

        stage('Build & Push Docker Image') {
            steps {
                sh """
                    docker build -t $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG .
                    docker push $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    docker tag $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPO:latest
                    docker push $ECR_REGISTRY/$ECR_REPO:latest
                """
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
                withEnv(["KUBECONFIG=${env.WORKSPACE}/.kube/config"]) {
                    sh 'kubectl get deployments -n default'
                }
            }
        }
    }

    post {
        failure {
            echo '❌ El pipeline falló en alguna etapa.'
        }
        success {
            echo '✅ El pipeline se ejecutó correctamente.'
        }
    }
}
