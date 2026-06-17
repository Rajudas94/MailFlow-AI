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


## Problem 3 — "Already Processed" confusion
What broke: Ingestion service kept saying "Email already processed" even after deleting database entries.
Why: Two things happening simultaneously that confused the situation. First — the delete wasn't actually clearing the table properly. Second — once emails were saved, the 60-second scheduler would run again and correctly skip already-saved emails. This is the duplicate prevention logic working as designed.
Fix: Properly cleared the table with DELETE FROM emails confirmed with SELECT COUNT(*). Then restarted the service fresh so it re-fetched from Gmail.
Lesson: "Already processed" is not a bug — it's the existsByGmailMessageId check protecting against duplicates. The scheduler runs every 60 seconds and will always skip emails it has already saved. This is correct behavior.

5/6/26
When running injestion service if the tokens get expired , delete the tokens folder from injestion service 


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
        


