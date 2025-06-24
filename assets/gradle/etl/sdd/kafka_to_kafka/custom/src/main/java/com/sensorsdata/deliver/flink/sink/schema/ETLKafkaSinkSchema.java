package com.sensorsdata.deliver.flink.sink.schema;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

public class ETLKafkaSinkSchema implements KafkaRecordSerializationSchema<Tuple2<String, String>> {

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Tuple2<String, String> tuple2, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(tuple2.f0, tuple2.f1.getBytes(StandardCharsets.UTF_8));
    }
}
