package com.nasdaq.ncdsclient.core;

import com.nasdaq.ncdsclient.core.controlevent.StreamCompleted;
import com.nasdaq.ncdsclient.core.controlevent.StreamDeleted;
import com.nasdaq.ncdsclient.core.controlevent.StreamInitiated;
import com.nasdaq.ncdsclient.core.controlevent.StreamResumed;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.io.IOException;
import java.util.Map;

public class KafkaControlSchema {
    static Schema schema;



    public static final String CONTROL_TOPIC = "control";

    static final String NAME_FIELD = "name";
    static final String UUID_FIELD = "uuid";
    static final String REF_DATE_FIELD = "referenceDate";
    static final String TIMESTAMP_FIELD = "timestamp";
    static final String SOUP_PARTITION_FIELD = "soupPartition";
    static final String SCHEMA_FIELD = "schema";
    static final String FINAL_SEQUENCE_FIELD = "finalSequenceNumber";
    static final String RESUME_SEQUENCE_FIELD = "resumeSequenceNumber";


    private static final Map<String,Schema> RECORD_TYPE_MAP = new NonBlockingHashMap<>();


    static final String HEARTBEAT__TYPE_NAME = "Heartbeat";
    static final String STREAM_INIT__TYPE_NAME = "StreamInitiated";
    static final String STREAM_COMPLETED__TYPE_NAME = "StreamCompleted";
    static final String STREAM_RESUMED__TYPE_NAME = "StreamResumed";
    static final String STREAM_DELETED__TYPE_NAME = "StreamDeleted";

    public KafkaControlSchema() {
        Schema.Parser parser = new Schema.Parser();

        try {
            schema = parser.parse(ClassLoader.getSystemResourceAsStream("KafkaControlTopicSchema.json"));
            for (Schema type : schema.getTypes()) {
                RECORD_TYPE_MAP.put(type.getName(), type);
            }
        }
        catch (IOException x) {
            throw new RuntimeException("Unable to load control topic schema: " + x, x);
        }
    }

    /**
     * Get the control message schema.
     */
    public static Schema getSchema() {
        return schema;
    }

    /**
     * Create a Kafka deserializer for control schema messages.
     */
    public static Deserializer<GenericRecord> createControlMessageDeserializer() {
        return new AvroDeserializer(getSchema());
    }

    /**
     * Create a Kafka serializer for control schema messages.
     */
    public static Serializer<GenericRecord> createControlMessageSerializer() {
        return new GenericRecordSerializer(getSchema());
    }

    /**
     * Create a StreamInitiated message.
     */
    public static GenericRecord streamInitiated(final StreamInitiated event) {

        GenericRecord record = new GenericData.Record(RECORD_TYPE_MAP.get(STREAM_INIT__TYPE_NAME));

        record.put(NAME_FIELD, event.getName());
        record.put(UUID_FIELD, event.getUUID().toString());
        record.put(REF_DATE_FIELD, event.getReferenceDate().toString()); // YYYY-MM-DD
        record.put(SOUP_PARTITION_FIELD, event.getSoupPartition());
        record.put(TIMESTAMP_FIELD, event.getTimestamp());

        // NOTE: we use an empty string for null schemas so we don't have to make a new avro schema for these messages
        Schema schema = event.getMessageSchema();
        record.put(SCHEMA_FIELD, (schema == null) ? "" : schema.toString(false));

        return record;
    }

    /**
     * Create a Heartbeat message.
     */
    public static GenericRecord heartbeat(final long timestamp) {

        GenericRecord record = new GenericData.Record(RECORD_TYPE_MAP.get(HEARTBEAT__TYPE_NAME));

        record.put(TIMESTAMP_FIELD, timestamp);

        return record;
    }

    /**
     * Create a StreamCompleted event message.
     */
    public static GenericRecord streamCompleted(final StreamCompleted event) {
        GenericRecord record = new GenericData.Record(RECORD_TYPE_MAP.get(STREAM_COMPLETED__TYPE_NAME));

        record.put(UUID_FIELD, event.getUUID().toString());
        record.put(TIMESTAMP_FIELD, event.getTimestamp());
        record.put(FINAL_SEQUENCE_FIELD, event.getFinalSequenceNumber());

        return record;
    }

    /**
     * Create a StreamResumed event message.
     */
    public static GenericRecord streamResumed(final StreamResumed event) {
        GenericRecord record = new GenericData.Record(RECORD_TYPE_MAP.get(STREAM_RESUMED__TYPE_NAME));

        record.put(UUID_FIELD, event.getUUID().toString());
        record.put(TIMESTAMP_FIELD, event.getTimestamp());
        record.put(RESUME_SEQUENCE_FIELD, event.getResumeSequenceNumber());

        return record;
    }

    /**
     * Create a StreamDeleted event message.
     */
    public static GenericRecord streamDeleted(final StreamDeleted event) {
        GenericRecord record = new GenericData.Record(RECORD_TYPE_MAP.get(STREAM_DELETED__TYPE_NAME));

        record.put(UUID_FIELD, event.getUUID().toString());

        return record;
    }
}
