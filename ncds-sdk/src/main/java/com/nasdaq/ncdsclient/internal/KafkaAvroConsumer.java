package com.nasdaq.ncdsclient.internal;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

/**
 * Kafka consumer for Avro messages.  Key value is dependent on the configuration
 * for Soup2Kafka, the value is an Avro GenericRecord object.
 */
public class KafkaAvroConsumer extends BasicKafkaConsumer<String,GenericRecord> {
    public KafkaAvroConsumer(final Properties config, final Schema messageSchema) {
        super(config, new StringDeserializer(), new AvroDeserializer(messageSchema));
    }
}