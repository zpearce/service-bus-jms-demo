package com.example;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MaxDeliveryTests extends ServiceBusJmsDemoApplicationTests {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TestListenerManualReject listener;

    @Test
    public void itDoesNotHonorMaxDeliveryWhenManuallyRejecting() throws InterruptedException {
        int expectedDeliveredCount = 10;

        Foo foo = new Foo("baz");
        jmsTemplate.convertAndSend("test-topic", foo);

        listener.getLatch().await(20, TimeUnit.SECONDS);

        int actualDeliveredCount = listener.getDeliveredCount();

        assertEquals(expectedDeliveredCount, actualDeliveredCount);
    }
}
