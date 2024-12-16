package com.assess.kafka.consumer.jpa;

import com.assess.kafka.consumer.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    // Define custom query methods if needed
}