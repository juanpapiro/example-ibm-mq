package com.example.ibmmq.sender;

import com.example.ibmmq.dto.MessageDto;
import com.ibm.mq.headers.CCSID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Log4j2
@Component
public class SenderMQ {

    @Autowired
    private JmsOperations jmsOperations;

    @Value("${ibmmq.orderQueue}")
    private String queueOrder;

    public void sendMessageWithCorrelationID(MessageDto messageDto) throws Exception {
        try {
            jmsOperations.convertAndSend(queueOrder, messageDto.getMessage(), textMessage -> {
                textMessage.setJMSCorrelationIDAsBytes(encodeStringToBytesArray(messageDto.getCorrelationID()));
                return textMessage;
            });
        } catch (JmsException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void sendMessageToMQ(String mqString) throws JmsException {
        try {
            jmsOperations.convertAndSend(queueOrder, mqString);
        } catch (JmsException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private byte[] encodeStringToBytesArray(String value) {
        try {
            return value.getBytes(CCSID.getCodepage(37));
        } catch(Exception e) {
            log.error(e);
            throw new RuntimeException();
        }
    }

}
