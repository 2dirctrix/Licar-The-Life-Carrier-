FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && apt-get install -y ca-certificates curl

RUN install -m 0755 -d /etc/apt/keyrings

RUN curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc

# RUN add-apt-repository \
   # "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN echo \
   "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
   $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

RUN apt-get update && apt-get install -y docker-ce docker-ce-cli docker-compose-plugin gradle

RUN groupadd -f docker && usermod -aG docker jenkins

USER jenkins