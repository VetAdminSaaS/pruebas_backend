FROM jenkins/inbound-agent:latest-jdk17

USER root

# Instalar herramientas necesarias
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    docker.io \
    maven \
    awscli \
    curl \
    unzip \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    bash && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Instalar kubectl (versión fija, por ejemplo v1.30.1)
RUN curl -LO https://dl.k8s.io/release/v1.30.1/bin/linux/amd64/kubectl && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

USER jenkins
