package com.nasdaq.ncdsclient;

import com.nasdaq.ncdsclient.utils.NCDSTestUtil;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NCDSSDKJunitTest {

    static NCDSTestUtil ncdsTestUtil;

    static {
        try {
            ncdsTestUtil = new NCDSTestUtil();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Test
    @Order(1)
    public void testNCDSClient() {
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            assertNotNull(ncdsClient);
        } catch (Exception e){
            new AssertionError("Error");
            System.out.println(e.getMessage());
        }

    }

    @Test
    @Order(2)
    public void testListTopicsForTheClient() {
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            String[] topics = ncdsClient.ListTopicsForTheClient();
            Collections.sort(Arrays.asList(topics));
            String[] addedTopics = ncdsTestUtil.getAddedTopics();
            Collections.sort(Arrays.asList(addedTopics));
            assertEquals(Arrays.asList(topics),Arrays.asList(addedTopics));
        } catch (Exception e) {
            Assertions.fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    @Order(3)
    public void testgetSchemaForTheTopic(){
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            String topic = "GIDS";
            String schemaFromSDK = ncdsClient.getSchemaForTheTopic(topic);
            String schemaFile = "testGIDS.avsc";
            String schemaFromFile = ncdsTestUtil.getSchemaForTopic(schemaFile);

            assertEquals(schemaFromSDK,schemaFromFile);
        } catch (Exception e) {
            Assertions.fail();
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void testInsertion(){
        ArrayList<GenericRecord> mockRecords = ncdsTestUtil.getMockMessages();
        ArrayList<GenericRecord> mockRecordsFromKafka = new ArrayList<>();

        String topic = "MOCK";
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            ConsumerRecords<String, GenericRecord> records = ncdsClient.topMessages(topic);
            Iterator<ConsumerRecord<String, GenericRecord>> iteratorMocker =records.iterator();
            while (iteratorMocker.hasNext())
            {
                mockRecordsFromKafka.add(iteratorMocker.next().value());
            }
            //mockRecordsFromKafka.remove(0);
            assertEquals(mockRecords,mockRecordsFromKafka);
        } catch (Exception e) {
            Assertions.fail();
            e.printStackTrace();
        }
    }
}
