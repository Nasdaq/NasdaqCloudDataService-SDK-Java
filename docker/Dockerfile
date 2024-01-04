FROM maven:3.6-jdk-8 as builder

#Copy in code base
COPY . .

##Build
RUN mvn -B \
     -Dmaven.test.skip=true \
     clean install

### Build Images ###
## SDK app ##
FROM quay.io/strimzi/kafka:0.32.0-kafka-3.3.1 as sdk-app

COPY . /home/kafka

COPY --from=builder /ncdssdk-client/target/ncdssdk-client.jar /home/kafka/app.jar

WORKDIR /home/kafka

ENTRYPOINT ["bash","docker/run-sdk-app.sh"]

CMD ["-opt", "TOPICS"]
