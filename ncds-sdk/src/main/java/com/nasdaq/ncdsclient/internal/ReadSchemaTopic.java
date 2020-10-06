package com.nasdaq.ncdsclient.internal;

import com.nasdaq.ncdsclient.internal.utils.IsItJunit;
import com.nasdaq.ncdsclient.internal.utils.KafkaConfigLoader;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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
        schemaConsumer.subscribe(Collections.singletonList(controlSchemaName));
        Duration sec = Duration.ofSeconds(5);
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
        schemaConsumer.subscribe(Collections.singletonList(controlSchemaName));
        Duration sec = Duration.ofSeconds(5);
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
            controlMessageSchema = parser.parse(ClassLoader.getSystemResourceAsStream("ControlMessageSchema.avsc"));

            if (IsItJunit.isJUnitTest()) {
                kafkaProps = KafkaConfigLoader.loadConfig();
            }
            kafkaProps.put("key.deserializer", StringSerializer.class.getName());
            kafkaProps.put("value.deserializer", AvroDeserializer.class.getName());
            kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, cleindId + "_" + UUID.randomUUID().toString());
            kafkaProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "5048576");
            ConfigProperties.resolve(kafkaProps);
        }
        catch (Exception e) {
            throw e;
        }
        return new KafkaAvroConsumer(kafkaProps, controlMessageSchema);

    }

    private Schema internalSchema (String topic) throws Exception {
        try {
            final Schema topicSchema;
            Schema.Parser parser = new Schema.Parser();
            topicSchema = parser.parse(ClassLoader.getSystemResourceAsStream("schemas/" + topic + ".avsc"));
            return topicSchema;
        } catch (Exception e){
            throw new Exception("SCHEMA NOT FOUND FOR TOPIC: "+ topic);
        }
    }

   }