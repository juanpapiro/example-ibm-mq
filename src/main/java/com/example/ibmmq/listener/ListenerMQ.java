package com.example.ibmmq.listener;

import com.ibm.mq.headers.CCSID;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Log4j2
@Component
public class ListenerMQ {

    @Autowired
    @Qualifier("jmsOperations")
    private JmsOperations jmsOperations;

    @Autowired
    @Qualifier("jmsOperationsRetry")
    private JmsOperations jmsOperationsRetry;

    @Value("${ibmmq.orderQueue}")
    private String queueOrder;


    public String readMessageFromMq() throws JMSException {
        TextMessage textMessage = (TextMessage) jmsOperations.receive(queueOrder);
        assert textMessage != null;
        String stringFromQueue;
        try {
            stringFromQueue = textMessage.getText();
            log.info(stringFromQueue);
        } catch (JMSException e) {
            log.error(e.getMessage());
            throw e;
        }
        return stringFromQueue;
    }


    @JmsListener(
            destination = "${ibmmq.orderQueue}",
            containerFactory = "myFactory",
            selector = "JMSCorrelationID='ID:8885818485996d9799968385a2a2'"
    )
    public void listener(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        log.info("Mensagem com header recebida: {} - corretationID: {}",
                textMessage.getText(), textMessage.getJMSCorrelationID());
    }

    @JmsListener(
            destination = "${ibmmq.orderQueue}",
            containerFactory = "myFactory",
            selector = "JMSCorrelationID NOT IN ('ID:8885818485996d9799968385a2a2')"
    )
    public void listenerOthers(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        log.info("Mensagem recebida: {} - corretationID: {}",
                textMessage.getText(), textMessage.getJMSCorrelationID());
    }


    /**
     * Busca mensagem por correlationID
     */
    public String findMessageWithCorrelationID(String correlationID) throws Exception {
        String correlationIdFormat = correlationIdFormat(correlationID);
        Message message = jmsOperationsRetry.receiveSelected(queueOrder, correlationIdFormat);
        TextMessage textMessage = (TextMessage) message;
        assert textMessage != null;
        return textMessage.getText();
    }

    public String correlationIdFormat(String correlationID) throws UnsupportedEncodingException {
        String correlationIdHex = Hex.encodeHexString(correlationID.getBytes(CCSID.getCodepage(37)));
        return String.format("JMSCorrelationID='ID:%s'", correlationIdHex);
    }

}
