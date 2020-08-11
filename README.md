# Nasdaq Cloud Data Service (NCDS)

Nasdaq Cloud Data Service (NCDS) provides a modern and efficient method of delivery for realtime exchange data and other financial information. Data is made available through a suite of APIs, allowing for effortless integration of data from disparate sources, and a dramatic reduction in time to market for customer-designed applications. The API is highly scalable, and robust enough to support the delivery of real-time exchange data.

This repository provides an SDK for developing applications to access the NCDS API. While the SDK is open source, connecting to the API does require credentials, which are provided by Nasdaq during an on-boarding process.

# Products Currently Available

- Nasdaq Basic (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/NasdaqBasic-Cloud.pdf)
- Nasdaq Last Sale+ (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/NLSPlus-cloud.pdf) 
- Nasdaq TotalView (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/Totalview-ITCH-cloud.pdf)
- Global Index Data Service (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/GIDS_Cloud.pdf)
- Nasdaq Data On Demand (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/DOD-SIP-cloud.pdf)
- Nasdaq Canada Basic (http://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/Nasdaq-Basic-Canada-Cloud-Specification.pdf)
- Financial News (http://nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/MTNewswires-cloud.pdf)
- Nasdaq Smart Options (http://nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/NCDSSmartOptions.pdf)


# Table of Contents
 - [Getting Started](#Getting-Started)
 - [Using the SDK](#Using-The-SDK)
 - [Documentation](#Documentation)
 - [Docker](#Docker)
 - [Contributing](#Contributing)
 - [License](#License)
 

## Getting Started

### Pre-requisites 

- OpenJDK 8
- Maven 3

### Get the SDK

Clone the repository: ```git clone https://github.com/Nasdaq/CloudDataService```
  - Run ```mvn install``` to build the library, test, javadoc and source jars and install to your local Maven repository.
  - Run ```mvn javadoc:javadoc``` to build the documentation.

### Retrieving certificates 

Run jar `ncdssdk-client/target/ncdssdk-client.jar` with arguments, which take path and password (minimum 6 characters) for keystore

For example: 

```java -jar ncdssdk-client.jar -opt INSTALLCERTS -path /my/trusted/store/ncdsinstallcerts -pass my_password```

### Stream configuration

  Replace example stream properties in the file **kafka-config.properties** (https://github.com/Nasdaq/CloudDataService/blob/master/ncdssdk-client/src/main/resources/kafka-config.properties) with provided values during on-boarding.

 Required kafka configuration 
    
```properties
bootstrap.servers={streams_endpoint_url}:9094 #without the protocol 
```
    
  For optional consumer configurations see: https://kafka.apache.org/0100/javadoc/org/apache/kafka/clients/consumer/ConsumerConfig.html
  
  For example:  
```properties
request.timeout.ms=20000
retry.backoff.ms=500
max.poll.records=2000
```
 
### Client Authentication configuration

   Replace example client authentication properties in the file **clientAuthentication-config.properties** (https://github.com/Nasdaq/CloudDataService/blob/master/ncdssdk-client/src/main/resources/clientAuthentication-config.properties) with valid credentials provided during on-boarding.
   
```properties
oauth.token.endpoint.uri=https://{auth_endpoint_url}/auth/realms/demo/protocol/openid-connect/token
oauth.client.id=client
oauth.client.secret=client-secret
oauth.username.claim=preferred_username
```   
   Update the path to your local Keystore  

```properties
javax.net.ssl.trustStore=/my/trusted/store/ncdsinstallcerts/ncdsTrustStore.p12  
javax.net.ssl.trustStorePassword=my_password
javax.net.ssl.trustStoreType=PKCS12
```  

### Create NCDS Session Client

  Run `mvn clean install` command in ncdssdk-client project (https://github.com/Nasdaq/CloudDataService/tree/master/ncdssdk-client). It will generate the jar file in target file.
  How to run the jar:
```
-opt -- Provide the operation you want to perform \n" +
  "        * TOP - View the top nnn records in the Topic/Stream\n"+
  "        * SCHEMA - Display the Schema for the topic\n"+
  "        * METRICS - Display the Metrics for the topic\n"+
  "        * TOPICS - List of streams available on Nasdaq Cloud DataService\n"+
  "        * GETMSG - Get one example message for the\n"+
  "        * INSTALLCERTS - Install certificate to keystore\n"+
  "        * CONTSTREAM   - Retrieve continuous stream  \n"+
  "        * NEWS - Get stream for Pro Global news stream\n"+
  "        * HELP - help \n"+
"-topic -- Provide topic for selected option         --- REQUIRED for TOP,SCHEMA,METRICS,GETMSG,CONTSTREAM and NEWS \n"+
"-authprops -- Provide Client Properties File path     --- For using different set of Client Authentication Properties \n"+
"-kafkaprops -- Provide Kafka Properties File path   --- For using different set of Kafka Properties \n"+
"-n -- Provide number of messages to retrieve        --- REQUIRED for TOP \n"+
"-msgName -- Provide name of message based on schema --- REQUIRED for GETMSG \n"+
"-path -- Provide the path for key store             --- REQUIRED for INSTALLCERTS \n"+
"-pass -- Provide the password for key store         --- REQUIRED for INSTALLCERTS \n"+
"-timestamp -- Provide timestamp in milliseconds     --- OPTIONAL for TOP and CONTSTREAM \n"
```
 
  Few examples to use jar:
  
  Get first 100 records for given stream
  
  ```java -jar ncdssdk-client.jar -opt TOP -n 100 -topic GIDS```
  
  Get all available streams
  
  ```java -jar ncdssdk-client.jar -opt TOPICS```  
 

## Using the SDK

  ### Getting list of data stream available
  List all available data stream for the user.
```java
// Example1.java
NCDSClient ncdsClient = new NCDSClient();
String[] topics = ncdsClient.ListTopicsForTheClient();
System.out.println("Entitled DataSet Topics:" );
for (String topicEntry : topics) {
    System.out.println(topicEntry);
}
```

 Example output:
```
List of streams available on Nasdaq Cloud DataService:
GIDS
NLSUTP
NLSCTA
```

  ### Getting schema for the stream
  This methods returns the schema for the stream in Apache Avro format (https://avro.apache.org/docs/current/spec.html).  
```java
// Example2.java
NCDSClient ncdsClient = new NCDSClient();
String topic = "GIDS";
String schema = ncdsClient.getSchemaForTheTopic(topic);
System.out.println(schema);
```
  Example output:
```
[ {
    "type" : "record",
    "name" : "SeqCommoditySummary",
    "namespace" : "com.nasdaq.marketdata.sequencer.applications.datafeed.gids20feed.messaging",
    "fields" : [ {
      "name" : "SoupPartition",
      "type" : "int"
    }, {
      "name" : "SoupSequence",
      "type" : "long"
    }, {
      "name" : "msgType",
      "type" : "string"
    }, {
      "name" : "timeStamp",
      "type" : "int"
    }, {
      "name" : "fpType",
      "type" : "string"
    }, {
      "name" : "brand",
      "type" : "string"
    }, {
      "name" : "series",
      "type" : "string"
    }, {
      "name" : "instrumentID",
      "type" : "string"
    }, {
      "name" : "summaryType",
      "type" : "string"
    }, {
      "name" : "sodValue",
      "type" : "long"
    }, {
      "name" : "high",
      "type" : "long"
    },
}........
....]
```

### Get first 10 messages of the stream
 This returns the first 10 available messages of the stream.
```java
// Example2.java
NCDSClient ncdsClient = new NCDSClient();
String topic="NLSCTA";
ConsumerRecords<String, GenericRecord> records = ncdsClient.topMessages(topic);
for (ConsumerRecord<String, GenericRecord> record : records) {
    System.out.println("key:" + record.key());
    System.out.println("value:" + record.value().toString());
}
```
 Example output:
```
key:1
value:{"SoupPartition": 0, "SoupSequence": 1, "trackingID": 7238625218217, "msgType": "S", "event": "O"}
key:2
value:{"SoupPartition": 0, "SoupSequence": 2, "trackingID": 11231714567789, "msgType": "R", "symbol": "A       ", "marketClass": "N", "fsi": " ", "roundLotSize": 100, "roundLotOnly": "N", "issueClass": "C", "issueSubtype": "Z ", "authenticity": "P", "shortThreshold": "N", "ipo": " ", "luldTier": "1", "etf": "N", "etfFactor": 0, "inverseETF": "N", "compositeId": "BBG000C2V3D6"}
key:3
value:{"SoupPartition": 0, "SoupSequence": 3, "trackingID": 11231714567789, "msgType": "G", "symbol": "A       ", "securityClass": "N", "adjClosingPrice": 766400}
key:4
value:{"SoupPartition": 0, "SoupSequence": 4, "trackingID": 11231714628669, "msgType": "R", "symbol": "AA      ", "marketClass": "N", "fsi": " ", "roundLotSize": 100, "roundLotOnly": "N", "issueClass": "C", "issueSubtype": "Z ", "authenticity": "P", "shortThreshold": "N", "ipo": " ", "luldTier": "1", "etf": "N", "etfFactor": 1, "inverseETF": "N", "compositeId": "BBG00B3T3HD3"}
key:5
value:{"SoupPartition": 0, "SoupSequence": 5, "trackingID": 11231714628669, "msgType": "G", "symbol": "AA      ", "securityClass": "N", "adjClosingPrice": 225300}
key:6
value:{"SoupPartition": 0, "SoupSequence": 6, "trackingID": 11231714675278, "msgType": "R", "symbol": "AAAU    ", "marketClass": "P", "fsi": " ", "roundLotSize": 100, "roundLotOnly": "N", "issueClass": "Q", "issueSubtype": "I ", "authenticity": "P", "shortThreshold": "N", "ipo": " ", "luldTier": "2", "etf": "Y", "etfFactor": 1, "inverseETF": "N", "compositeId": "BBG00LPXX872"}
key:7
value:{"SoupPartition": 0, "SoupSequence": 7, "trackingID": 11231714675278, "msgType": "G", "symbol": "AAAU    ", "securityClass": "P", "adjClosingPrice": 145600}
key:8
value:{"SoupPartition": 0, "SoupSequence": 8, "trackingID": 11231714764805, "msgType": "R", "symbol": "AADR    ", "marketClass": "P", "fsi": " ", "roundLotSize": 100, "roundLotOnly": "N", "issueClass": "Q", "issueSubtype": "I ", "authenticity": "P", "shortThreshold": "N", "ipo": " ", "luldTier": "2", "etf": "Y", "etfFactor": 0, "inverseETF": "N", "compositeId": "BBG000BDYRW6"}
key:9
value:{"SoupPartition": 0, "SoupSequence": 9, "trackingID": 11231714764805, "msgType": "G", "symbol": "AADR    ", "securityClass": "P", "adjClosingPrice": 499000}
key:10
value:{"SoupPartition": 0, "SoupSequence": 10, "trackingID": 11231714853049, "msgType": "R", "symbol": "AAMC    ", "marketClass": "A", "fsi": " ", "roundLotSize": 100, "roundLotOnly": "N", "issueClass": "C", "issueSubtype": "Z ", "authenticity": "P", "shortThreshold": "N", "ipo": " ", "luldTier": "2", "etf": "N", "etfFactor": 0, "inverseETF": "N", "compositeId": "BBG003PNL136"}    
```

### Get first 10 messages of the stream from given timestamp
 This returns the first 10 available messages of the stream from given timestamp in milliseconds since the UNIX epoch.
```java
// Example3.java
NCDSClient ncdsClient = new NCDSClient();
String topic="NLSCTA";
Long timestamp = 1590084445610L; // This is timestamp in millseconds from UNIX epoch. (https://currentmillis.com/)
ConsumerRecords<String, GenericRecord> records = ncdsClient.topMessages(topic, timestamp);
for (ConsumerRecord<String, GenericRecord> record : records) {
    System.out.println("key:" + record.key());
    System.out.println("value:" + record.value().toString());
}
```
 Example output:
 ```
Offset: 105834100
Top 10 Records for the Topic:NLSCTA
key:9362630
value :{"SoupPartition": 0, "SoupSequence": 9362630, "trackingID": 50845551492208, "msgType": "T", "marketCenter": "L", "symbol": "SIVR    ", "securityClass": "P", "controlNumber": "0000A2MLOB", "price": 164797, "size": 1, "saleCondition": "@  o", "cosolidatedVolume": 520174}
key:9362631
value :{"SoupPartition": 0, "SoupSequence": 9362631, "trackingID": 50845557908136, "msgType": "T", "marketCenter": "Q", "symbol": "TJX     ", "securityClass": "N", "controlNumber": "   8358213", "price": 540300, "size": 100, "saleCondition": "@   ", "cosolidatedVolume": 16278768}
key:9362632
value :{"SoupPartition": 0, "SoupSequence": 9362632, "trackingID": 50845565203932, "msgType": "T", "marketCenter": "L", "symbol": "CMI     ", "securityClass": "N", "controlNumber": "0000A2MLOC", "price": 1579900, "size": 100, "saleCondition": "@   ", "cosolidatedVolume": 568622}
key:9362633
value :{"SoupPartition": 0, "SoupSequence": 9362633, "trackingID": 50845565791061, "msgType": "T", "marketCenter": "L", "symbol": "UTI     ", "securityClass": "N", "controlNumber": "0000A2MLOD", "price": 70150, "size": 64, "saleCondition": "@  o", "cosolidatedVolume": 151359}
key:9362634
value :{"SoupPartition": 0, "SoupSequence": 9362634, "trackingID": 50845566628604, "msgType": "T", "marketCenter": "L", "symbol": "UFS     ", "securityClass": "N", "controlNumber": "0000A2MLOE", "price": 203660, "size": 24, "saleCondition": "@  o", "cosolidatedVolume": 664962}
key:9362635
value :{"SoupPartition": 0, "SoupSequence": 9362635, "trackingID": 50845569154140, "msgType": "T", "marketCenter": "L", "symbol": "KR      ", "securityClass": "N", "controlNumber": "0000A2MLOF", "price": 320350, "size": 100, "saleCondition": "@   ", "cosolidatedVolume": 4054473}
key:9362636
value :{"SoupPartition": 0, "SoupSequence": 9362636, "trackingID": 50845577944984, "msgType": "T", "marketCenter": "L", "symbol": "PAGP    ", "securityClass": "N", "controlNumber": "0000A2MLOG", "price": 98350, "size": 100, "saleCondition": "@   ", "cosolidatedVolume": 1557084}
key:9362637
value :{"SoupPartition": 0, "SoupSequence": 9362637, "trackingID": 50845588007117, "msgType": "T", "marketCenter": "L", "symbol": "LUV     ", "securityClass": "N", "controlNumber": "0000A2MLOH", "price": 297413, "size": 4, "saleCondition": "@  o", "cosolidatedVolume": 16791899}
key:9362638
value :{"SoupPartition": 0, "SoupSequence": 9362638, "trackingID": 50845596356365, "msgType": "T", "marketCenter": "L", "symbol": "M       ", "securityClass": "N", "controlNumber": "0000A2MLOI", "price": 54000, "size": 10, "saleCondition": "@  o", "cosolidatedVolume": 39273663}
key:9362639
value :{"SoupPartition": 0, "SoupSequence": 9362639, "trackingID": 50845600594567, "msgType": "T", "marketCenter": "L", "symbol": "TTM     ", "securityClass": "N", "controlNumber": "0000A2MLOJ", "price": 56000, "size": 400, "saleCondition": "@   ", "cosolidatedVolume": 1293244}
```

### Get example message from stream
 Print message to the console for given message name.
```java
NCDSClient ncdsClient = new NCDSClient();
String topic="GIDS"
ncdsClient.getSampleMessages(topic, "SeqIndexDirectory");
```
 Example output:
 ```
 {"SoupPartition": 0, "SoupSequence": 193, "msgType": "R", "timeStamp": 224140137, "instrumentID": "NQJP3700LMCAD     ", "disseminationFlag": "Y", "fpType": "I", "brand": "NQ", "series": "NQG", "strategy": "SEC", "assetType": "EQ", "marketCapSize": "X", "currency": "CAD", "geography": "JP  ", "settlementType": " ", "calculationMethod": "PR ", "state": "A", "indexUsage": "L", "schedule": "ASI", "frequency": "ODCL", "numberOfIssueParticipation": 23, "baseValue": 100000000000000, "baseDate": 20140111, "instrumentName": "NASDAQ Japan Psnl & Hhld Goods Lg Md Cap CAD"}
```

### Get Continuous stream
```java
NCDSClient ncdsClient = new NCDSClient();
String topic="GIDS"
Consumer consumer = ncdsClient.NCDSKafkaConsumer(topic);
while (true) {
    ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMinutes(Integer.parseInt("1")));
    if (records.count() == 0) {
        System.out.println("No Records Found for the Topic:" + topic);
    }
    for (ConsumerRecord<String, GenericRecord> record : records) {
        System.out.println("value :" + record.value().toString());
    }
}
```

### Get News stream
```java
NCDSClient ncdsClient = new NCDSClient();
String topic="NEWS-PRO-GLOBAL"
Consumer consumer = ncdsClient.NCDSNewsKafkaConsumer(topic);
while (true) {
    ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMinutes(Integer.parseInt("1")));
    if (records.count() == 0) {
        System.out.println("No Records Found for the News");
    }
    for (ConsumerRecord<String, GenericRecord> record : records) {
        System.out.println("News :" + News.newsBuilder(record.value()).toString());
    }
}
```

 Example output:
```-----------------------------------------------------------------------------------------------
   News :ReleaseTime: 2020/04/03 14:40:00
   TransmissionID: A2136726
   RevisionID: 0
   Retract: FALSE
   StoryType:
   TechnicalStory: FALSE
   metaDataNode:
   STOK.MN
   EARN.MN
   USUS.MN
   NOAM.MN
   INBY.MN
   INSL.MN
   FOR4.MN
   EECH.MN
   
   HeadLine: {Headlines}
   Body:
   {Body}
   Tickers: {tickers}
   CopyRight: Copyright © 2020 MT Newswires, All Rights reserved. Data provided by UpTick Data Technologies.
   ISIN: null
   ArticleImage: null
   -----------------------------------------------------------------------------------------------

```

### Example syntax to run the Client Jar based on this SDK

1. To list of streams available on Nasdaq Cloud DataService
 
 ```java -jar ncdssdk-client.jar -opt TOPICS```
 
2. To display the schema for the given topic
 
 ```java -jar ncdssdk-client.jar -opt SCHEMA -topic NLSUTP```
 
3. To dump top n records from the given topic
 
 ```java -jar ncdssdk-client.jar -opt TOP -n 10 -topic NLSUTP```
 
4. To use client based specific authorization file instead of using from the resources of client code base

 ```java -jar ncdssdk-client.jar -opt TOP -n 10 -topic NLSUTP -authprops clntauth.properties```
 
5. To use the specific kafka properties instead of using the kafka properties from the resources of the client base code

  ```java -jar ncdssdk-client.jar -opt TOP -n 10 -topic NLSUTP -kafkaprops kafkaprops.properties```
  
6. To use the specific client based authorization file and specific kafka properties file

  ```java -jar ncdssdk-client.jar -opt TOP -n 10 -topic NLSUTP -authprops clntauth.properties -kafkaprops kafkaprops.properties```
  
7. To display a specific message type

  ```java -jar ncdssdk-client.jar -opt GETMSG -topic UTPBIN-UF30 -msgName SeqTradeLong```
  
8. To install the certificates

  ```java -jar ncdssdk-client.jar -opt INSTALLCERTS -path /home/ec2-user/testInstallCerts -pass testuser```

9. To dump top n records from the given topic from given timestamp in milliseconds since the UNIX epoch

  ```java -jar ncdssdk-client.jar -opt TOP -n 10 -topic NLSUTP -timestamp 1590084445610 ```

 

## Documentation 
 
   An addition to the example application, there is extra documentation at the package and class level within the JavaDocs, which are located in project ```https://github.com/Nasdaq/CloudDataService/tree/master/ncds-sdk/docs```
   
   If you make an update, you can run `mvn javadocs:javadocs` to update documents.

## Docker
   
   Docker images are already configured to run the SDK. View the instructions (https://github.com/Nasdaq/CloudDataService/blob/master/docker/README.md)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

Code and documentation released under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
