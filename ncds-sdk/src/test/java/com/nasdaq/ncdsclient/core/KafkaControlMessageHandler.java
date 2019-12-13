package com.nasdaq.ncdsclient.core;

import com.nasdaq.ncdsclient.core.controlevent.StreamCompleted;
import com.nasdaq.ncdsclient.core.controlevent.StreamDeleted;
import com.nasdaq.ncdsclient.core.controlevent.StreamInitiated;
import com.nasdaq.ncdsclient.core.controlevent.StreamResumed;

/**
 * Handler callback interface for control messages.
 */
public interface KafkaControlMessageHandler {

    default void controlClientInitialized() { }

    default void onStreamInitiated(final StreamInitiated event) { }

    default void onStreamCompleted(final StreamCompleted event) { }

    default void onStreamResumed(final StreamResumed event) { }

    default void onHeartbeat(final long timestamp) { }

    default void onStreamDeleted(final StreamDeleted event) { }
}

