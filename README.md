######## Problem statement ######

Your assignment is to build a service capable of collecting data from hundreds
of thousands of sensors and alert if the CO2 concentrations reach critical levels.
Acceptance criteria
• The service should be able to receive measurements from each sensor at
the rate of 1 per minute
• If the CO2 level exceeds 2000 ppm the sensor status should be set to WARN
• If the service receives 3 or more consecutive measurements higher than
2000 the sensor status should be set to ALERT
• When the sensor reaches to status ALERT an alert should be stored
• When the sensor reaches to status ALERT it stays in this state until it receives 3
consecutive measurements lower than 2000; then it moves to OK
• The service should provide the following metrics about each sensor:
- Average CO2 level for the last 30 days
- Maximum CO2 Level in the last 30 days
  • It is possible to list all the alerts for a given sensor

#### Deliverable ####
The final deliverable is a link to a project on github that meets the following
criteria:
• The service implements the previously documented API
• Meets the previously defined acceptance criteria
• Is easy to read and maintain
• Can be easily built and ran on Linux or OSX
• Is written in a language that runs on the JVM
• Is tested using appropriate testing mechanisms
• Has appropriate documentation
• The code, documentation, or the repository should not include any reference to movingimage
• Don’t include this document in the deliverable

###### Specifications #####

# API 1 : Collect measurements
--------------------------------------------------------------------------------------------------------------------
Request -: /api/v1/sensors/{uuid}/measurements
Request -: 
"co2" : 2000,
"time" : "2019-02-01T18:55:47+00:00"
}

# API 2 : Sensor Status
---------------------------------------------------------------------------------------------------------------------
GET /api/v1/sensors/{uuid}
Response:
{
"status" : "OK" // Possible status OK,WARN,ALERT
}

# API 3 -: Sensor Metrics
---------------------------------------------------------------------------------------------------------------------
GET /api/v1/sensors/{uuid}/metrics
Response:
{
"maxLast30Days" : 1200,
"avgLast30Days" : 900
}


# API 3 -: Listing all alerts

GET /api/v1/sensors/{uuid}/alerts
Response:
[
{
"startTime" : "2019-02-02T18:55:47+00:00",
"endTime" : "2019-02-02T20:00:47+00:00",
"mesurement1" : 2100,
"mesurement2" : 2200,
"mesurement3" : 2100
}
]

######## SOLUTION AND IMPLEMENTATION #############

# Stack #
* Java
* Spring Boot , Spring MVC
* Maven
* MongoDb
* Docker

#### MongoDb Database setup ####
1. Setup locally
   You need to install mongo locally (Mac OSX)
   https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/

Run the following command to start mongo
brew services start mongodb-community@5.0

2. Run as docker (your docker service needs to be active and running)
* Pull docker image
  docker pull mongo

* Create a /mongodata directory on the host system:
  sudo mkdir -p /mongodata

* Start the Docker container with the run command using the mongo image. The /data/db directory in the container is mounted as /mongodata on the host. Additionally, this command changes the name of the container to mongodb:
  sudo docker run -it -v mongodata:/data/db -p 27017:27017 --name mongodb -d mongo

# Configuration #
There's an application.properties file under resources/ path which contains configuration values of mongoDB for local setup
There are json files in test/resources which is used to load test data for unit tests

# Build and Run #
* You can either run it directly by executing this command from project's root:
  mvn spring-boot:run

* As a result the web application will be ready to accept requests at http://localhost:8080/ , so you can test the service endpoints for example at: http://localhost:8080/transactions or you can package the project as JAR and deploy it on a standard Java Servlet Container like Tomcat. Here is the Maven command to build the package:
  mvn clean package

* After a successful build, project is being packaged as alfred-1.0.jar under target folder

* To run as docker container {Either running individual containers or using docker compose)
  docker network create -d bridge backend (create network to link app and mongo)
  mvn clean install
  docker image build -t alfred . //create app image
  docker run -d -p 27017:27017 --name mongo --net backend mongo:latest  //run mongo container
  docker run --rm -p 8080:8080 --name flash --net backend --link mongo:mongo flash:latest //run app image

# For docker-compose we need to have the application image already created before running the Docker Compose file.
docker network create -d bridge backend (create network to link app and mongo)
mvn clean install
docker image build -t flash . //create app image
docker-compose up

##### P.S.: Docker approach is not working right now, facing errors of not finding main manifest attribute in target jar

# Tests #
* You can either run unit tests and build together using
  mvn clean install

or only run tests using
mvn test

* If for any reason you would decide to ignore unit tests during the build process, Maven's -skipTests can be used. For example:
  mvn clean package -skipTests