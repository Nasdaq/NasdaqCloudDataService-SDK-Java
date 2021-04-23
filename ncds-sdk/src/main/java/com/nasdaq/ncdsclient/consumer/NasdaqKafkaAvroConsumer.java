package com.nasdaq.ncdsclient.consumer;

import com.nasdaq.ncdsclient.internal.AvroDeserializer;
import com.nasdaq.ncdsclient.internal.KafkaAvroConsumer;
import com.nasdaq.ncdsclient.internal.ReadSchemaTopic;
import com.nasdaq.ncdsclient.internal.utils.AuthenticationConfigLoader;
import com.nasdaq.ncdsclient.internal.utils.IsItJunit;
import com.nasdaq.ncdsclient.internal.utils.KafkaConfigLoader;
import com.nasdaq.ncdsclient.news.NewsUtil;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.nasdaq.ncdsclient.internal.utils.AuthenticationConfigLoader.getClientID;

/**
 * This is a class which creates Kafka Consumer for Avro messages
 *
 * @author rucvan
 */
public class NasdaqKafkaAvroConsumer {

    private KafkaConsumer kafkaConsumer;
    private String clientID;

    private Properties securityProps;
    private Properties kafkaProps;
    private ReadSchemaTopic readSchemaTopic = new ReadSchemaTopic();

    public NasdaqKafkaAvroConsumer(Properties securityCfg,Properties kafkaCfg ) throws Exception {
        try {
            if  (kafkaCfg == null)
                if (IsItJunit.isJUnitTest()) {
                    Properties junitKafkaCfg = KafkaConfigLoader.loadConfig();
                    kafkaProps = junitKafkaCfg;
                }
                else {
                    throw new Exception("Kafka Configuration not Defined ");
                }

            else {
                kafkaProps = kafkaCfg;
                KafkaConfigLoader.validateAndAddSpecificProperties(kafkaProps);
            }

            if (securityCfg == null) {
                securityProps = new Properties();
                securityProps.setProperty(AuthenticationConfigLoader.OAUTH_CLIENT_ID, "unit-test"); // Just for the unit tests.
            }
            else {
                securityProps = securityCfg;

            }
        }
        catch (Exception e) {
            throw (e);
        }
        readSchemaTopic.setSecurityProps(securityProps);
        readSchemaTopic.setKafkaProps(kafkaProps);
        this.clientID = getClientID(securityProps);

    }

    /**
     * Return kafka consumer
     * @param streamName  Kafka Message Series topic Name
     * @return org.apache.kafka.clients.consumer.KafkaConsumer
     * @throws Exception - Java Exception
     */
    public  KafkaConsumer getKafkaConsumer(String streamName) throws Exception {
        try {
            Schema kafkaSchema = readSchemaTopic.readSchema(streamName);

            if (kafkaSchema == null) {
                throw new Exception("Kafka Schema not Found for Stream: " + streamName);
            }
            kafkaConsumer = getConsumer(kafkaSchema);
            kafkaConsumer.subscribe(Collections.singletonList(streamName + ".stream"));
         }
        catch (Exception e) {
            throw (e);
        }
        return kafkaConsumer;
    }

    /**
     * Return kafka consumer
     * @param streamName  Kafka Message Series topic Name
     * @param timestamp - timestamp in milliseconds since the UNIX epoch
     * @return org.apache.kafka.clients.consumer.KafkaConsumer
     * @throws Exception - Java Exception
     */
    public KafkaConsumer getKafkaConsumer(String streamName, long timestamp) throws Exception {

        try{
            Schema kafkaSchema = readSchemaTopic.readSchema(streamName);

            if (kafkaSchema == null) {
                throw new Exception("Kafka Schema not Found for Stream: " + streamName);
            }
            kafkaConsumer = getConsumer(kafkaSchema);

            // seek the consumer offset to a specific timestamp
            Map<TopicPartition,Long> timestmaps = new HashMap();
            TopicPartition topicPartition = new TopicPartition(streamName + ".stream",0);
            timestmaps.put(topicPartition , timestamp);
            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = kafkaConsumer.offsetsForTimes(timestmaps);
            System.out.println("Offset: "+ offsetsForTimes.get(topicPartition).offset());
            kafkaConsumer.seek(topicPartition, offsetsForTimes.get(topicPartition).offset());

            kafkaConsumer.subscribe(Collections.singletonList(streamName + ".stream"));

            return kafkaConsumer;
        }
        catch (Exception e){
            throw (e);
        }
    }

    /**
     *
     * @param avroSchema - Schema for the topic
     * @return KafkaConsumer
     * @throws Exception - Java exception
     */


    public  KafkaAvroConsumer getConsumer(Schema avroSchema) throws Exception {
        try {
            if(!IsItJunit.isJUnitTest()) {
                ConfigProperties.resolveAndExportToSystemProperties(securityProps);
            }
            //Properties kafkaProps = KafkaConfigLoader.loadConfig();

            kafkaProps.put("key.deserializer", StringDeserializer.class.getName());
            kafkaProps.put("value.deserializer", AvroDeserializer.class.getName());
            if(!kafkaProps.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)) {
                kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            }
            kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, this.clientID + "_" +getDate() + "_" + UUID.randomUUID().toString());
            ConfigProperties.resolve(kafkaProps);
            return new KafkaAvroConsumer(kafkaProps, avroSchema);
        }
        catch (Exception e) {
            throw e;
        }
    }

    /**
     * Return kafka consumer
     * @param topic  - Topic name
     * @return org.apache.avro.Schema
     * @throws Exception - Java exception
     */
    public  Schema getSchemaForTopic(String topic) throws Exception {
        try {
             Schema kafkaSchema = readSchemaTopic.readSchema(topic);
            return kafkaSchema;
        }
        catch (Exception e) {
            throw (e);
        }
    }

    /**
     * Return all topics
     * @return java.util.List
     * @throws Exception - Java exception
     */

    public List<String> getTopics() throws Exception {
        try{
            List<String> topicsList= new ArrayList<>();
             topicsList.addAll(readSchemaTopic.getTopics());
            return topicsList;
        }
        catch (Exception e){
            throw (e);
        }
    }

    public void close() throws Exception{
        kafkaConsumer.close();
    }

    public KafkaConsumer getNewsConsumer(String topic) throws Exception {
        try{
            Schema newsSchema = NewsUtil.getNewsSchema();
            if (newsSchema == null) {
                throw new Exception("News Schema not Found ");
            }
            kafkaConsumer = getConsumer(newsSchema);
            kafkaConsumer.subscribe(Collections.singletonList(topic+".stream"));
            return kafkaConsumer;
        }
        catch (Exception e){
            throw (e);
        }
    }

    private String getDate(){
        // Get Today's EST date
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        dateformat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String date = dateformat.format(new Date());
        return date;
    }
}
