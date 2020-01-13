FROM maven:3.6-jdk-8 as builder

#Copy in code base
COPY . .

##Build
RUN mvn -B \
     -Dmaven.test.skip=true \
     clean install

### Build Images ###
## SDK app ##
FROM java:8 as sdk-app

COPY . .

COPY --from=builder /ncdssdk-client/target/ncdssdk-client.jar app.jar

RUN mkdir /truststore

RUN chmod 777 /docker/run-sdk-app.sh

ENV JAVAX_NET_SSL_TRUSTSTORE=/truststore/ncdsTrustStore.p12

ENTRYPOINT ["bash","/docker/run-sdk-app.sh"]

CMD ["-opt", "TOPICS"]