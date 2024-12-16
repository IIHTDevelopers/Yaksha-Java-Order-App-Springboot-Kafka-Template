package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderEvent {
    private String eventId;
    private OrderEventType eventType;
    private OrderDto orderDto;
    private String eventDetails;

    public OrderEvent() {

    }
}

