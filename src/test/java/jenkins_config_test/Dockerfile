# Imagen base oficial de Jenkins inbound agent
FROM jenkins/inbound-agent:latest

# Cambiar a root para instalar herramientas
USER root

# Instalar Java 17, Maven, Git y Docker CLI
RUN apt-get update && \
    apt-get install -y \
        openjdk-17-jdk \
        maven \
        git \
        docker.io && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configurar JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Verificar instalaciones
RUN java -version && \
    mvn -version && \
    git --version && \
    docker --version

# Volver al usuario jenkins (no root)
USER jenkins
