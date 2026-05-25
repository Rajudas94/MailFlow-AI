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
