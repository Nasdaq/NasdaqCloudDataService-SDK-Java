package com.nasdaq.ncdsclient.internal;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

/**
 * Wrapper for the Avro Deserializer
 */
public class AvroDeserializer implements Deserializer<GenericRecord> {
    private final GenericDatumReader<GenericRecord> reader;
    private final DecoderFactory decoderFactory;

    private BinaryDecoder decoder = null;

    public AvroDeserializer(final Schema schema) {
        if (schema == null) {
            throw new NullPointerException("Avro schema must not be null!");
        }
        this.decoderFactory = DecoderFactory.get();
        this.reader = new GenericDatumReader<>(schema);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public GenericRecord deserialize(final String topic, final byte[] data) {
        decoder = decoderFactory.binaryDecoder(data, decoder);
        try {
            return reader.read(null, decoder);
        }
        catch (IOException x) {
            throw new RuntimeException("Unable to decode byte[] as Avro: " + x, x);
        }
    }

    @Override
    public void close() {
    }
}
