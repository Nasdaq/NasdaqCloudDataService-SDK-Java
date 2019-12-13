package com.nasdaq.ncdsclient.core.controlevent;

import java.util.Date;
import java.util.UUID;

public class StreamResumed extends ControlEvent {
    private final UUID uuid;
    private final long timestamp;
    private final long resumeSequenceNumber;

    public StreamResumed(UUID uuid, long timestamp, long resumeSequenceNumber) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.resumeSequenceNumber = resumeSequenceNumber;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampReadable() {
        return new Date(timestamp).toString();
    }

    public long getResumeSequenceNumber() {
        return resumeSequenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamResumed that = (StreamResumed) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}

