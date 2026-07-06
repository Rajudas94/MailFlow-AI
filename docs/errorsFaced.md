# Problems Faced & How I Resolved Them

## Problem 1 — Bean Name Conflict
**Error:** BeanDefinitionOverrideException — cannot register bean 'gmailService' already exists
**Cause:** GmailConfig had a @Bean method named gmailService() and GmailService class was also named gmailService by Spring automatically. Two beans, same name.
**Fix:** Renamed the @Bean method from gmailService() to gmailClient() to avoid the conflict.
**Lesson:** Spring names beans automatically from class/method names. Always be aware of what name Spring assigns.

---

## Problem 2 — Missing Property
**Error:** PlaceholderResolutionException — could not resolve placeholder 'kafka.topic.email-events'
**Cause:** EmailEventProducer had @Value("${kafka.topic.email-events}") but that key was missing from application.properties.
**Fix:** Added kafka.topic.email-events=email-events to application.properties.
**Lesson:** Every @Value annotation needs a matching key in application.properties. Missing keys fail at startup not runtime.

---

## Problem 3 — Gmail OAuth Access Blocked
**Error:** Error 403 access_denied — app has not completed Google verification
**Cause:** Google Cloud app was in testing mode. Only approved test users can authenticate. My Gmail wasn't on the approved list.
**Fix:** Added rajud878@gmail.com to test users in Google Cloud Console → Audience → Test users.
**Lesson:** Google OAuth apps in testing mode require explicit test user approval. Add your own email before running OAuth flow.

---

## Debugging Rule Learned
Always read the LAST line of the stack trace first — that's where the actual root cause lives, not the top.

## Problem 1 — PropertyReferenceException
What broke: Classification service crashed on startup with "No property 'email' found for type 'Classification'"
Why: Spring JPA generates SQL queries by reading method names literally. I wrote existsByEmail() but the entity field was named emailId not email.
Fix: Renamed to existsByEmailId() to exactly match the entity field name.
Lesson: Spring JPA method names must exactly match entity field names. existsByEmailId → WHERE email_id = ?. One character wrong and it fails.

## Problem 2 — ClassNotFoundException
What broke: Classification service threw Class not found [com.mailflowai.ingestion.dto.EmailEvent]
Why: When Ingestion Service sends a message to Kafka it stamps a type header on it saying "this JSON belongs to com.mailflowai.ingestion.dto.EmailEvent". Classification Service tried to find that exact class — but it doesn't exist there. Classification Service has its own EmailEvent at com.mailflowai.classification.dto.EmailEvent.

Fix: Added these to application.properties:
spring.kafka.consumer.properties.spring.json.use.type.headers=false
spring.kafka.consumer.properties.spring.json.value.default.type=com.mailflowai.classification.dto.EmailEvent

And in KafkaConsumerConfig.java:
config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.mailflowai.classification.dto.EmailEvent");

This tells the deserializer — ignore whatever class name the message claims to be. Just deserialize the JSON into my own EmailEvent class.
Lesson: When services share DTOs through Kafka, each service serializes with its own package path. The receiving service must either use a shared module or ignore type headers and deserialize into its own class.

---------------------------------------------------------------

## Problem 3 — "Already Processed" confusion
What broke: Ingestion service kept saying "Email already processed" even after deleting database entries.
Why: Two things happening simultaneously that confused the situation. First — the delete wasn't actually clearing the table properly. Second — once emails were saved, the 60-second scheduler would run again and correctly skip already-saved emails. This is the duplicate prevention logic working as designed.
Fix: Properly cleared the table with DELETE FROM emails confirmed with SELECT COUNT(*). Then restarted the service fresh so it re-fetched from Gmail.
Lesson: "Already processed" is not a bug — it's the existsByGmailMessageId check protecting against duplicates. The scheduler runs every 60 seconds and will always skip emails it has already saved. This is correct behavior.

---------------------------------------------------------------

5/6/26
When running injestion service if the tokens get expired , delete the tokens folder from injestion service 

--------------------------------------------------------------

17/6/26
Kafka Consumer error. In the Kafka consumer config file, 
the consumer was trying to deserialize EmailEvent, but that's not 
the event it was supposed to listen to — it was supposed to listen 
to ClassificationEvent from Classification Service. So I changed 
the consumer to listen to ClassificationEvent.

Then the error still showed up — why? Because in the Kafka config 
of Routing Service, it was trying to deserialize the event using 
the package path declared in Classification Service's DTO. Changed 
it to Routing Service's own DTO package, and updated 
application.properties as well.

Problem: ClassNotFoundException — com.mailflowai.classification.dto.ClassificationEvent
Cause: Copy-pasted KafkaConsumerConfig from Classification Service. 
       Fixed the class name (EmailEvent → ClassificationEvent) but 
       forgot the package path was still "classification" instead 
       of "routing" — the service's own package.
Fix: Changed VALUE_DEFAULT_TYPE to com.mailflowai.routing.dto.ClassificationEvent
Lesson: When copy-pasting config between services, check BOTH the 
        package path AND the class name. Easy to fix one and miss 
        the other since they look similar at a glance.

17/6/26

---------------------------------------------------------------

18/6/26
Missing @RequiredArgsConstructor in Routing Service File

Error Faced:
NullPointerException: Cannot invoke 
"RoutingRepository.existsByEmailId(UUID)" because 
"this.routingRepository" is null

Why It Happened:
RoutingService had fields like routingRepository but no 
constructor was defined anywhere in the class, since I forgot 
@RequiredArgsConstructor. Without it, Java only has the default 
empty constructor. Spring used that empty constructor to create 
the object, so routingRepository was never set — it stayed null.

How I Solved It:
Added @RequiredArgsConstructor on the RoutingService class. 
Lombok then generated a constructor that takes routingRepository, 
queueRepository, and routingEventProducer as parameters. Spring 
saw this constructor, found the matching beans already created 
in its container, and injected them automatically.

Lesson Learned:
A class with final fields needs a constructor that accepts those 
fields — otherwise dependency injection has nothing to inject 
into. @RequiredArgsConstructor is not optional decoration, it's 
what makes constructor injection actually work. Always check that 
every @Service or @Component class that uses final fields also 
has either @RequiredArgsConstructor or a manually written 
constructor. Missing it causes a silent NullPointerException 
that only shows up at runtime, not at compile time — which makes 
it sneaky to catch early.

--------------------------------------------------------------

20/6/26
Routing Service Saving Nothing to Database — Fields Not Final

Error Faced:
RoutingService logs showed "Received classified email event" 
and "Routing Email" printing correctly, but nothing ever got 
saved to the routings table. No exception was visible in the 
normal logs.

Why It Happened:
Two mistakes stacked on top of each other. First, I had completely 
forgotten to add @RequiredArgsConstructor on the RoutingService 
class. Second, even after adding it, I had declared 
routingRepository, queueRepository, and routingEventProducer as 
just "private" instead of "private final". 

@RequiredArgsConstructor only generates constructor parameters 
for fields marked final. Since none of my fields were final, 
Lombok generated an empty constructor with nothing in it. Spring 
used that empty constructor to create RoutingService, so all 
three fields stayed null. Every time routeEmail() ran, it crashed 
internally trying to call methods on a null repository, but the 
crash was hidden inside Kafka's retry and error handling, so it 
never showed up unless I went searching deep in the logs for 
"Caused by".

How I Solved It:
Added @RequiredArgsConstructor to the class, then changed all 
three fields from "private" to "private final". Did a clean 
rebuild with "./mvnw clean spring-boot:run" to make sure no old 
compiled version was still running.

Lesson Learned:
@RequiredArgsConstructor is only half the fix — the fields it 
injects into must also be declared final. Missing either one 
causes the exact same silent null dependency bug. Always check 
both: the annotation is present AND every field meant for 
injection is final. Compile-time success does not mean 
runtime success — null pointer bugs from missing dependency 
injection only show up when the code actually runs.

----------------------------------------------------------------

20/6/26 — Full pipeline confirmed working end to end. 
Gmail → Ingestion → Kafka → Classification (Ollama) → Kafka 
→ Routing → PostgreSQL routings table populated with 7 real rows.

26/6/26 : Full pipeline confirmed working end to end after adding response service
Gmail ➤ Ingestion Service ➤ Kakfa ➤ Classification Service (with Ollama) ➤ Kafka ➤ Routing Service ➤ kafka ➤ Response Service 
(Each service saves the records of their incoming events to PostgreSQL) 

30/6/26
Full pipeline confirmed working end to end after adding response service
Gmail ➤ Ingestion Service ➤ Kakfa ➤ Classification Service (with Ollama) ➤ Kafka ➤ Routing Service ➤ kafka ➤ Response Service ➤ Kafka ➤ Notification Service
(Each service saves the records of their incoming events to PostgreSQL) 


Noticed: For above three pipeline tests, Ollama occasionally fails with 500 error when system RAM is low (needs 2.3GB, only 1.2GB free). Falls back to OTHER category correctly instead of crashing — error handling worked as designed.

----------------------------------------------------------------

Docker errors


4/7/26
# PostgreSQL Container Failed to Start (Port 5432 Already in Use)

## What Happened
While starting the MailFlow project using:

```bash
docker compose up
```

Docker failed to start the PostgreSQL container.

---

## Error Message

```text
ERROR: failed to bind host port 0.0.0.0:5432/tcp:
address already in use
```

---

## Why It Happened

The PostgreSQL container was configured to expose port **5432** on the host machine.

However, another PostgreSQL instance was already running locally and listening on the same port.

Since only one process can bind to a host port at a time, Docker could not start the PostgreSQL container.

---

## Investigation

- Verified the Docker Compose logs.
- Confirmed the failure occurred while binding host port **5432**.
- Identified that a local PostgreSQL service was already running and occupying the port.

---

## Action Taken

1. Stopped the local PostgreSQL service.
2. Restarted Docker Compose.

```bash
sudo service postgresql stop

docker compose up
```

---

## Result

- ✅ PostgreSQL container started successfully.
- ✅ Database initialized correctly.
- ✅ Spring Boot services connected to the database.
- ✅ Hibernate created the required tables.
- ✅ MailFlow services continued startup successfully.

---

## Key Learning

Docker port mapping follows the format:

```
HOST_PORT:CONTAINER_PORT
```

If the **host port** is already occupied by another application, Docker cannot bind the container to that port. Before exposing a port, always ensure it is available or use a different host port.

---

5/7/26

errors faced :
Couldn't resolve server kafka:9092 from bootstrap.servers as DNS resolution failed for kafka
No resolvable bootstrap urls given in bootstrap.servers

mailflow-kafka exited with code 1
advertised.listeners cannot use the nonroutable meta-address 0.0.0.0

why it happened


How i solved


result


