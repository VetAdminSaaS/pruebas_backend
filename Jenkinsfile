pipeline {
    agent { label 'backend' }

    environment {
        AWS_REGION   = 'us-east-1'
        ECR_REGISTRY = '478039852035.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO     = 'eccomerceveterinariasanfrancisco-backend'
        IMAGE_TAG    = "${GIT_COMMIT.take(7)}"
    }

    stages {
        stage('Verificar herramientas instaladas') {
            steps {
                echo 'Verificando herramientas necesarias...'
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
                echo 'Clonando repositorio...'
                checkout scm
            }
        }

        stage('Compilar JAR (mvn clean package)') {
            steps {
                echo 'Compilando el backend Java...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Login a AWS ECR') {
            steps {
                echo 'Autenticando con AWS ECR...'
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

        stage('Construir y subir imagen Docker') {
            steps {
                echo 'Construyendo y subiendo imagen Docker...'
                sh """
                    docker build -t $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG .
                    docker push $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    docker tag $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPO:latest
                    docker push $ECR_REGISTRY/$ECR_REPO:latest
                """
            }
        }

        stage('Desplegar en Amazon EKS') {
            steps {
                echo 'Desplegando backend en EKS...'
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
                            // Asegurar que el deployment exista
                            sh '''
                                if ! kubectl get deployment backend -n default > /dev/null 2>&1; then
                                    echo "Deployment 'backend' no existe. Creando..."
                                    kubectl apply -f k8s/backend-deployment.yaml
                                else
                                    echo "Deployment 'backend' ya existe."
                                fi
                            '''

                            // Eliminar pods en estado Terminating ANTES del rollout
                            sh '''
                                echo "Verificando pods en estado Terminating..."
                                for pod in $(kubectl get pods -n default --field-selector=status.phase=Running -o jsonpath="{.items[*].metadata.name}"); do
                                    if kubectl get pod $pod -n default -o json | grep -q '"deletionTimestamp"'; then
                                        echo "Eliminando pod atascado: $pod"
                                        kubectl delete pod $pod -n default --grace-period=0 --force || true
                                    fi
                                done
                            '''

                            // Actualizar imagen
                            sh "kubectl set image deployment/backend backend=$ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG -n default"

                            // Esperar a que el rollout finalice correctamente
                            sh 'kubectl rollout status deployment/backend -n default'
                        }
                    }
                }
            }
        }

        stage('Verificar estado de pods backend') {
            steps {
                echo 'Verificando estado y logs de los pods del backend...'
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
                                echo " Logs del pod: ${pod}"
                                sh "kubectl logs ${pod} -n default || true"
                                echo " Describe del pod: ${pod}"
                                sh "kubectl describe pod ${pod} -n default || true"
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            echo ' El pipeline falló. Revisa los logs para más detalles.'
        }
        success {
            echo ' Despliegue exitoso. Backend actualizado en EKS.'
        }
    }
}