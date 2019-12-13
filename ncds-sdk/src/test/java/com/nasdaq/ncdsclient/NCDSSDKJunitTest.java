package com.nasdaq.ncdsclient;

import junit.framework.Assert;
import com.nasdaq.ncdsclient.utils.NCDSTestUtil;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NCDSSDKJunitTest {

    static NCDSTestUtil ncdsTestUtil;

    static {
        try {
            ncdsTestUtil = new NCDSTestUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(1)
    void testNCDSClient() {
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            Assertions.assertNotNull(ncdsClient);
        } catch (Exception e){
            new AssertionError("Error");
            System.out.println(e.getMessage());
        }

    }

    @Test
    @Order(2)
    void testListTopicsForTheClient() {
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            String[] topics = ncdsClient.ListTopicsForTheClient();
            Collections.sort(Arrays.asList(topics));
            String[] addedTopics = ncdsTestUtil.getAddedTopics();
            Collections.sort(Arrays.asList(addedTopics));
            Assert.assertEquals(Arrays.asList(topics),Arrays.asList(addedTopics));
        } catch (Exception e) {
            Assert.fail();
            System.out.println(e.getMessage());
        }
    }


    @Test
    @Order(3)
    void testgetSchemaForTheTopic(){
        try {
            NCDSClient ncdsClient = new NCDSClient(null, null);
            String topic = "GIDS";
            String schemaFromSDK = ncdsClient.getSchemaForTheTopic(topic);
            String schemaFile = "testGIDS.avsc";
            String schemaFromFile = ncdsTestUtil.getSchemaForTopic(schemaFile);

            Assert.assertEquals(schemaFromSDK,schemaFromFile);
        } catch (Exception e) {
            Assert.fail();
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testInsertion(){
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
            Assert.assertEquals(mockRecords,mockRecordsFromKafka);
        } catch (Exception e) {
            Assert.fail();
            e.printStackTrace();
        }
    }
}
