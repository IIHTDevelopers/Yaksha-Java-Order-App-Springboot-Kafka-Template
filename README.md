### Order Application

- Provides order operation like creation/cancellation using Apache kafka.

### Order Application Producer Module

- ** Create Order using api endpoint /api/orders/ and publish order event message to kafka using topic create-order.

### Order Application Consumer Module

- ** OrderEventsConsumerManualOffset Listening order event and store details in database.
- Incase of any failure happen in OrderEventsConsumerManualOffset then New message has been published to
  retry-create-order topic and store failure record in database

### Order Application Retry Module

- ** OrderEventsRetryConsumer is Responsible to listen failed consumed message.
- A scheduler RetryScheduler is running wit 5 sec internal to fetch all failure record from database and publish again
  on kafka

---
