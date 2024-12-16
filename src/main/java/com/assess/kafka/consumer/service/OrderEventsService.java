package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Order;
import com.assess.kafka.consumer.entity.OrderEvent;
import com.assess.kafka.consumer.entity.OrderEventType;
import com.assess.kafka.consumer.jpa.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderEventsService {

    private final OrderEventRepository orderEventRepository;

    @Autowired
    public OrderEventsService(OrderEventRepository orderEventRepository) {
        this.orderEventRepository = orderEventRepository;
    }


    public void listenCreateOrderEvent(com.assess.kafka.producer.domain.OrderEvent orderEvent, Order dbOrder) {
        OrderEvent event = OrderEvent.builder()
                .eventType(mapEventType(orderEvent.getEventType()))
                .eventDetails(orderEvent.getEventDetails())
                .orderId(dbOrder.getId())
                .build();
        orderEventRepository.save(event);
    }

    private OrderEventType mapEventType(com.assess.kafka.producer.domain.OrderEventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED -> OrderEventType.ORDER_CREATED;
            case ORDER_CANCELLED -> OrderEventType.ORDER_CANCELLED;
        };
    }
}
