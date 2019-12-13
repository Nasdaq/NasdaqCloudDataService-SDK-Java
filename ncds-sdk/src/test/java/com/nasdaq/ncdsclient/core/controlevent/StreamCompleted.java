package com.nasdaq.ncdsclient.core.controlevent;

import java.util.Date;
import java.util.UUID;

public class StreamCompleted extends ControlEvent {
    private final UUID uuid;
    private final long timestamp;
    private final long finalSequenceNumber;

    public StreamCompleted(UUID uuid, long timestamp, long finalSequenceNumber) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.finalSequenceNumber = finalSequenceNumber;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getFinalSequenceNumber() {
        return finalSequenceNumber;
    }

    public String getTimestampReadable() {
        return new Date(timestamp).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamCompleted that = (StreamCompleted) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
