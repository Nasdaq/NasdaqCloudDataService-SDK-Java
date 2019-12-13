package com.nasdaq.ncdsclient.internal;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;


/**
 * Base class for all Kafka consumers which adds some utility methods.
 */
public class BasicKafkaConsumer<K,V> extends KafkaConsumer<K,V> {
    public BasicKafkaConsumer(final Properties config,
                              final Deserializer<K> keyDeserializer,
                              final Deserializer<V> valueDeserializer) {
        super(config, keyDeserializer, valueDeserializer);
    }

    /**
     *
     * @param topic - Topic Name
     */
    public void subscribe(final String topic) {
        subscribe(Collections.singletonList(topic));
    }

    /**
     *
     * @return - Returns the Assignment value
     */
    public Set<TopicPartition> ensureAssignment() {
        // make sure we're attached and have assignments
        poll(0);
        return assignment();
    }

}
