package com.example.ibmmq.config;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.AcknowledgeMode;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Log4j2
@Configuration
public class JmsConfig {

    @Value("${ibmmq.channel}")
    private String channel;

    @Value("${ibmmq.orderQueue}")
    private String queueOrder;

    @Value("${ibmmq.host}")
    private String host;

    @Value("${ibmmq.port}")
    private Integer port;

    @Value("${ibmmq.queueManager}")
    private String queueManager;

    @Value("${ibmmq.user}")
    private String user;

    @Value("${ibmmq.password}")
    private String password;

    @Bean
    public MQQueueConnectionFactory mqQueueConnectionFactory() {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(host);
        try {
            mqQueueConnectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            mqQueueConnectionFactory.setCCSID(37);
            mqQueueConnectionFactory.setChannel(channel);
            mqQueueConnectionFactory.setPort(port);
            mqQueueConnectionFactory.setQueueManager(queueManager);
        } catch (Exception e) {
            log.error(e);
        }
        return mqQueueConnectionFactory;
    }

    @Bean
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQQueueConnectionFactory mqQueueConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setUsername(user);
        userCredentialsConnectionFactoryAdapter.setPassword(password);
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
        return userCredentialsConnectionFactoryAdapter;
    }

    @Bean
    @Primary
    public CachingConnectionFactory cachingConnectionFactory(
            UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
        cachingConnectionFactory.setSessionCacheSize(500);
        cachingConnectionFactory.setReconnectOnException(true);
        cachingConnectionFactory.setClientId("client-read");
        return cachingConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactoryRetry(
            UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
        cachingConnectionFactory.setSessionCacheSize(500);
        cachingConnectionFactory.setReconnectOnException(true);
        cachingConnectionFactory.setClientId("client-read-retry");
        return cachingConnectionFactory;
    }

    @Bean
    public PlatformTransactionManager jmsTransactionManager(CachingConnectionFactory cachingConnectionFactory) {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(cachingConnectionFactory);
        return jmsTransactionManager;
    }

    @Bean
    public JmsOperations jmsOperations(CachingConnectionFactory cachingConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
//        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setSessionAcknowledgeMode(AcknowledgeMode.CLIENT.getMode());
        return jmsTemplate;
    }

    @Bean
    public JmsOperations jmsOperationsRetry(UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        CachingConnectionFactory cachingConnectionFactoryRetry = this.cachingConnectionFactoryRetry(userCredentialsConnectionFactoryAdapter);
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactoryRetry);
        jmsTemplate.setReceiveTimeout(1000);
//        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setSessionAcknowledgeMode(AcknowledgeMode.CLIENT.getMode());
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory mqQueueConnectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("3");
        configurer.configure(factory, mqQueueConnectionFactory);
        return factory;
    }


}
