## Build from source
Build with Maven
### Prepare next release and checkout the tag
    mvn release:clean release:prepare release:perform -DreleaseVersion=0.45.0 -DdevelopmentVersion=0.46.0-SNAPSHOT -Darguments="-Dmaven.deploy.skip=true"
    git checkout tags/v0.45.0          
### Compile and package code in an executable JAR
Activate one of the following profile to choose the datasource
* **Postgres** (default)
    `mvn clean package -Ppostgresql`
* **Mongodb**
   `mvn clean package -Pmongodb`
### Build docker image
See Dockerfile [here](zeebe-user-task-boot/Dockerfile)

Change directory to `zeebe-user-task-boot`

    /zeebe-user-task-boot# docker image build -t zeebe-usertask-be:0.41.0 .

### Tag docker image for Postgres
```
docker tag zeebe-usertask-be:0.33.0  docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.POSTGRES
```
### Tag docker image for mongoDB
```
docker tag zeebe-usertask-be:0.33.0  docker.eai.giottolabs.com/zeebe-usertask-be:0.33.0.MONGODB
```