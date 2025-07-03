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
        stage('Verificar herramientas instaladas') {
            steps {
                echo 'Verificando si aws, docker y kubectl están disponibles...'
                sh '''
                    which aws || echo "aws no está instalado"
                    which docker || echo "docker no está instalado"
                    which kubectl || echo "kubectl no está instalado"
                    aws --version || true
                    docker --version || true
                    kubectl version --client || true
                '''
            }
        }

        stage('Checkout del código fuente') {
            steps {
                echo 'Obteniendo el código desde Git...'
                checkout scm
            }
        }

        stage('Compilar JAR (mvn clean package)') {
            steps {
                echo 'Compilando el proyecto Java...'
                sh 'mvn clean package -DskipTests'
                echo 'JAR compilado exitosamente.'
            }
        }

        stage('Login a AWS ECR') {
            steps {
                echo 'Autenticando en AWS ECR...'
                withCredentials([[ 
                    $class: 'AmazonWebServicesCredentialsBinding', 
                    credentialsId: 'SanFranciscoAWS' 
                ]]) {
                    sh '''
                        aws ecr get-login-password --region $AWS_REGION | \
                        docker login --username AWS --password-stdin $ECR_REGISTRY
                    '''
                    echo 'Login exitoso en ECR.'
                }
            }
        }

        stage('Construir y subir imagen Docker') {
            steps {
                echo 'Construyendo imagen Docker...'
                sh """
                    docker build -t $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG .
                    docker push $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    docker tag $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPO:latest
                    docker push $ECR_REGISTRY/$ECR_REPO:latest
                """
                echo "Imagen Docker publicada con tag: $IMAGE_TAG"
            }
        }

        stage('Desplegar en Amazon EKS') {
            steps {
                echo 'Iniciando despliegue a EKS...'
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
                        echo 'Configuración de acceso a EKS generada correctamente.'

                        withEnv(["KUBECONFIG=${kubeconfigPath}"]) {
                            sh '''
                                if ! kubectl get deployment backend -n default > /dev/null 2>&1; then
                                    echo "Deployment 'backend' no existe. Aplicando manifiesto inicial..."
                                    kubectl apply -f k8s/backend-deployment.yaml
                                else
                                    echo "Deployment 'backend' ya existe."
                                fi
                            '''

                            sh "kubectl set image deployment/backend backend=$ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG -n default"
                            echo 'Imagen actualizada en el deployment de EKS.'

                            sh 'kubectl rollout status deployment/backend -n default'

                            // Eliminar pods en estado Terminating si existen
                            sh '''
                                TERMINATING=$(kubectl get pods -n default | grep Terminating | awk '{print $1}')
                                if [ ! -z "$TERMINATING" ]; then
                                    echo "Forzando eliminación de pods en estado Terminating..."
                                    for pod in $TERMINATING; do
                                        kubectl delete pod $pod --grace-period=0 --force -n default
                                    done
                                else
                                    echo "No hay pods en estado Terminating."
                                fi
                            '''
                        }
                    }
                }
            }
        }

        stage('Verificar estado de pods backend') {
            steps {
                echo 'Verificando estado de los pods del backend...'
                withCredentials([[ 
                    $class: 'AmazonWebServicesCredentialsBinding', 
                    credentialsId: 'SanFranciscoAWS' 
                ]]) {
                    withEnv(["KUBECONFIG=${env.WORKSPACE}/.kube/config"]) {
                        script {
                            sh 'kubectl get pods -n default'

                            def pods = sh(
                                script: "kubectl get pods -l app=backend -n default -o jsonpath='{.items[*].metadata.name}'",
                                returnStdout: true
                            ).trim().split()

                            for (pod in pods) {
                                echo "Logs del pod ${pod}"
                                sh "kubectl logs ${pod} -n default || echo 'No se pudieron obtener logs'"

                                echo "Describe del pod ${pod}"
                                sh "kubectl describe pod ${pod} -n default || echo 'No se pudo describir el pod'"
                            }
                        }
                    }
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