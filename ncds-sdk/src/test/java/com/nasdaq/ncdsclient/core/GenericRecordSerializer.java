package com.nasdaq.ncdsclient.core;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class GenericRecordSerializer  extends ByteCountingSerializer<GenericRecord> {

    private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final Encoder avroEncoder;
    private final DatumWriter<GenericRecord> datumWriter;





    public GenericRecordSerializer(Schema schema) {
        this.byteArrayOutputStream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
        this.avroEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
        this.datumWriter = new GenericDatumWriter<>(schema);
    }


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] doSerialize(String topic, GenericRecord data) {
        // convert to bytes
        try {
            datumWriter.write(data, avroEncoder);
            avroEncoder.flush();
        }
        catch (IOException x) {
            throw new RuntimeException("Unable to encode message to Avro: " + x, x);
        }

        // now pull out the bytes from the stream
        byte[] encodedBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.reset(); // re-use this output stream

        return encodedBytes;
    }

    @Override
    public void close() {
    }
}