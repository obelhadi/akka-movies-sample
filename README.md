# Akka Movies Sample
This is a simple app to show reactive programming using Akka actor system.

It consists of 3 actors:
- _TitlesActor_: it downloads the IMDB TSV file and parses it line by line. for every line that corresponds to a movie title it will be sent to MoviesActor.
- _MoviesActor_ : receives movies title and once it found a Comedy movie it will be forwarded to _ComedyMoviesPublisherActor_.
- _ComedyMoviesPublisherActor_: it receives a comedy title and publishes it to RabbitMQ queue.

On can imagine as improvement to create a dynamic RabbitMQ publisher Actor that could be reused by other future Actor.

The separation between _TitlesActor_ and _MoviesActor_ is meant to be open to extension if we wanted for example to manage TV Shows in the future.

# How to use
Make sure you have JVM 1.8 and Maven installed.
## Install RabbitMQ server:
You can use official binaries or simply use Docker with this command:
```
docker run -d --hostname my-rabbit --name some-rabbit -p 4369:4369 -p 5671:5671 -p 5672:5672 -p 15672
:15672 rabbitmq:3-management
```
RabbitMQ will be run as daemon and you can check the management UI on url : [http://localhost:15672](http://localhost:15672) using guest/guest as default credentials. 
## Config app RabbitMQ properties
 If you are not using the default configuration used by RabbitMQ server, you can change the app configuration properties in the _/resource/application.conf_ in the **rmq** part.
## Run the app
 Run the maven command
 ```
 mvn clean package exec:exec
 ```
 Or simply execute the _Main.java_ from your IDE