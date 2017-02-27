package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.CountDownLatch;

import static org.apache.qpid.jms.message.JmsMessageSupport.RELEASED;

@Component
public class TestListenerManualReject {
    private final Logger log = LoggerFactory.getLogger(TestListenerManualReject.class);
    private final CountDownLatch latch = new CountDownLatch(20);
    private int deliveredCount = 0;

    @JmsListener(destination = "test-topic/subscriptions/test-subscription", containerFactory = "topicContainerFactory")
    public void receive(Foo foo, Message message) throws JMSException {
        deliveredCount++;
        latch.countDown();

        log.info("Received a Foo");

        //The message will be acked later by spring jms
        message.setIntProperty("JMS_AMQP_ACK_TYPE", RELEASED);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public int getDeliveredCount() {
        return deliveredCount;
    }
}