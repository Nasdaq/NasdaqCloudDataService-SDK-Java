package com.nasdaq.ncdsclient.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper containing immutable list of Kafka Brokers indexable by their brokerId.
 */
public class KafkaBrokers implements Iterable<KafkaBroker> {
    /**
     * Immutable mapping of brokerId to KafkaBroker instance.
     */
    private Map<Integer, KafkaBroker> brokerMap;

    /**
     * Constructor.
     * @param brokers List of KafkaBrokers in a cluster.
     */
    public KafkaBrokers(final Collection<KafkaBroker> brokers) {
        // Build immutable map.
        this.brokerMap = Collections.unmodifiableMap(brokers
                .stream()
                .collect(Collectors.toMap(KafkaBroker::getBrokerId, Function.identity()))
        );
    }

    /**
     * Lookup and return Kafka Broker by its id.
     * @param brokerId id of the broker.
     * @return KafkaBroker.
     * @throws IllegalArgumentException if requested an invalid broker id.
     */
    public KafkaBroker getBrokerById(final int brokerId) throws IllegalArgumentException {
        if (!brokerMap.containsKey(brokerId)) {
            throw new IllegalArgumentException("No broker exists with id " + brokerId);
        }
        return brokerMap.get(brokerId);
    }

    /**
     * Returns an immutable list of KafkaBroker instances.
     * @return Immutable List of Brokers.
     */
    public List<KafkaBroker> asList() {
        return Collections.unmodifiableList(
                new ArrayList<>(brokerMap.values())
        );
    }

    /**
     * Returns a stream of brokers.
     * @return Stream of Brokers.
     */
    public Stream<KafkaBroker> stream() {
        return asList().stream();
    }

    /**
     * Returns how many brokers are represented.
     * @return Number of brokers.
     */
    public int size() {
        return brokerMap.size();
    }

    @Override
    public Iterator<KafkaBroker> iterator() {
        return asList().iterator();
    }

    @Override
    public void forEach(final Consumer<? super KafkaBroker> action) {
        asList().forEach(action);
    }

    @Override
    public Spliterator<KafkaBroker> spliterator() {
        return asList().spliterator();
    }

    @Override
    public String toString() {
        return "KafkaBrokers{"
                + asList()
                + '}';
    }
}