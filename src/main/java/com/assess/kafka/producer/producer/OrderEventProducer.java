package com.assess.kafka.producer.producer;

import com.assess.kafka.producer.domain.OrderDto;
import com.assess.kafka.producer.domain.OrderEvent;
import com.assess.kafka.producer.domain.OrderEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${spring.kafka.order.topic.create-order}")
    private String topic;

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderEvent sendCreateOrderEvent(OrderDto orderDto, String eventDetails) {
        OrderEvent orderEvent = OrderEvent.
                builder()
                .orderDto(orderDto)
                .eventType(OrderEventType.ORDER_CREATED)
                .eventDetails(eventDetails + orderDto.getCustomerId())
                .build();
        try {
            CompletableFuture<SendResult<String, OrderEvent>> sendResultCompletableFuture = kafkaTemplate.send(topic, orderEvent.getEventType().toString(), orderEvent);
           return sendResultCompletableFuture.get().getProducerRecord().value();
        } catch (Exception e) {
            log.debug("Error occurred while publishing message due to " + e.getMessage());
        }
        return orderEvent;
    }
}
