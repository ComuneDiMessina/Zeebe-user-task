# select parent image
FROM maven:3.6.3-openjdk-11-slim
COPY settings.xml /usr/share/maven/ref/

# copy the source tree and the pom.xml to our new container
COPY ./ ./

# package our application code
RUN mvn clean install
