# betSync

A Spring Boot service that:
- exposes an HTTP API to publish **Event Outcome** messages,
- consumes those events from Kafka to publish eligible bets for settlement,
- publishes/consumes settlement messages via RocketMQ,
- persists bet state in an in-memory H2 database (for local/demo).

---

## Prerequisites

- Java 21
- Docker + Docker Compose
- Gradle

---

## Build the application Docker image (Jib)

From the project root:
```
gradle jibDockerBuild
```

This builds a local Docker image using Jib (no Dockerfile needed).

---

## Start the infrastructure + application

Start the full stack:
```
docker-compose up
``` 

To stop everything:
```
docker-compose down
``` 

---

## Use the API (Swagger UI)

Open Swagger UI:

- http://localhost:8080/api/swagger-ui.html

Trigger the flow by calling the **events/outcome** endpoint and submit this example request body:
```
json { "eventId": "E001", "eventName": "E001", "winnerId": "W001" }
```

Expected behavior:
1. The API publishes an event-outcome message.
2. The Kafka consumer processes the message and publishes eligible bets for settlement.
3. Bet settlement processing updates bet status in the database.

---

## Verify results in H2 Console

Open H2 Console:

- http://localhost:8080/api/h2-console/

Login details:
- **JDBC URL**: `jdbc:h2:mem:betsyncdb`
- Username: `sa`
- Password: (leave empty)

You can inspect the `bet` table and verify that bet statuses change as settlement messages are processed.

---

## Notes / Operational behavior (local)

- The app runs with an in-memory H2 database for simplicity.
- Kafka and RocketMQ are used for asynchronous processing.
- For local development, topics may be auto-created depending on the Kafka broker configuration.

---
