package com.phoenixacces.apps.producer;


import com.phoenixacces.apps.jms.messages.JmsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JmsProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Value(value = "${activemq.message_queue}")
    private String QUEUE;

    public void send(JmsMessage message) {
        log.info("<<< Send message : {}", message);
        jmsTemplate.convertAndSend(QUEUE, message);
    }
}