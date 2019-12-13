package com.nasdaq.ncdsclient.core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Class that wraps relevant information about data that was published to kafka.
 * Used within our tests.
 *
 * @param <K> - Object type of the Key written to kafka.
 * @param <V> - Object type of the Value written to kafka.
 */
public class ProducedKafkaRecord<K, V> {
    private final String topic;
    private final int partition;
    private final long offset;
    private final K key;
    private final V value;

    /**
     * Constructor.
     *
     * @param topic Topic that the record was published to.
     * @param partition Partition that the record was published to.
     * @param offset Offset that the record was published to.
     * @param key The key that was published.
     * @param value The message value that was published.
     */
    ProducedKafkaRecord(String topic, int partition, long offset, K key, V value) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.key = key;
        this.value = value;
    }

    /**
     * Utility factory.
     * @param recordMetadata Metadata about the produced record.
     * @param producerRecord The original record that was produced.
     * @param <K> Type of key
     * @param <V> Type of message
     * @return A ProducedKafkaRecord that represents metadata about the original record, and the results of it being published.
     */
    static <K,V> ProducedKafkaRecord<K,V> newInstance(
            final RecordMetadata recordMetadata,
            final ProducerRecord<K,V> producerRecord) {
        return new ProducedKafkaRecord<>(
                recordMetadata.topic(),
                recordMetadata.partition(),
                recordMetadata.offset(),
                producerRecord.key(),
                producerRecord.value()
        );
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ProducedKafkaRecord{"
                + "topic='" + topic + '\''
                + ", partition=" + partition
                + ", offset=" + offset
                + ", key=" + key
                + ", value=" + value
                + '}';
    }
}