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

* As a result the web application will be ready to accept requests at http://localhost:8080/ , so you can test the service endpoints for example at: you can package the project as JAR and deploy it on a standard Java Servlet Container like Tomcat. Here is the Maven command to build the package:
  mvn clean package

* After a successful build, project is being packaged as flash-1.0.jar under target folder

* To run as docker container {Either running individual containers or using docker compose)
  docker network create -d bridge backend (create network to link app and mongo)
  mvn clean install
  docker image build -t flash . //create app image
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
  
* or only run tests using
  mvn test

* If for any reason you would decide to ignore unit tests during the build process, Maven's -skipTests can be used. For example:
  mvn clean package -skipTests

### Scalability and Performance ###
1. To scale the GET for metrics, status and alerts and POST for measurements introduce streaming for measurements in async
  * Implement the consumption to reduce single event time so that messages get processed fastly. Event consumption involves 1 DB select call,
    2 DB update calls and compute new metrics and alerts. Here we can add caching to remove the first DB select call and add 2 cache update calls (batch update)
    Supporting the computation at measurement stream is done to support higher read availability and lower latency which leads to
    eventual consistency. We can create partitions based on sensorId which will help in aggregating similar sensors and have multiple consumers
    Choose partitions and can aggregate sensors over partitions such that we have balanced batches, sent to internal topic and then do batch reads (Applying one record at a time can have quite an impact on performance. Without batching, 
    there’s an overhead of time and network traffic for every single record, and Kafka has to work much harder as it performs many network round-trips to fetch data)

    Lets say 50K sensors each sending 1-2 measurements per day
    Rule of thumb - 10 partitions per topic and 10,000 partitions per cluster
    - Partitions = Desired Throughput / Partition Speed
      Conservatively, we can estimate that a single partition for a single Kafka topic runs at 10 MB/s.
      Let's say 1 message is 1 KB - 50 MB per sec - 50*60*60*24 = 4320000 MB per day - 4.3 TB per day
    - Partitions = 5
      The replication factor is set to 3 as a default. While partitions reflect horizontal scaling of unique information,
      replication factors refer to backups. For a replication factor of 3 in the example above, there are 15 partitions in total with 5 partitions
      being the originals and then 2 copies of each of those unique partitions(IMPORTANT FOR DATA LOSS)

  * We can scale kafka horizontally and vertically - add more brokers and resource size based on load testing

2. How would I change the system to support 100M users hitting each endpoint 10 times/day?
   100M * 10 in 24 hours ~ 11500 RPS - We can scale application level(implementation discussed above), horizontally(add more nodes) and vertically scaling(increasing machine size)
- Here the problem statement - availability and latency in GET API which is different for both APIs
  1. We can add a cache layer (redis) here to support fast retrievals and avoid DB calls everytime
     update cache async on post measurement consumption - recompute metrics and alerts data for each sensor on measurement stream and update DB and cache -> write through cache
     This will keep the cache refreshed at all times for popular instrument candlestick data
     Caching will help in supporting latency and reducing load on DB

  Second thing to support increasing user calls is to horizontally scale service instance(cloud EC2), cache and DB(MySQL) (INCREASE RESOURCES)
  * Increase no of instances running and add autoscaling of instances based on CPU, Memory utilisation and API throughput (this will add containers whenever gradually users increase)
  * Increase redis nodes and add replication or sharding for read or writes respectively. Same for MYSQL
    Sharding is useful to increase performance, reducing the hit and memory load on any one resource.
    Replication is useful for getting a high availability of reads. If you read from multiple replicas, we will also reduce the hit rate on all resources, but the memory requirement for all resources remains the same. It should be noted that, while we can write to a slave, replication is master->slave only. So we cannot scale writes this way.
  * SAME FOR MYSQL AS ABOVE
   
3. Our system can grow stale (the most recent measurement data we have is few minutes old)
- The main reason for above will be eventual consistency because of consuming messages in async and in times of load, message processing can take time leading to stale data
  This means messages are getting accumulated in kafka, so we can add alerts and monitors on visible messages in kafka, event processing time
  which can be used to autoscaling at spikes. This will help in reducing the errors
- Keep a timeout at event consumption and in case of timeout push the message to dead letter queue. At time of user call check from DB data
  and DLQ(if present in queue, consume at that time and refresh updated data). This will result in updated data for client at all times but will lead to increase in latency


### Future features ###
* Apply more validations and descriptive exceptions in pre-processing of input data and api request
* Add short-circuiting and timeout while reading from database
* Configure Rule Engine (Jeasy) to make rules configurable - yml based 
* Docker compose for service and MySQL