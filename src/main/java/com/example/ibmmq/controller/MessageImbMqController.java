package com.example.ibmmq.controller;

import com.example.ibmmq.dto.MessageDto;
import com.example.ibmmq.listener.ListenerMQ;
import com.example.ibmmq.sender.SenderMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ibm/mq")
public class MessageImbMqController {

    @Autowired
    private SenderMQ senderMQ;

    @Autowired
    private ListenerMQ listenerMQ;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageDto messageDto) throws Exception {
        senderMQ.sendMessageWithCorrelationID(messageDto);
    }

    @GetMapping("/read")
    public ResponseEntity<String> read() {
        try {
            return ResponseEntity.ok(listenerMQ.readMessageFromMq());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/read/{correlationID}")
    public ResponseEntity<String> read(@PathVariable String correlationID) {
        try {
            return ResponseEntity.ok(listenerMQ.findMessageWithCorrelationID(correlationID));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
