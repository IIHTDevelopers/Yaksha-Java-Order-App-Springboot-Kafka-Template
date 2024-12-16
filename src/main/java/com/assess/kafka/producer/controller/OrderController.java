package com.assess.kafka.producer.controller;

import com.assess.kafka.producer.domain.OrderDto;
import com.assess.kafka.producer.domain.OrderEvent;
import com.assess.kafka.producer.producer.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderEventProducer orderEventProducer;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto requestedOrderDto) {
        try {
            OrderEvent orderEvent = orderEventProducer.sendCreateOrderEvent(requestedOrderDto, "Order Created");
            return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending Order event");
        }
    }
}
