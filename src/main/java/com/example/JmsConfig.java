package com.example;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.apache.qpid.jms.policy.JmsPrefetchPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import java.io.UnsupportedEncodingException;

@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
public class JmsConfig {

    private ServiceBusProperties props;

    public JmsConfig(ServiceBusProperties props) {
        this.props = props;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory(JmsPrefetchPolicy policy) throws UnsupportedEncodingException {
        JmsConnectionFactory connectionFactory = new JmsConnectionFactory(props.getUrlString());
        connectionFactory.setClientID(props.getClientId());
        connectionFactory.setUsername(props.getSharedAccessPolicyName());
        connectionFactory.setPassword(props.getSharedAccessPolicyKey());
        connectionFactory.setReceiveLocalOnly(true);
        connectionFactory.setPrefetchPolicy(policy);
        return new CachingConnectionFactory(connectionFactory);
    }

    @Bean
    public JmsListenerContainerFactory topicContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setSubscriptionDurable(true);
        factory.setPubSubDomain(true);
        factory.setMessageConverter(messageConverter());
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsDefaultPrefetchPolicy prefetchPolicy() {
        JmsDefaultPrefetchPolicy policy = new JmsDefaultPrefetchPolicy();
        policy.setAll(1);
        return policy;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
