# Nasdaq Cloud Data Service - Kafka mirroring with MirrorMaker
Kafka's mirroring feature makes it possible to maintain a replica of an existing Kafka cluster. The tool uses a Kafka consumer to consume messages from the source cluster, and re-publishes those messages to the local (target) cluster using an embedded Kafka producer. (https://kafka.apache.org/documentation.html#basic_ops_mirror_maker)

## Running Mirror Maker
This example shows how to setup standalone Mirror Maker instance application. 

#### Producer Configuration (Target Cluster)
- The producer is the part of Mirror Maker that uses the data read by the and replicates it to the destination cluster.
- Update the producer.properties based target cluster. (https://github.com/Nasdaq/CloudDataService/tree/master/docker/mirrormaker/producer.properties)
- Make sure the bootstrap.server IPs, truststore location if using SSL, and password are correct.

#### Creating docker build
- Run docker build in the project home directory. (https://github.com/Nasdaq/CloudDataService) 

```
docker build -f docker/Dockerfile . -t sdk-app --no-cache
```

#### Running mirror maker
- Run mirror maker for given topics list.
- Replace client id(`{client-id-value}`) and client secret(`{client-secret-value}`) provided during on-boarding from Nasdaq team. Also, provide the password (`{truststore-pass}`) for java truststore.

```
docker run -e "OAUTH_CLIENT_ID={client-id-value}" -e "OAUTH_CLIENT_SECRET={client-secret-value} -e "JAVAX_NET_SSL_TRUSTSTOREPASSWORD={truststore-pass}" sdk-app:latest -opt mirrormaker -topics NLSUTP.stream
```

