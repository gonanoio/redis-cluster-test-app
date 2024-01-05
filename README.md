# Redis Cluster and Spring Boot local development

Experimenting with Redis Cluster and Read Replicas as well as integration test setup and execution using redis cluster and read replicas.

*java 17 required* (sdk man)

`sdk use java 17.0.7-tem`

## Integration Tests 
The integration tests use [TestContainers](https://testcontainers.com/) to create a redis cluster and start the spring application. Tests can be invoked in two ways:

1. `mvn verify`
2. From IntelliJ and within the Test Class or Test Method: `Right-Click -> Run` or `Right Click -> Debug`

## Running Locally
When running locally the intent was to create a redis cluster with a specified number of shards and a specified number of read replicas and also create an instance of redis-commander to assist with node inspections.

Setting the spring profile to "docker" also executes `docker-compose` against the included docker-compose.yaml file.

* `mvn spring-boot:run` or `mvn spring-boot:run -Dspring-boot.run.profiles=docker` to launch redis cluster and redis commander via docker-compose automatically
* Redis Commander can be accessed at: http://localhost:8081/ 
* Swagger Doc can be used to read and write data and can be accessed at: http://localhost:8080/swagger-ui/index.html

## Inducing Primary Node Failure
To force master fail over to a replica execute the following command:
```shell
docker exec -it redis-cluster bash -c "mv /redis-conf/7000/redis.conf /redis-conf/7000/redis.conf.tmp && redis-cli -h 127.0.0.1 -p 7000 shutdown NOSAVE"
```

This does the following:
* Renames the redis configuration file for primary node running on port 7000
* Executes `shutdown NOSAVE` against the same server. 

It's necessary to rename the config file so that supervisor is not able to restart the node before fail over occurs.


