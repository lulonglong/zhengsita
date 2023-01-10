package share;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import share.kafka.KafkaMyProducer;

@SpringBootTest
public class KafkaTest {
    @Autowired
    private KafkaMyProducer kafkaMyProducer;

    @Test
    public void test_kafka_send() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            kafkaMyProducer.send_spring(i + "A template for executing high-level operations. When used with a DefaultKafkaProducerFactory, the template is thread-safe. The producer factory and org.apache.kafka.clients.producer.KafkaProducer ensure this; refer to their respective javadocs.\n" +
                    "Author:\n" +
                    "Marius Bogoevici, Gary Russell, Igor Stepanov, Artem Bilan, Biju Kunjummen, Endika Gutierrez, Thomas Strauß\n" +
                    "Type parameters:\n" +
                    "<K> – the key type.\n" +
                    "<V> – the value type. " + i);
        }
    }

    @Test
    public void test_kafka_listener() throws InterruptedException {
        Thread.sleep(1000000);
    }
}