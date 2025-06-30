# Dockerfile para agente de pruebas backend con Docker CLI
FROM jenkins/inbound-agent:latest

USER root

# Instalar JDK 17, Maven, Git y Docker CLI
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven git docker.io && \
    apt-get clean

# Definir variables de entorno
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Verificar instalaciones
RUN java -version && mvn -version && docker --version

USER jenkins
