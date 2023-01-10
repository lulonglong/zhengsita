package share.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaMyProducer {
    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    public static void main(String[] args) {
        Properties props = initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>(kafkaDemo.topicName, "hello, Kafka1 ");
        try {
            producer.send(record);
            producer.send(record);
            producer.send(record);
            producer.send(record);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            producer.close();
        }
    }

    public void send_spring(String msgData){
        kafkaTemplate.send(kafkaDemo.topicName,msgData);
    }

    private static Properties initConfig(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
       // props.put ("client. id", "producer.client.id.demo");
        return props;
    }

}
