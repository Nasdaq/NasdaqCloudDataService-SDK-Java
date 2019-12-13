package com.nasdaq.ncdsclient.internal.utils;

/**
 * This class is to parse TrackingID field of data
 */
public class TrackingID {

    /**
     * Returns the timestamp from TrackingId.
     * @param trackingId  - trackingId
     * @return - java.lang.Long timestamp in nanoseconds from midnight
     */
    public static long getTimestamp( long trackingId ) {
        return trackingId & 0xffffffffffffl;
    }

    /**
     * Returns the counter from TrackingId.
     * @param trackingId  - trackingId
     * @return - java.lang.Short counter
     */
    public static short getCounter( long trackingId ) {

        return (short)( trackingId >> 48 );
    }
}
