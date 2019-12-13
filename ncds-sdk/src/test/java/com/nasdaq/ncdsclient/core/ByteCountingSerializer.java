package com.nasdaq.ncdsclient.core;

import org.apache.kafka.common.serialization.Serializer;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ByteCountingSerializer<T> implements Serializer<T> {
    protected AtomicLong messageCount = new AtomicLong(0);
    protected AtomicLong serializedByteCount = new AtomicLong(0);
    protected int previousMessageSize = -1;

    @Override
    public final byte[] serialize(String topic, T data) {
        byte[] encoded = doSerialize(topic, data);
        messageCount.incrementAndGet();
        serializedByteCount.addAndGet(encoded.length);
        previousMessageSize = encoded.length;
        return encoded;
    }
    /**
     * Get the size (in bytes) that the previous call to serialize() produced.
     */
    public int getPreviousMessageSize() {
        return previousMessageSize;
    }
    /**
     * Get the total number of objects serialized.
     */
    public long getMessageCount() {
        return messageCount.get();
    }
    /**
     * Get the total number of bytes serialized.
     */
    public long getSerializedByteCount() {
        return serializedByteCount.get();
    }
    /**
     * Reset byte and message counters.
     */
    public void resetCounters() {
        this.previousMessageSize = -1;
        messageCount.set(0);
        serializedByteCount.set(0);
    }
    /**
     * Actually serialize data.
     */
    protected abstract byte[] doSerialize(String topic, T data);
}