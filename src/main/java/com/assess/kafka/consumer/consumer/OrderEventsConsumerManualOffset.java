package com.assess.kafka.consumer.consumer;

import com.assess.kafka.consumer.entity.Order;
import com.assess.kafka.consumer.service.KafkaOrderConsumerService;
import com.assess.kafka.consumer.service.OrderEventsService;
import com.assess.kafka.producer.domain.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventsConsumerManualOffset implements AcknowledgingMessageListener<String, OrderEvent> {
    @Autowired
    private KafkaOrderConsumerService orderConsumerService;
    @Autowired
    private OrderEventsService orderEventsService;

    @Override
    @KafkaListener(topics = "${spring.kafka.order.topic.create-order}", groupId = "${spring.kafka.order.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, OrderEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("OrderEventsConsumerManualOffset Received message from Kafka: " + consumerRecord.value());
            if (consumerRecord.offset() % 2 == 0) {
                throw new RuntimeException("This is really odd.");
            }
            processMessage(consumerRecord.value());
            acknowledgment.acknowledge();
    }

    private void processMessage(OrderEvent orderEvent) {
        Order dbOrder = orderConsumerService.listenCreateOrder(orderEvent.getOrderDto());
        orderEventsService.listenCreateOrderEvent(orderEvent, dbOrder);
    }

}