package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Order;
import com.assess.kafka.consumer.jpa.OrderRepository;
import com.assess.kafka.producer.domain.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaOrderConsumerService {
    @Autowired
    private OrderRepository orderRepository;

    public Order listenCreateOrder(OrderDto orderDto) {
        Order dbOrder = Order.builder()
                .customerId(orderDto.getCustomerId())
                .status(orderDto.getStatus())
                .totalPrice(orderDto.getTotalPrice())
                .orderDate(LocalDateTime.now())
                .build();
        return orderRepository.save(dbOrder);
    }

}
