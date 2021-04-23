FROM maven:3.6-jdk-8 as builder

#Copy in code base
COPY . .

##Build
RUN mvn -B \
     -Dmaven.test.skip=true \
     clean install

### Build Images ###
## SDK app ##
FROM strimzi/kafka:0.20.0-kafka-2.6.0 as sdk-app

COPY . /home/kafka

COPY --from=builder /ncdssdk-client/target/ncdssdk-client.jar /home/kafka/app.jar

WORKDIR /home/kafka

RUN mkdir truststore

ENV JAVAX_NET_SSL_TRUSTSTORE=truststore/ncdsTrustStore.p12

ENTRYPOINT ["bash","docker/run-sdk-app.sh"]

CMD ["-opt", "TOPICS"]
