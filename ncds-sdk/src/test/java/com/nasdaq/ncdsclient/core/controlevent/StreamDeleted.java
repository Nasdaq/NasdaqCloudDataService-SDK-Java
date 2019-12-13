package com.nasdaq.ncdsclient.core.controlevent;

import java.util.UUID;

public class StreamDeleted extends ControlEvent {
    private final UUID uuid;

    public StreamDeleted(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamDeleted that = (StreamDeleted) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}