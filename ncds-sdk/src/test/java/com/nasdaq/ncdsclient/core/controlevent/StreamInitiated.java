package com.nasdaq.ncdsclient.core.controlevent;

import org.apache.avro.Schema;
import org.h2.util.StringUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class StreamInitiated extends ControlEvent {
    private final String name;
    private final UUID uuid;
    private final LocalDate referenceDate;
    private final int soupPartition;
    private final long timestamp;
    private final Schema messageSchema;

    public StreamInitiated(StreamInitiated copy, Schema newSchema) {
        this(copy.getName(),
                copy.getUUID(),
                copy.getReferenceDate(),
                copy.getSoupPartition(),
                copy.getTimestamp(),
                newSchema);
    }

    public StreamInitiated(String name, UUID uuid, LocalDate referenceDate, int soupPartition, long timestamp, Schema messageSchema) {
        this.name = name;
        this.uuid = uuid;
        this.referenceDate = referenceDate;
        this.soupPartition = soupPartition;
        this.timestamp = timestamp;
        this.messageSchema = messageSchema;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public int getSoupPartition() {
        return soupPartition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampReadable() {
        return new Date(timestamp).toString();
    }

    public Schema getMessageSchema() {
        return messageSchema;
    }

    public boolean isSameStreamForDate(final StreamInitiated other) {
        return StringUtils.equals(this.name, other.name)
                && this.referenceDate.equals(other.referenceDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamInitiated that = (StreamInitiated) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}