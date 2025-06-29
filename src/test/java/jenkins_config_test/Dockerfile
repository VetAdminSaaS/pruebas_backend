# Dockerfile para agente de pruebas backend
FROM jenkins/inbound-agent:latest

USER root

# Instalar JDK 17 y Maven
RUN apt-get update && apt-get install -y openjdk-17-jdk maven git && \
    apt-get clean

# Definir variables de entorno
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Verificar versiones
RUN java -version && mvn -version

USER jenkins
