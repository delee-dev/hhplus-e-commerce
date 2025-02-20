package kr.hhplus.be.server.kafka;

import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@EmbeddedKafka(topics = "test-topic")
public class KafkaTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private EventHandler eventHandler;

    @Test
    public void publishAndConsumeTest() throws InterruptedException {
        // given
        String payload = "Hello World!";

        // when
        kafkaTemplate.send("test-topic", payload);
        Thread.sleep(500);

        // then
        assertThat(eventHandler.result).isEqualTo(payload);
    }
}

@Component
class EventHandler {
    public String result = "";

    @KafkaListener(topics = "test-topic", groupId = "test-consumer")
    public void handler(String payload) {
        result = payload;
    }
}
