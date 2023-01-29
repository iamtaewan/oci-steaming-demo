~/kafka/bin/kafka-console-producer.sh \
--bootstrap-server cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com:9092 \
--topic demo_stream \
--producer.config ./producer-stream.properties
