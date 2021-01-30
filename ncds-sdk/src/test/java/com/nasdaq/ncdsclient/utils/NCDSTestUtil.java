package com.nasdaq.ncdsclient.utils;

import com.nasdaq.ncdsclient.core.GenericRecordSerializer;
import com.nasdaq.ncdsclient.core.KafkaControlSchema;
import com.salesforce.kafka.test.KafkaTestServer;
import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.listeners.BrokerListener;
import com.salesforce.kafka.test.listeners.PlainListener;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;

public class NCDSTestUtil {
    KafkaTestServer kafkaTestServer;
    KafkaTestUtils kafkaTestUtils;
    Properties config;
    ArrayList<String> topicsOnStream = new ArrayList<String>();
    ArrayList<GenericRecord> messagesOnStream = new ArrayList<GenericRecord>();
    final String ctrlTopic = "control";
    final String mockDataStream = "MOCK.stream";
    Map<String, Schema> CTRL_MSG_RECORD_TYPE_MAP = new NonBlockingHashMap<>();
    Map<String, Schema> GIDS_MSG_RECORD_TYPE_MAP = new NonBlockingHashMap<>();

    public NCDSTestUtil() throws Exception {
        beforeEachTest();
        addSchemasToControlTopic();
        pushMockMessages();
    }

    private Properties getConfig() {
        this.config = new Properties();
        final String kafkaConnectString = kafkaTestServer.getKafkaConnectString();
        config.put("group.id", "test-consumer-group");
        config.put("enable.auto.commit", "false");
        config.put("auto.offset.reset", "earliest");
        config.put("bootstrap.servers", kafkaConnectString);
        config.put("client.id", UUID.randomUUID().toString());
        config.put("request.timeout.ms", 15000);
        return config;
    }

    private GenericRecord getStreamInitiatedRecord(String strmName, Schema schema) {
        GenericRecord record = new GenericData.Record(CTRL_MSG_RECORD_TYPE_MAP.get("StreamInitiated"));
        record.put("name", strmName);
        record.put("uuid", UUID.randomUUID().toString());
        record.put("referenceDate",
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now())); // YYYY-MM-DD
        record.put("soupPartition", 0);
        record.put("timestamp", System.currentTimeMillis());
        record.put("schema", (schema == null) ? "" : schema.toString(false));
        return record;
    }

    private void beforeEachTest() throws Exception {
        final List<BrokerListener> brokerListeners = new ArrayList<>();
        brokerListeners.add(new PlainListenerSpecificPort());
        kafkaTestServer = new KafkaTestServer(getDefaultBrokerOverrideProperties(), brokerListeners);
        kafkaTestServer.start();
    }

    private Properties getDefaultBrokerOverrideProperties() {
        // Speed up shutdown in our tests
        final Properties overrideProperties = new Properties();
        overrideProperties.setProperty("controlled.shutdown.max.retries", "0");
        overrideProperties.setProperty("controlled.shutdown.enable", "true");
        overrideProperties.setProperty("controlled.shutdown.retry.backoff.ms", "100");
        return overrideProperties;
    }
    private void addSchemasToControlTopic() throws Exception {
        try {

            int partitionId = 0;
            // Define our topics
            String nlsKey = "NLSUTP";
            String gidsKey = "GIDS";
            String mockKey = "MOCK";
            Schema nlsSchema, gidsSchema, ctrlSchema, mockMsgSchema;

            Schema.Parser parser = new Schema.Parser();
            nlsSchema = parser.parse(ClassLoader.getSystemResourceAsStream("testNLSUTP.avsc"));
            gidsSchema = parser.parse(ClassLoader.getSystemResourceAsStream("testGIDS.avsc"));
            mockMsgSchema = parser.parse(ClassLoader.getSystemResourceAsStream("testMock.avsc"));
            ctrlSchema = parser.parse(ClassLoader.getSystemResourceAsStream("ControlMessageSchema.avsc"));
            for (Schema type : ctrlSchema.getTypes()) {
                CTRL_MSG_RECORD_TYPE_MAP.put(type.getName(), type);
            }
            for (Schema type : gidsSchema.getTypes()) {
                GIDS_MSG_RECORD_TYPE_MAP.put(type.getName(), type);
            }

            kafkaTestUtils = new KafkaTestUtils(kafkaTestServer);

            // Build default client configuration.
            // Create a topic.
            kafkaTestUtils.createTopic(ctrlTopic, 1, (short) 1);
            // Define override properties.
            // Create a new producer
            KafkaControlSchema kafkaCtrlSchema = new KafkaControlSchema();

            Serializer<GenericRecord> controlMessageSerializer = kafkaCtrlSchema.createControlMessageSerializer();


            Properties producerConfig = getConfig();
            try {

                // Creating producer
                KafkaProducer<String, byte[]> producer = kafkaTestUtils.getKafkaProducer(
                        StringSerializer.class,
                        ByteArraySerializer.class,
                        producerConfig);

                //Sending NLS Schema
                GenericRecord nlsRecord = getStreamInitiatedRecord(nlsKey, nlsSchema);
                byte[] nlsEncoded = controlMessageSerializer.serialize(null, nlsRecord);
                ProducerRecord<String, byte[]> nlsProducerRecord
                        = new ProducerRecord<>(ctrlTopic, partitionId, nlsKey, nlsEncoded);
                producer.send(nlsProducerRecord);
                topicsOnStream.add(nlsKey);

                //Sending GIDS schema
                GenericRecord gidsRecord = getStreamInitiatedRecord(gidsKey, gidsSchema);
                byte[] gidsEncoded = controlMessageSerializer.serialize(null, gidsRecord);
                ProducerRecord<String, byte[]> gidsProducerRecord = new ProducerRecord<>(ctrlTopic, partitionId, gidsKey, gidsEncoded);
                producer.send(gidsProducerRecord);
                topicsOnStream.add(gidsKey);

                //Adding Mock message Schema
                GenericRecord mockMsgRecord = getStreamInitiatedRecord(mockKey, mockMsgSchema);
                byte[] mockMessageEncoded = controlMessageSerializer.serialize(null, mockMsgRecord);
                ProducerRecord<String, byte[]> mockProducerRecord = new ProducerRecord<>(ctrlTopic, partitionId, mockKey, mockMessageEncoded);
                Future<RecordMetadata> future = producer.send(mockProducerRecord);
                topicsOnStream.add(mockKey);

                producer.flush();
                while (!future.isDone()) {
                    Thread.sleep(500L);
                }
                producer.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] getAddedTopics(){
        return topicsOnStream.toArray(new String[0]);
    }

    public ArrayList<GenericRecord> getMockMessages(){
        return messagesOnStream;
    }

    public String getSchemaForTopic(String schemaFile) throws IOException {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(ClassLoader.getSystemResourceAsStream(schemaFile));
        return schema.toString(true);

    }

    public void pushMockMessages() throws IOException {

        kafkaTestUtils.createTopic(mockDataStream, 1, (short) 1);
        ArrayList<GenericRecord> records= getMockerGenericRecord(1);
        Properties producerConfig = getConfig();
        try {

            // Creating Producer
            KafkaProducer<String, byte[]> mockerProducer = kafkaTestUtils.getKafkaProducer(
                    StringSerializer.class,
                    ByteArraySerializer.class,
                    producerConfig);

            // Adding mock records to topic
            for(GenericRecord record: records){
                messagesOnStream.add(record);
                Schema mockerSchema = new Schema.Parser().parse(ClassLoader.getSystemResourceAsStream("testMock.avsc"));
                byte[] encodedMocker = new GenericRecordSerializer(mockerSchema).doSerialize(mockDataStream,record);
                ProducerRecord<String,byte[]> MockerProducerRecord = new ProducerRecord<>(mockDataStream,encodedMocker);
                Future<RecordMetadata> future = mockerProducer.send(MockerProducerRecord);
                mockerProducer.flush();
                while (!future.isDone()) {
                    Thread.sleep(500L);
                }
                mockerProducer.close();
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private ArrayList<GenericRecord> getMockerGenericRecord(int numberOfRecords ) throws IOException {
        Schema mockerSchema = new Schema.Parser().parse(ClassLoader.getSystemResourceAsStream("testMock.avsc"));
        //AvroGenerator avroGenerator = new AvroGenerator(mockerSchema,numberOfRecords);
        //return avroGenerator.generateMockMessages();
        AvroMocker avroMocker = new AvroMocker(mockerSchema,numberOfRecords);
        return avroMocker.generateMockMessages();
    }

}
