package kr.oracle.devmeetup.kafkaclient;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Producer {
    public static void main(final String[] args) {
        final Properties properties = new Properties();

        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                "cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com:9092");
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");

        properties.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"apackrsct/oracleidentitycloudservice/taewan.kim@oracle.com/ocid1.streampool.oc1.ap-tokyo-1.amaaaaaavsea7yiahyfz3ikb5ng3gwolua2x5cqj6dgtsnzpb5y5rzncx3na\" password=\"pp0A+AXRkdP1A5PL#TX)\";");
        properties.put(CommonClientConfigs.RETRIES_CONFIG, 5);
        properties.put("max.request.size", 1024 * 1024);
        //properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        final KafkaProducer<String, String> producer = new KafkaProducer<>(properties, new StringSerializer(),
                new StringSerializer());

        try {
            for (int i = 0; i < 100000; i++) {
                //String message = String.format("오라클 디벨로퍼 밋업 - 스트림 메시지 -%s (Kafka Client)", i);
                String message = String.format("Oracle Developer Meetup - Stream Message -%s (Kafka Client)", i);

                producer.send(
                    new ProducerRecord<String, String>("demo_stream", message)
                );
                System.out.println("[Completed Send Message]:"+message);
            }
        } catch (final Exception e) {
            System.out.println(e);
        } finally {
            producer.close();
        }

        System.out.println("End.");
    }
}