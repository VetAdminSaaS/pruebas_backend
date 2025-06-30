FROM maven:3.9.6-eclipse-temurin-17

USER root

# Instalar Docker CLI, Git (ya viene con Maven), y otros paquetes si necesitas
RUN apt-get update && \
    apt-get install -y docker.io git && \
    apt-get clean

# Verificar instalaciones
RUN java -version && mvn -version && docker --version
