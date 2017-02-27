## Service Bus Jms Demo

This Spring app demonstrates an issue between the Apache Qpid java client library
and Azure Service Bus, namely that a message is redelivered many (potentially thousands of)
times to a Qpid client without Service Bus reliably incrementing the message's delivery count.
The message never hits the subscription's configured max delivery count and is therefore
never moved to the dead letter queue.

### Running the Tests

First, set up a topic and subscription in Service Bus with the following settings:

* Topic name: test-topic; Accept all the default settings
* Subscription Name: test-subscription; Accept all the default settings, but verify Max Delivery Count is set to 10

Next, in the root folder of the project issue the command

```
$> cp src/main/resources/application-default.yml.example src/main/resources/application-default.yml
```

Set up a SAS policy and key in your Service Bus namespace and record the host,
policy name, and key in your application-default.yml file.

Finally, run the tests:

```
$> ./gradlew clean build --info
```

### What the Tests Show

The test sets up a JMS listener that counts the number of times it receives a message
from Service Bus, failing when it passes 10 deliveries (the subscription's max delivery count).

The listener has to manually set the `JMS_AMQP_ACK_TYPE` to `RELEASED` in order to duplicate
Service Bus's documented PeekLock semantics which state that while being processed by the client, the message
is locked (for a certain period) until it is either accepted, rejected, or released.

The app is configured to log tracing information from Qpid, showing the protocol
level interactions between client and broker. There is very little [information from
Microsoft](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-amqp-protocol-guide)
on how Service Bus implements AMQP 1.0.

The logs show that, after message reception a `Disposition` performative is sent back to the
broker with state=Released{}, which we expect to trigger the increment of the message's delivery
count on the broker side. Weirdly, it only appears to increment once every time the test is run
(i.e. when the runtime is shut down) even though at the protocol level there is one Transfer
event every time a message is received.