package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderDto {
    private String orderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private String status;

    public OrderDto() {
    }
}
