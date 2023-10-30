Zeebe User Task
==========================

A [Zeebe](https://zeebe.io) worker to manage manual/user tasks in a workflow. It shows all jobs of type `user` as a task/todo-list. A user can complete the tasks with requested data. 

## Project Resources
* Home page: http://10.207.1.10/eai/zeebe/zeebe-user-task/wikis/home
* Compatibility Matrix: http://10.207.1.10/eai/zeebe/zeebe-user-task/wikis/Zeebe-Manager-Versions
* Wiki:  http://10.207.1.10/eai/zeebe/zeebe-user-task/wikis/home
* Issue Tracker: http://10.207.1.10/eai/zeebe/zeebe-user-task/issues 


## Building the distribution from source
1. Install Java SE Development Kit 1.8
2. Install Apache Maven 3.x.x(https://maven.apache.org/download.cgi#)
3. Get a clone from http://10.207.1.10/eai/zeebe/zeebe-user-task.git or download the source
4. Run `mvn clean package`
5. You can find the binary distribution in _zeebe-user-task-boot/target_ directory.


## Installation and Running
### Docker image
The docker image for the worker is published to [Nexus Giottolabs](https://repo.eai.giottolabs.com)

* Docker image for Postgres
```
docker pull docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.POSTGRES
docker run -d --name usertask -p 8080:8080 -p 9090:9090 docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.POSTGRES
```

* Docker image for MongoDB
```
docker pull docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.MONGODB
docker run -d --name usertask -p 8080:8080 docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.MONGODB
```

### Change Configuration Files
You can use docker volumes to link your own configuration files inside the container. For example if you want to change the **application.properties**:
```
docker run -d --name usertask -p 8080:8080 -p 9090:9090 \
           -v $PWD/application.properties:/app/config/application.properties \
           docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0
```
see [Dockerfile](zeebe-user-task-boot/Dockerfile) for more information

#### Zeebe configuration
* ensure that a Zeebe broker is running
* configure the connection to the Zeebe broker by setting `zeebe.client.broker.contactPoint` (default: `localhost:26500`) 

The worker is a Spring Boot application that uses the [Spring Zeebe Starter](https://github.com/zeebe-io/spring-zeebe). The configuration can be changed via environment variables or an `application.properties` file. See also the following resources:
* [Spring Zeebe Configuration](https://github.com/zeebe-io/spring-zeebe#configuring-zeebe-connection)
* [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)

```
zeebe.client.worker.defaultName: zeebe-user-task
zeebe.client.worker.defaultType: user
zeebe.client.worker.threads: 2
zeebe.client.job.timeout: 2592000000
zeebe.client.broker.contactPoint=localhost:26500
zeebe.client.security.plaintext: true
```
#### Postgres configuration
* choose your database and configure it using the following script [schema.sql](docs/schema.sql)
* edit the **application.properties**
```
spring.datasource.url=jdbc:postgresql://localhost:5432/usertask?currentSchema=usertask
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.generate-ddl=true
spring.datasource.platform=postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```
#### MongoDB Configuration

```
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=usertask
```

#### Profile Manager configuration
* ensure that Profile Manger is running
* configure the connection to the Profile Manager microservice by setting: `ljsa.profilemanager.contact-point` (default: `localhost:9090`) in **application.properties**
* init Profile Manager using the following script [data.sql](docs/data.sql)
* edit **application.properties**
```
ljsa.ssl-validation=false
ljsa.profilemanager.contact-point=localhost:9090
ljsa.profilemanager.enabled=true
ljsa.profilemanager.micro-service-id=e99cd85b-eaa3-4133-9c08-b49c369c7665
ljsa.profilemanager.security.plaintext=true
ljsa.oidc.issuer-key=https://is:443/oauth2/token
ljsa.oidc.roles-key=groups
ljsa.oidc.subject-key=sub
ljsa.oidc.url-jwk-provider=https://is/oauth2/jwks
ljsa.skip-urls=/usertask/swagger-ui.html**,/usertask/v2/api-docs,/usertask/webjars/**,/usertask/swagger-resources/**,/usertask/notifications/**
rolename.admin=UT_ADMIN
```
* **ljsa.profilemanager.contact-point** connection to Profile Manager microservice
* **ljsa.oidc.issuer-key** id_token issuer key
* **ljsa.oidc.roles-key** role claim key in the jwt payload
* **ljsa.oidc.subject-key** subject claim key in the jwt payload
* **ljsa.oidc.url-jwk-provider** jwks endpoint
* **ljsa.skip-urls** list of urls excluded by the profile manager



## API documentation
API documentation is defined according to the **OpenAPI specification** and it is available [here](wso2-ljsa-user-manager-api/swagger.json)

The `/v2/api-docs` URL is the default documentation for your API in the Swagger 2.0 standard