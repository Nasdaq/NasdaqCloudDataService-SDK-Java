package com.nasdaq.ncdsclient;

import com.nasdaq.ncdsclient.internal.utils.InstallCertificates;
import com.nasdaq.ncdsclient.news.News;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.errors.WakeupException;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class NCDSSession {

    public static void main(String[] args) throws Exception {
        //Thread.sleep(10000);
        CommandOptions cmd = new CommandOptions(args);
        String testOption = null;
        String topic = null;
        String messageName = null;
        String keyStorePath = null;
        String keyStorePassword = null;
        String numberOfTopMessage = null;
        Long timestamp = null;

        String authPropsFile = null;
        String kafkaPropsFile = null;
        if(cmd.hasOption("-authprops")){
            authPropsFile = cmd.valueOf("-authprops");
        }
        if(cmd.hasOption("-kafkaprops")){
            kafkaPropsFile = cmd.valueOf("-kafkaprops");
        }
        Properties securityCfg = LoadAuthProperties(authPropsFile);
        Properties kafkaConfig = loadKafkaConfig(kafkaPropsFile);

        if(cmd.hasOption("-opt")){
            testOption = cmd.valueOf("-opt");
        }
        else {
            printHelpMessage();
            System.exit(0);
        }

        switch (testOption) {
            case "SCHEMA":
            case "METRICS":
                if(!cmd.hasOption("-topic")){
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                topic = cmd.valueOf("-topic");
                break;
            case "TOP":
                if(!cmd.hasOption("-topic") || !cmd.hasOption("-n")){
                    System.out.println("You must provide -topic and -n (Number of records) for getting top records");
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                if(cmd.hasOption("-timestamp"))
                {
                    try {
                        timestamp = Long.parseLong(cmd.valueOf("-timestamp"));
                    } catch (NumberFormatException e){
                        System.out.println("You must provide timestamp in long format");
                        System.exit(0);
                    }
                }
                topic = cmd.valueOf("-topic");
                numberOfTopMessage = cmd.valueOf("-n");
                break;
            case "GETMSG":
                if(!cmd.hasOption("-msgName") || !cmd.hasOption("-topic")){
                    System.out.println("You must provide -topic and -msgName for getting example message");
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                messageName = cmd.valueOf("-msgName");
                topic = cmd.valueOf("-topic");
                break;
            case "INSTALLCERTS":
                if(!cmd.hasOption("-path") || !cmd.hasOption("-pass")){
                    System.out.println("You must provide -path (Directory path) and -pass (Password) for getting example message");
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                keyStorePath = cmd.valueOf("-path");
                keyStorePassword = cmd.valueOf("-pass");
                break;
            case "CONTSTREAM":
                if(!cmd.hasOption("-topic")){
                    System.out.println("You must provide -topic");
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                if(cmd.hasOption("-timestamp"))
                {
                    try {
                        timestamp = Long.parseLong(cmd.valueOf("-timestamp"));
                    } catch (NumberFormatException e){
                        System.out.println("You must provide timestamp in long format");
                        System.exit(0);
                    }
                }
                topic = cmd.valueOf("-topic");
                break;
            case "NEWS":
                if(!cmd.hasOption("-topic")){
                    System.out.println("You must provide -topic");
                    printHelpMessage();
                    System.exit(0);
                    break;
                }
                topic = cmd.valueOf("-topic");
                break;
            default:
        }


        NCDSClient ncdsClient = null;

        try {
            if  (testOption.equals("TOP")) {
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                int numOfRecords = Math.min(Integer.parseInt(numberOfTopMessage),999);
                if (numOfRecords == 0) {
                    numOfRecords = 10;
                }
                ConsumerRecords<String, GenericRecord> records;
                if (timestamp == null){
                    records = ncdsClient.topMessages(topic);
                }
                else {
                    records = ncdsClient.topMessages(topic, timestamp);
                }
                System.out.println("Top " + numOfRecords + " Records for the Topic:" +  topic );
                if (records != null) {
                    if (records.count() == 0) {
                        System.out.println("No Records Found for the Topic:" +  topic );
                    }
                    else {
                        int i = 0;
                        for (ConsumerRecord<String, GenericRecord> record : records) {
                            if (i < numOfRecords) {
                                System.out.println("key:" + record.key());
                                System.out.println("value :" + record.value().toString());
                            }
                            else {
                                break;
                            }
                            i++;
                        }
                    }
                }
                else {
                    System.out.println("Access to topic is not granted ");
                }
            }
            else if  (testOption.equals("METRICS")) {
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                Map<MetricName, ? extends Metric> metrics = ncdsClient.getMetrics(topic);
                System.out.println("Retrieve the Metrics for the Topic:" +  topic );
                for (Metric metric : metrics.values()) {
                    System.out.println(metric.metricName().toString() + " Value:" + metric.metricValue().toString());

                }
            }
            else if (testOption.equals("SCHEMA")) {
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                // Dump the Schema for the topic
                String schema = ncdsClient.getSchemaForTheTopic(topic);
                System.out.println("Schema for the Topic:" +  topic );
                if  (schema != null) {
                    System.out.println(schema);
                }
                else {
                    System.out.println(" Access to topic is not granted ");
                }
            }
            else if (testOption.equals("GETMSG")) {
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                System.out.println("Finding the message");
                if (kafkaConfig.containsKey("auto.offset.reset") && kafkaConfig.getProperty("auto.offset.reset").equals("latest")){
                    System.out.println("Need to get run GETMSG with `earliest` offset");
                    System.exit(0);
                }
                String msg = ncdsClient.getSampleMessages(topic, messageName);
                if (msg != null) {
                    System.out.println(msg);
                }
                else {
                    System.out.println(" Message Not Found ... ");
                }
            }
            else if (testOption.equals("INSTALLCERTS")){
                try
                {
                    System.out.println("Installing Certificates ");
                    new InstallCertificates(keyStorePath,keyStorePassword).install();
                    System.out.println("Installed certificate at :"+keyStorePath+File.separator+"ncdsTrustStore.p12");
                }catch (Exception e){
                    System.out.println("Error in installing certificates " + e);
                }
            }
            else if (testOption.equals("TOPICS")){
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                String[] topics = ncdsClient.ListTopicsForTheClient();
                System.out.println("List of streams available on Nasdaq Cloud DataService:" );
                for (String topicEntry : topics) {
                    System.out.println(topicEntry);
                }
            }
            else if (testOption.equals("NEWS")){
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                Consumer ncdsmtNewsKafkaConsumer = ncdsClient.NCDSNewsKafkaConsumer(topic);
                System.out.println("Now starting the news!!" );
                try {
                    while (true) {
                        //ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMinutes(Integer.parseInt("1")));
                        ConsumerRecords<String, GenericRecord> records = ncdsmtNewsKafkaConsumer.poll(Long.MAX_VALUE);
                        if (records.count() == 0) {
                            System.out.println("No Records Found for the Topic:" + topic);
                        }
                        for (ConsumerRecord<String, GenericRecord> record : records) {
                            System.out.println("-----------------------------------------------------------------------------------------------");
                            System.out.println("News :" + News.newsBuilder(record.value()).toString());
                            System.out.println("-----------------------------------------------------------------------------------------------");
                        }
                        ncdsmtNewsKafkaConsumer.commitAsync();
                    }
                } catch (WakeupException e) {
                    // ignore for shutdown
                    System.out.println("Error in cont stream");
                } finally {
                    ncdsmtNewsKafkaConsumer.close();
                }

            }
            else if (testOption.equals("CONTSTREAM")) {
                ncdsClient = new NCDSClient(securityCfg,kafkaConfig);
                Consumer consumer;
                if (timestamp == null){
                    consumer = ncdsClient.NCDSKafkaConsumer(topic);
                }
                else {
                    consumer = ncdsClient.NCDSKafkaConsumer(topic, timestamp);
                }
                try {
                    while (true) {
                        //ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMinutes(Integer.parseInt("1")));
                        ConsumerRecords<String, GenericRecord> records = consumer.poll(Long.MAX_VALUE);
                        if (records.count() == 0) {
                            System.out.println("No Records Found for the Topic:" + topic);
                        }
                        for (ConsumerRecord<String, GenericRecord> record : records) {
                            System.out.println("value :" + record.value().toString());
                        }
                        consumer.commitAsync();
                    }
                } catch (WakeupException e) {
                    // ignore for shutdown
                    System.out.println("Error in cont stream");
                } finally {
                    consumer.close();
                }
            }
            else {
                //No valid option provided
                printHelpMessage();
                System.exit(0);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static Properties LoadAuthProperties(String authPropsFile) throws Exception {
        Properties cfg = new Properties();
        InputStream inputStream;
        try {
                if (authPropsFile != null) {
                    inputStream = new FileInputStream(new File(authPropsFile));
                }
                else {
                    inputStream = ClassLoader.getSystemResourceAsStream("clientAuthentication-config.properties");
                }

                if (inputStream != null) {
                    cfg.load(inputStream);
                }
                else {
                    cfg = null;
                }
        }
        catch (Exception e) {
            throw e;
        }
        return cfg;
    }
    public static Properties loadKafkaConfig(String kafkaCfgFile) throws Exception {
        Properties cfg = new Properties();
        InputStream inputStream;
        try {
            if (kafkaCfgFile != null){
                inputStream = new FileInputStream(new File(kafkaCfgFile));
            }
            else {
                inputStream = ClassLoader.getSystemResourceAsStream("kafka-config.properties");
            }
            if (inputStream == null) {
                System.out.println("kafka-config.properties: Unable to produce input Stream ");
                throw new Exception ("kafka-config.properties: Unable to produce input Stream " );
            }
            cfg.load(inputStream);
        }
        catch (Exception e) {
            throw e;
        }
        return cfg;
    }


    private static void printHelpMessage(){

        System.out.println("-opt -- Provide the operation you want to perform \n" +
                              "        * TOP - View the top nnn records in the Topic/Stream\n"+
                              "        * SCHEMA - Display the Schema for the topic\n"+
                              "        * METRICS - Display the Metrics for the topic\n"+
                              "        * TOPICS - List the eligible topics for the client\n"+
                              "        * GETMSG - Get one example message for the\n"+
                              "        * INSTALLCERTS - Install certificate to keystore\n"+
                              "        * CONTSTREAM   - Retrieve continuous stream  \n"+
                              "        * NEWS - Retrieve news stream               \n"+
                "        * HELP - help \n"+
                            "-topic -- Provide topic for selected option         --- REQUIRED for TOP,SCHEMA,METRICS,GETMSG,CONTSTREAM and NEWS  \n"+
                            "-authprops -- Provide Client Properties File path   --- For using different set of Client Authentication Properties \n"+
                            "-kafkaprops -- Provide Kafka Properties File path   --- For using different set of Kafka Properties \n"+
                            "-n -- Provide number of messages to retrieve        --- REQUIRED for TOP \n"+
                            "-msgName -- Provide name of message based on schema --- REQUIRED for GETMSG \n"+
                            "-path -- Provide the path for key store             --- REQUIRED for INSTALLCERTS \n"+
                            "-pass -- Provide the password for key store         --- REQUIRED for INSTALLCERTS \n"+
                            "-timestamp -- Provide timestamp in milliseconds     --- OPTIONAL for TOP and CONTSTREAM \n"
        );
    }
}
