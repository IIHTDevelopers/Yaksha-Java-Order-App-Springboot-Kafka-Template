package com.assess.kafka;

import com.assess.kafka.consumer.consumer.OrderEventsConsumerManualOffset;
import com.assess.kafka.producer.controller.OrderController;
import com.assess.kafka.producer.domain.OrderDto;
import com.assess.kafka.producer.domain.OrderEvent;
import com.assess.kafka.producer.domain.OrderEventType;
import com.assess.kafka.producer.producer.OrderEventProducer;
import com.assess.kafka.testutils.MasterData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.assess.kafka.testutils.TestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@EnableKafka
public class OrderApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;


    @MockBean
    private OrderEventProducer orderEventProducer;

    @MockBean
    private OrderEventsConsumerManualOffset orderEventsConsumerManualOffset;

    @Test
    public void test_BookControllerSendBook() throws Exception {
        final int[] count = new int[1];
        OrderDto orderDto = OrderDto.builder()
                .orderDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        OrderEvent orderEvent = OrderEvent.
                builder()
                .eventDetails("Create Order")
                .eventType(OrderEventType.ORDER_CREATED)
                .orderDto(orderDto)
                .build();

        when(orderEventProducer.sendCreateOrderEvent(orderDto, "Order Created")).then(new Answer<OrderEvent>() {

            @Override
            public OrderEvent answer(InvocationOnMock invocation) throws Throwable {
                // TODO Auto-generated method stub
                count[0]++;
                return orderEvent;
            }
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/orders/")
                .content(MasterData.asJsonString(orderDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        yakshaAssert(currentTest(), count[0] == 1, businessTestFile);

    }

    @Test
    public void testSendBook() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .orderDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        OrderEvent orderEvent = OrderEvent.
                builder()
                .eventDetails("Create Order")
                .eventType(OrderEventType.ORDER_CREATED)
                .orderDto(orderDto)
                .build();
        try {
            CompletableFuture<SendResult<String, OrderEvent>> mockFuture = mock(CompletableFuture.class);
            when(kafkaTemplate.send("create-order", orderEvent.getEventType().toString(), orderEvent)).thenReturn(mockFuture);
            this.orderEventProducer.sendCreateOrderEvent(orderDto, "Order Created");
            yakshaAssert(currentTest(), true, businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }

    }

    @Test
    @Disabled
    public void testConsumeBook() {
        OrderDto orderDto = OrderDto.builder()
                .orderDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        OrderEvent orderEvent = OrderEvent.
                builder()
                .eventDetails("Create Order")
                .eventType(OrderEventType.ORDER_CREATED)
                .orderDto(orderDto)
                .build();

        kafkaTemplate.send("create-order", orderEvent.getEventType().toString(), orderEvent);


        await().atMost(5, SECONDS).untilAsserted(() -> {
            ConsumerRecord<String, OrderEvent> mockRecord = mock(ConsumerRecord.class);

            Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);
            orderEventsConsumerManualOffset.onMessage(mockRecord, mockAcknowledgment);
            yakshaAssert(currentTest(), true, businessTestFile);

        });
    }


}
