FROM jenkins/inbound-agent:latest-jdk17

USER root

# Instalar herramientas necesarias
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    docker.io \
    maven \
    curl \
    unzip \
    ca-certificates \
    bash \
    git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Instalar AWS CLI v2
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    ln -s /usr/local/bin/aws /usr/bin/aws && \
    rm -rf awscliv2.zip aws

# Verificar instalación AWS CLI
RUN aws --version

# Instalar kubectl (versión fija, puedes cambiar si es necesario)
ENV KUBECTL_VERSION="v1.30.1"
RUN curl -LO "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    ln -s /usr/local/bin/kubectl /usr/bin/kubectl && \
    rm kubectl

# Verificar instalación kubectl
RUN kubectl version --client

# Asegurar que /usr/local/bin esté en PATH
ENV PATH="/usr/local/bin:$PATH"

USER jenkins
