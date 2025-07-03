pipeline {
    agent {
        label 'backend'
    }

    environment {
        AWS_REGION   = 'us-east-1'
        ECR_REGISTRY = '478039852035.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO     = 'eccomerceveterinariasanfrancisco-backend'
        IMAGE_TAG    = "${GIT_COMMIT.take(7)}"
    }

    stages {
        stage(' Verificar herramientas instaladas') {
            steps {
                echo ' Verificando si aws, docker y kubectl están disponibles...'
                sh '''
                    which aws || echo " aws no está instalado"
                    which docker || echo " docker no está instalado"
                    which kubectl || echo " kubectl no está instalado"
                    aws --version || true
                    docker --version || true
                    kubectl version --client || true
                '''
            }
        }

        stage(' Checkout del código fuente') {
            steps {
                echo ' Obteniendo el código desde Git...'
                checkout scm
            }
        }

        stage(' Compilar JAR (mvn clean package)') {
            steps {
                echo ' Compilando el proyecto Java...'
                sh 'mvn clean package -DskipTests'
                echo ' JAR compilado exitosamente.'
            }
        }

        stage(' Login a AWS ECR') {
            steps {
                echo ' Autenticando en AWS ECR...'
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'SanFranciscoAWS'
                ]]) {
                    script {
                        try {
                            sh '''
                                aws ecr get-login-password --region $AWS_REGION | \
                                docker login --username AWS --password-stdin $ECR_REGISTRY
                            '''
                            echo ' Login exitoso en ECR.'
                        } catch (err) {
                            error " Fallo el login en ECR: ${err}"
                        }
                    }
                }
            }
        }

        stage(' Construir y subir imagen Docker') {
            steps {
                echo ' Construyendo imagen Docker...'
                sh """
                    docker build -t $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG .
                    docker push $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    docker tag $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPO:latest
                    docker push $ECR_REGISTRY/$ECR_REPO:latest
                """
                echo " Imagen Docker publicada con tag: $IMAGE_TAG"
            }
        }

        stage(' Desplegar en Amazon EKS') {
            steps {
                echo ' Iniciando despliegue a EKS...'
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'SanFranciscoAWS'
                ]]) {
                    script {
                        def kubeconfigPath = "${env.WORKSPACE}/.kube/config"
                        try {
                            sh """
                                mkdir -p ${env.WORKSPACE}/.kube
                                aws eks update-kubeconfig \
                                    --region $AWS_REGION \
                                    --name eccomerceveterinariasanfrancisco \
                                    --kubeconfig ${kubeconfigPath}
                            """
                            echo 'Configuración de acceso a EKS generada correctamente.'

                            withEnv(["KUBECONFIG=${kubeconfigPath}"]) {

                                // Asegura que el Deployment backend exista
                                sh '''
                                    if ! kubectl get deployment backend -n default > /dev/null 2>&1; then
                                        echo " Deployment 'backend' no existe. Aplicando manifiesto inicial..."
                                        kubectl apply -f k8s/backend-deployment.yaml
                                    else
                                        echo "Deployment 'backend' ya existe."
                                    fi
                                '''

                  
                                sh """
                                    kubectl set image deployment/backend backend=$ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG -n default
                                """
                                echo 'Imagen actualizada en el deployment de EKS.'
                            }
                        } catch (err) {
                            error "Fallo al desplegar en EKS: ${err}"
                        }
                    }
                }
            }
        }

        stage('Verificar deployments en EKS') {
            steps {
                echo 'Verificando estado de los deployments en EKS...'
                withEnv(["KUBECONFIG=${env.WORKSPACE}/.kube/config"]) {
                    sh 'kubectl get deployments -n default'
                }
            }
        }
    }

    post {
        failure {
            echo 'El pipeline falló en alguna etapa. Revisa los mensajes de error arriba.'
        }
        success {
            echo 'El pipeline finalizó correctamente y el backend fue desplegado en EKS.'
        }
    }
}