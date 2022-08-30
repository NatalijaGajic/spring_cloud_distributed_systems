# spring_cloud_distributed_systems

spring_cloud_distributed_systems is a information system developed using `Spring Cloud`. It represents a set of cooperating microservices with minimalistic functionality. Essentially, it provides a RESTful API exposed by a composite microservice. The composite microservice calls four other microservices to create an aggregated response. The following is a diagram of the microservices landscape.

![](diagrams/microservices-landscape.png "Microservices landscape diagram")

Microservice-based system landscape consists of four core microservices, the `course`, `lecture`, `author` and `rating` services, all of which deal with one type of resource, and a composite microservice called `course-composite` service, which aggregates information from the core services. 
 
 The following is a class diagram of DTO classes.
 
 ![](diagrams/class-diagram.png "Class diagram")
 
 With `Spring Cloud`, the following microservices design patterns are implemented:
 
 | No. | Design Pattern | Implementation  |
| -------- | ------------------------------------ | ----------------------------------- |
| 1 | [Service discovery](#service-discovery) | Netflix Eureka |
| 2 | [Edge server](#edge-server) | Spring Cloud Gateway |
| 3 | [Reactive microservices](#reactive-microservices) | Spring Web Flux framework, RabbitMQ, Kafka |
| 4 | [Central configuration](#central-configuration) | Spring Cloud Configuration Server |
| 5 | [Distributed tracing](#distributed-tracing) | Spring Cloud Sleuth, Zipkin | 
| 6 | [Circuit breaker](#circuit-breaker) | Resilience4j   | 

The following is a diagram of the system landscape.

 ![](diagrams/system-landscape.png "System landscape diagram")

### Service Discovery
Considering the DNS-based service discovery is not well-suited for handling volatile microservices instances, `Netflix Eureka` is used to provide a robust, resilient and fault-tolerant solution. 
The process is as follows:
- Whenever a microservice instance starts up it registers itself to one of the Eureka servers.
- On a regular basis, each microservice instance sends a heartbeat message to the Eureka server, telling that the microservice instance is okay and is ready to receive requests.
- Client library is used in `course-composite` service to regularly ask the Eureka server for information about available services. To be able to look up available microservices instances through the Eureka server in the `course-composite` microservice, a load balancer-aware `WebClient` builder is added.
- Available instances are chosen in a round-robin fashion.

### Edge Server
Edge server is set up based on `Spring Cloud Gateway`. Edge server behaves like a reverse proxy. It hides services that shoud not be exposed outside their context and uses the standard OAuth protocol to protect services from malicious requests. External clients send all their requests to the edge server and edge server routes them based on the URL path and specified routing rules. 

### Reactive Microservices
Synchronous non-blocking microservice communication is provided for READ operation of the core services, while asynchrounous communication takes place during the CREATE and DELETE operations. Non-blocking I/O are used to ensure that no threads are allocated while waiting for processing to occur in another service, that is, a database or another service. 
Implemented solution is as follows:
- Asyncronous model is used to send messages without waiting for the reciever to process them.
- Reactive frameworks are used to execute synchronous requests using non-blocking I/O, that is, without allocating a thread while waiting for a response. 
- Additionally, circuit breaker is implemented and response is produces even if a service that the microservice depends on fails.

##### Non-blocking Synchronous APIs
Non-blocking synchronous APIs are developed using `Spring Web Flux` and `Spring Web Client`. Using `Spring Data`’s reactive support for `MongoDB`, databases are accessed in a non-blocking way. When using `Spring Data JPA` the processing of the blocking code is encapsulated by schedulling the processing in a dedicated thread pool. The composite service makes reactive non-blocking calls in parallel and makes a composite response when it collects data from all services.

##### Event-driven Asyncronous Services
Event-driven asyncronous services are developped using `Spring Cloud Stream` and `RabbitMQ` and `Kafka` as messaging systems. `Spring Cloud Stream` is based on the publishing and subscribe pattern, where a publisher publishes messages to topics and subscribers subscribe to topics they are interested in to recieve messages. The composite service publishes create and delete events on each core service topic and then returns an OK response back to the caller without waiting for the processing to take place in the core services.

### Central Configuration
`Spring Cloud Configuration Server` is used to centralize managing the configuration of microservices. The microservices are configured to retrieve their configuration from the config server at startup and to handle temporary outages while retrieving it.
Configuration server protects configuration information by requiring authenticated usage with basic HTTP authentication. Eavesdropping is prevented by exposing its API externally through the edge server that uses HTTPS. Server also protects data at rest by providing `/encrypt` endpoint to encrypt the information that is being stored. 

### Distributed Tracing
Microservices are configured to send trace information to the `Zipkin` server asynchronously while using message brokers. `Spring Cloud Sleuth` is used to collect trace information which is stored in memory. 

### Circuit Breaker
`Resilience4j` is used to implement circuit breaker and retry mechanism. These mechanisms are applied in calls to the `course` service from the `course-composite` service. These mechanisms are configured so that the circuit breaker doesn't open the circuit before the intended number of retries have been completed.

A circuit breaker can prevent a microservice from being unresponsive if the synchronous services it depends on stop responding normally, by using fast fail and fallback methods when the circuit is open. It can also make a microservice resilient by allowing requests when it is half-open to see weather the failing service operated normally again and close the circuit if so. 
The solution is as follows:
1.	Open the circuit and fast fail if problems with the service are detected.
2.	Probe for failure correction.
3.	Close the circuit if the probe detects that the service operates normally again.

The retry mechanism is useful for random and infrequent faults, such as temporary network glitches. It simply retries a failed request, which must be idempotent, a number of times with a configurable delay between the attempts. 

### Persistence
`Spring Data` is used to add a persistence layer to the core microservices. A Java bean mapping tool, `MapStruct`, is used to transform between `Spring Data` entity objects and the API model classes. The `version` field is used to implement optimistic locking, that is, to allow `Spring Data` to verify that updates of an entity in the database do not overwrite a concurrent update. 
Each microservice has its own database, `course`, `lecture`, and `author` are persisting data in a `MongoDB` NoSQL database, while `rating` microservice depends on `MySQL` relational database.

### Security
`Spring Security` is used to secure the system landscape.
- HTTPS is used for external communication, while plain text HTTP is used inside the system landscape.
- The local `Oauth 2.0` authorization server is accessed externally through the edge server.
- Both the edge server and the composite microservice validate access tokens as signed `JWT` tokens.
- The edge server and the product composite microservice get the authorization server’s public keys from `jwk-set` endpoint, and use them to validate the signature of the JWT-based access tokens.
- HTTP basic authentication is used to restrict access to the discovery service and configuration server.

### Docker
`Docker Compose` is used for defining and running multi-container Docker applications. With Compose, a YAML file, `docker-compose.yml`, is used to configure services which are then easily created and started by Docker Daemon. 
### Software and Hardware List
| No. | Software required | OS required |
| -------- | ------------------------------------ | ----------------------------------- |
| 1 | Spring | Windows, macOS, Linux |
| 2 | Docker CE | Windows, macOS, Linux, see [supported platforms](https://docs.docker.com/engine/install/#supported-platforms) |

### Pipeline
To start the landscape run the following:
```sh
gradlew build && docker-compose build && docker-compose up
```
To test the running services execute script:
```bash
./test-all.bash
```
