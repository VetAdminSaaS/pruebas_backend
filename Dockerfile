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
    bash && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Instalar AWS CLI v2
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip aws

# Instalar kubectl (v1.30.1 como ejemplo)
RUN curl -LO https://dl.k8s.io/release/v1.30.1/bin/linux/amd64/kubectl && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

# Verificar que aws está disponible para el usuario jenkins
RUN echo 'export PATH=$PATH:/usr/local/bin' >> /etc/profile.d/aws.sh && chmod +x /etc/profile.d/aws.sh

USER jenkins