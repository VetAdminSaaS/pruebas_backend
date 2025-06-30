FROM jenkins/inbound-agent:latest-jdk17

USER root

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    maven \
    awscli \
    docker.io \
    curl \
    unzip \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    bash \
    && apt-get clean

# Instalar kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

USER jenkins
