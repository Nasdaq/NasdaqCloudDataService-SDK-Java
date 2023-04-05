package com.nasdaq.ncdsclient.internal;

import com.nasdaq.ncdsclient.internal.utils.IsItJunit;
import com.nasdaq.ncdsclient.internal.utils.KafkaConfigLoader;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;

import static com.nasdaq.ncdsclient.internal.utils.AuthenticationConfigLoader.getClientID;

/**
 *  Class to Retrieve the Kafa Schema for the give Topic/Stream.
 *  1. Retrieve the schema set from the kafka Consumer using the control topic
 *  2. Locate the schema for the given topic and return the schema
 *  3. List
 */
public class ReadSchemaTopic {

    private String controlSchemaName;
    private Properties securityProps;
    private Properties kafkaProps;

    public ReadSchemaTopic(){
        this.controlSchemaName="control";
    }

    public Schema readSchema(String topic) throws Exception {
        KafkaConsumer schemaConsumer= getConsumer("Control-"+getClientID(securityProps));
        Duration sec = Duration.ofSeconds(10);
        Schema messageSchema = null;
        ConsumerRecord<String,GenericRecord> lastRecord=null;

        while (true) {
            ConsumerRecords<String, GenericRecord> schemaRecords = schemaConsumer.poll(sec);
            if(schemaRecords.isEmpty()){
                break;
            }
            Iterator<ConsumerRecord<String, GenericRecord>> recordsIterator = schemaRecords.iterator();
            while (recordsIterator.hasNext()) {
                ConsumerRecord<String, GenericRecord> record = recordsIterator.next();
                try {
                    Schema schema = record.value().getSchema();
                    List<Schema.Field> fldList = schema.getFields();
                    boolean nameFound = false;
                    for (int i = 0; i < fldList.size(); i++) {
                        if (fldList.get(i).name().equals("name")) {
                            nameFound = true;
                            break;
                        }
                    }
                    if (nameFound && (record.value().get("name").toString().equals(topic))) {
                        lastRecord = record;
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        if (lastRecord != null) {
            messageSchema = Schema.parse(lastRecord.value().get("schema").toString());
        }
        schemaConsumer.close();

        if (messageSchema==null){
            System.out.println("WARNING: Using the Old Schema!! It might not be latest schema");
            messageSchema= internalSchema(topic);
        }

        return  messageSchema;
    }
    public void setSecurityProps(Properties props) {
        securityProps = new Properties();
        securityProps.putAll(props);

    }
    public void setKafkaProps(Properties props) {
        kafkaProps = new Properties();
        kafkaProps.putAll(props);

    }
    public Set<String> getTopics() throws Exception{

        Set<String> topics = new HashSet<String>();

        KafkaConsumer schemaConsumer= getConsumer("Control-"+getClientID(securityProps));
        Duration sec = Duration.ofSeconds(10);
        while (true) {
            ConsumerRecords<String, GenericRecord> schemaRecords = schemaConsumer.poll(sec);
            if(schemaRecords.isEmpty()){
                break;
            }
            Iterator<ConsumerRecord<String, GenericRecord>> recordsIterator = schemaRecords.iterator();
            while (recordsIterator.hasNext()) {
                ConsumerRecord<String, GenericRecord> record = recordsIterator.next();
                try {
                    Schema schema = record.value().getSchema();
                    List<Schema.Field> fldList = schema.getFields();
                    for (int i = 0; i < fldList.size(); i++) {
                        if (fldList.get(i).name().equals("name")) {
                            topics.add((record.value().get("name").toString()));
                        }
                    }
                } catch (Exception e) {
                    //throw e;
                    break;
                }
            }
        }
        schemaConsumer.close();
        return topics ;
    }

    private KafkaAvroConsumer getConsumer(String cleindId) throws Exception {
        final Schema controlMessageSchema;
        //Properties kafkaProps = null;
        try {

            if(!IsItJunit.isJUnitTest()) {
                ConfigProperties.resolveAndExportToSystemProperties(securityProps);
            }

            Schema.Parser parser = new Schema.Parser();
            //controlMessageSchema = parser.parse(ClassLoader.getSystemResourceAsStream("ControlMessageSchema.avsc"));
            controlMessageSchema = parser.parse(this.getClass().getResourceAsStream("/ControlMessageSchema.avsc"));

            if (IsItJunit.isJUnitTest()) {
                kafkaProps = KafkaConfigLoader.loadConfig();
            }
            kafkaProps.put("key.deserializer", StringSerializer.class.getName());
            kafkaProps.put("value.deserializer", AvroDeserializer.class.getName());
            kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString().toLowerCase());
            kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, cleindId);
            kafkaProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "5048576");
            ConfigProperties.resolve(kafkaProps);
        }
        catch (Exception e) {
            throw e;
        }

        KafkaAvroConsumer kafkaAvroConsumer = new KafkaAvroConsumer(kafkaProps, controlMessageSchema);
        TopicPartition topicPartition = new TopicPartition(controlSchemaName,0);
        kafkaAvroConsumer.assign(Collections.singletonList(topicPartition));
        return seekTo7DaysBack(kafkaAvroConsumer, topicPartition);
    }

    private Schema internalSchema (String topic) throws Exception {
        try {
            final Schema topicSchema;
            Schema.Parser parser = new Schema.Parser();
            topicSchema = parser.parse(this.getClass().getResourceAsStream("/schemas/" + topic + ".avsc"));
            return topicSchema;
        } catch (Exception e){
            throw new Exception("SCHEMA NOT FOUND FOR TOPIC: "+ topic);
        }
    }

    private KafkaAvroConsumer seekTo7DaysBack(KafkaAvroConsumer kafkaAvroConsumer, TopicPartition topicPartition){
        Map<TopicPartition,Long> timestmaps = new HashMap();
        timestmaps.put(topicPartition , getTodayMidNightTimeStamp());
        Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = kafkaAvroConsumer.offsetsForTimes(timestmaps);
        OffsetAndTimestamp offsetAndTimestamp = null;
        if (offsetsForTimes != null && (offsetAndTimestamp = offsetsForTimes.get(topicPartition)) != null) {
            kafkaAvroConsumer.seek(topicPartition, offsetAndTimestamp.offset());
        } else {
            kafkaAvroConsumer.seekToBeginning(Collections.singleton(topicPartition));
        }
        return kafkaAvroConsumer;
    }

    private long getTodayMidNightTimeStamp(){

        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");

        Calendar today = Calendar.getInstance(timeZone);
        today.add(Calendar.DATE, -7);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long timestampFromMidnight = today.getTimeInMillis();

        return timestampFromMidnight;
    }

   }
