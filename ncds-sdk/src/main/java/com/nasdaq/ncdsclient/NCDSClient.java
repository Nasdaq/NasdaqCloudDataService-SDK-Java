package com.nasdaq.ncdsclient;

import com.nasdaq.ncdsclient.consumer.NasdaqKafkaAvroConsumer;
import com.nasdaq.ncdsclient.internal.utils.AuthenticationConfigLoader;
import com.nasdaq.ncdsclient.internal.utils.IsItJunit;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.TopicPartition;


import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This is a client class to access the nasdaq's market data
 *
 * @author rucvan
 */
public class NCDSClient {

    private NasdaqKafkaAvroConsumer nasdaqKafkaAvroConsumer;

    /**
     *
     * @param securityCfg  - Authentication Security Properties passed from the Client
     * @param kafkaCfg
     * @throws Exception   - Java Exception
     */
    public NCDSClient(Properties securityCfg,Properties kafkaCfg) throws Exception {
        try {
            if (securityCfg != null && AuthenticationConfigLoader.validateSecurityConfig(securityCfg, kafkaCfg)) {
                nasdaqKafkaAvroConsumer = new NasdaqKafkaAvroConsumer(securityCfg,kafkaCfg);
            }
            else if (IsItJunit.isJUnitTest()) {
                nasdaqKafkaAvroConsumer = new NasdaqKafkaAvroConsumer(null, null); // Just for Unit tests
            }
            else {
                throw new Exception("Authentication Arguments are missing ");
            }
        }
        catch (Exception e){
            throw (e);
        }
    }


    /**
     * Returns the Eligible topics/streams for the client.
     * @return - Topics in  a String array
     * @throws Exception  - Java Exception
     */
    public String[] ListTopicsForTheClient() throws Exception {
        try {
            String[] topics = nasdaqKafkaAvroConsumer.getTopics().toArray(new String[0]);
            return topics;
        }
        catch (Exception e){
            throw (e);
        }
    }

    /**
     * Returns Schema for Topic/Stream r Object
     * @param topic - Topic/Stream name
     * @return - Kafka Schema
     * @throws Exception  - Java Exception
     */
    public String getSchemaForTheTopic(String topic) throws Exception {
        try {
            Schema kafkaSchema = nasdaqKafkaAvroConsumer.getSchemaForTopic(topic);
            return kafkaSchema.toString(true);
        }
        catch (Exception e) {
            throw (e);
        }
    }
    /**
     * Return nasdaq's market data kafka consumer
     * @param topic - Topic/Stream name
     * @return org.apache.kafka.clients.consumer.KafkaConsumer
     * @throws Exception  - Java Exception
     */
    public KafkaConsumer NCDSKafkaConsumer(String topic) throws Exception {
        try {
            return nasdaqKafkaAvroConsumer.getKafkaConsumer(topic);
        }
        catch (Exception e) {
            throw (e);
        }
    }

    /**
     * Return nasdaq's market data kafka consumer
     * @param topic - Topic/Stream name
     * @param timestamp - timestamp in milliseconds since the UNIX epoch
     * @return org.apache.kafka.clients.consumer.KafkaConsumer
     * @throws Exception  - Java Exception
     */
    public KafkaConsumer NCDSKafkaConsumer(String topic, long timestamp) throws Exception {
        try {
            return nasdaqKafkaAvroConsumer.getKafkaConsumer(topic, timestamp);
        }
        catch (Exception e) {
            throw (e);
        }
    }

    /**
     * Return nasdaq's News kafka consumer
     * @return org.apache.kafka.clients.consumer.KafkaConsumer
     * @throws Exception  - Java Exception
     */
    public KafkaConsumer NCDSNewsKafkaConsumer(String topic) throws Exception {
        try {
            return nasdaqKafkaAvroConsumer.getNewsConsumer(topic);
        }
        catch (Exception e) {
            throw (e);
        }
    }

    //get metric on cosumer
    /**
     * Return first 10 messages of the given topic
     * @param topicName - Topic/Stream name
     * @return Map of key and Record Value
     * @throws Exception  - Java Exception
     */
    public ConsumerRecords<String, GenericRecord> topMessages(String topicName) throws Exception {
        try {
            Duration mins = Duration.ofMinutes(Integer.parseInt("1"));
            KafkaConsumer kafkaConsumer = NCDSKafkaConsumer(topicName);
            ConsumerRecords<String, GenericRecord> records = kafkaConsumer.poll( mins.toMillis());
            kafkaConsumer.close();
            return records;
        }
        catch (Exception e) {
            throw (e);
        }

    }

    /**
     * Return first 10 messages of the given topic
     * @param topicName - Topic/Stream name
     * @param timestamp - timestamp in milliseconds since the UNIX epoch
     * @return Map of key and Record Value
     * @throws Exception  - Java Exception
     */
    public ConsumerRecords<String, GenericRecord> topMessages(String topicName, long timestamp) throws Exception {
        try {
            Duration mins = Duration.ofMinutes(Integer.parseInt("1"));
            KafkaConsumer kafkaConsumer = NCDSKafkaConsumer(topicName, timestamp);
            ConsumerRecords<String, GenericRecord> records = kafkaConsumer.poll( mins.toMillis());
            kafkaConsumer.close();
            return records;
        }
        catch (Exception e) {
            throw (e);
        }

    }

    /**
     *
     * @param topicName  - Topic name
     * @return Metrics for the Kafka Topic
     * @throws Exception Java Exception
     */
    public Map<MetricName, ? extends Metric> getMetrics(String topicName) throws Exception {

        try {
            KafkaConsumer kafkaConsumer = NCDSKafkaConsumer(topicName);
            Map<MetricName, ? extends Metric> metrics = kafkaConsumer.metrics();
            kafkaConsumer.close();
            return metrics;
        }
        catch (Exception e) {
            throw (e);
        }
    }

    /**
     * Writes the first message to the file in current directory for given message type
     * @param topicName  - Topic name
     * @param messageName - Name of the message (get name from schema)
     * @return return the example message
     * @throws Exception Java Exception
     */
    public String getSampleMessages(String topicName, String messageName, boolean allMessages) throws Exception {
        KafkaConsumer kafkaConsumer = null;
        String sampleMsg = null;
        boolean found = false;

        try {
            kafkaConsumer = NCDSKafkaConsumer(topicName);
            while (!found) {
                Duration mins = Duration.ofMinutes(Integer.parseInt("1"));
                ConsumerRecords<String, GenericRecord> records = kafkaConsumer.poll(mins);
                if (records.isEmpty() || endOfData(kafkaConsumer)){
                    System.out.println("--------------------------------END of Stream------------------");
                    break;
                }
                for (ConsumerRecord<String, GenericRecord> record : records){
                    if(messageName.equals(record.value().getSchema().getName())){
                        sampleMsg = record.value().toString();
                        if (allMessages){
                            if (sampleMsg!= null) System.out.println(sampleMsg);
                        }
                        else{
                            found = true;
                        }
                    }
                }
            }
        } catch (Exception e){
            throw (e);
        }
        finally {
            kafkaConsumer.close();
        }
        return sampleMsg;
    }

    /**
     * Close Nasdaq Kafka Consumer
     * @throws Exception Java Exception
     */
    public void closeConsumer() throws Exception {
        try{
            nasdaqKafkaAvroConsumer.close();
        }
        catch (Exception e){
            throw (e);
        }
    }

    private boolean endOfData(KafkaConsumer consumer) {
        // see if we have a final sequence number for this stream yet
        // final sequence number is delivered in the StreamCompleted event
        // which is sent by Soup2Kafka when it's done publishing a stream
        Set<TopicPartition> topicPartitions = consumer.assignment();
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
        for (Map.Entry<TopicPartition, Long> offset : endOffsets.entrySet()) {
            long position = consumer.position(offset.getKey());
            if (position != offset.getValue()) {
                // not at the end of this topic+partition
                return false;
            }
        }
        return true;
    }



}
