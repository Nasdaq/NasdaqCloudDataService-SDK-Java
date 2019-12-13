package com.nasdaq.ncdsclient.core.controlevent;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class StreamInitiatedSummary extends ControlEvent {
    private final String name;
    private final UUID uuid;
    private final LocalDate referenceDate;
    private final int soupPartition;
    private final long timestamp;
    private Long finalSequenceNumber = null;

    public StreamInitiatedSummary(StreamInitiated event) {
        this.name = event.getName();
        this.uuid = event.getUUID();
        this.referenceDate = event.getReferenceDate();
        this.soupPartition = event.getSoupPartition();
        this.timestamp = event.getTimestamp();
    }

    public StreamInitiatedSummary(String name, UUID uuid, LocalDate referenceDate, int soupPartition, long timestamp) {
        this.name = name;
        this.uuid = uuid;
        this.referenceDate = referenceDate;
        this.soupPartition = soupPartition;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getReferenceDate() {
        return referenceDate.toString();
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

    public Long getFinalSequenceNumber() {
        return finalSequenceNumber;
    }

    public void setFinalSequenceNumber(Long finalSequenceNumber) {
        this.finalSequenceNumber = finalSequenceNumber;
    }

    public StreamInitiatedSummary withFinalSequenceNumber(Long finalSequenceNumber) {
        setFinalSequenceNumber(finalSequenceNumber);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamInitiatedSummary that = (StreamInitiatedSummary) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}

