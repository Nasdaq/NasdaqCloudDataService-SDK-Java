# Nasdaq Cloud Data Service - Kafka mirroring with MirrorMaker
This tool uses a Kafka consumer to consume messages from the source cluster, and re-publishes those messages to the local (target) cluster using an embedded Kafka producer. (https://kafka.apache.org/documentation.html#basic_ops_mirror_maker)

## Running Mirror Maker on docker
This example shows how to setup standalone Mirror Maker instance application.

#### Consumer Configuration (NCDS cluster)
- Replace example `bootstrap.servers` property in the kafka.properties file (https://github.com/Nasdaq/CloudDataService/blob/master/docker/mirrormaker/consumer.properties) with provided values during on-boarding.

#### Producer Configuration (Target Cluster)
- The producer is the part of Mirror Maker that uses the data read by the and replicates it to the destination cluster.
- Update the producer.properties based target cluster. (https://github.com/Nasdaq/CloudDataService/tree/master/docker/mirrormaker/producer.properties)
- Make sure the bootstrap.server IPs, truststore location if using SSL, and password are correct.

#### Group Id Suffix
- User can customize the group id suffix (property name : `groupidsuffix`) 
  - If custom suffix is reused then consumer will start where it left off on restart
  - If you don't pass a custom suffix, the SDK will create a new group id and start from the earliest message available on topic.
  
#### Creating docker build
- Run `docker build` in the project home directory. (https://github.com/Nasdaq/CloudDataService)

```
docker build -f docker/Dockerfile . -t sdk-app --no-cache
```

#### Running mirror maker
- Run mirror maker for given topics list.
- Replace client id(`{client-id-value}`) and client secret(`{client-secret-value}`) provided during on-boarding from Nasdaq team.

```
docker run -e "OAUTH_CLIENT_ID={client-id-value}" -e "OAUTH_CLIENT_SECRET={client-secret-value}" sdk-app:latest -opt mirrormaker -topics NLSUTP.stream
```

example with custom groupid suffix

```
docker run -e "OAUTH_CLIENT_ID={client-id-value}" -e "OAUTH_CLIENT_SECRET={client-secret-value}" sdk-app:latest -opt mirrormaker -topics NLSUTP.stream -groupidsuffix mycustomsuffix
```

## Deploying Kafka Mirror Maker on Strimzi kafka cluster
Strimzi is an open source project that provides container images and operators for running Apache Kafka on Kubernetes.(https://github.com/strimzi/strimzi-kafka-operator)
The Cluster Operator deploys one or more Kafka Mirror Maker replicas to replicate data between Kafka clusters.

### Prerequisites
- Before deploying Kafka Mirror Maker, the Cluster Operator must be deployed.

### Deploying mirror maker
- Create Kubernetes secret for Oauth Client Secret.
- Update Oauth Client Id in kafka-mirror-maker.yaml.
- Create a Kafka Mirror Maker cluster from the command-line:
  ```kubectl apply -f mirrormaker/template/kafka-mirror-maker.yaml```

Provided example script `install_mirror_maker.sh` to deploy the mirror maker in your cluster.