# Travel map server

https://yaytsa.com/

## Prerequisites

* Install Java 17+ and make sure with `java -version`.
* Run a `postgresql` database and make sure you can connect to it.
* Create a database for the application (`travel_map_db` by default).
* Create the `local.properties` file in `src/main/resources`.
* Fill it according to the example below
  (or [src/main/resources/local.properties.example](src/main/resources/local.properties.example)):

```
SERVER_PORT=8080

JDBC_DATABASE_URL=xxx
DATABASE_USERNAME=xxx
DATABASE_PASSWORD=xxx

JWT_SECRET=xxx
JWT_EXPIRES=xxx

FRONTEND_URL=xxx
```

`JWT_SECRET` is compulsory, the rest have default values
in [src/main/resources/application.properties](src/main/resources/application.properties).

## Run

To build a fat jar and run it use:

```bash
./gradlew build
java -jar build/libs/travel-map-server-0.1.0.jar
```
