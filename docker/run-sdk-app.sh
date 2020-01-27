#!/usr/bin/env sh

file="/home/kafka/truststore/ncdsTrustStore.p12"

# Remove the existing truststore if exists
if [ -f $file ] ; then
   rm $file
fi

OPT=$2

#Function to get Topics
function get_topics {
        if [ $3 == "-topics" ]; then
                TOPICS=$4
        else
                TOPICS='.*'
        fi
}

# Install Trust Store
java -jar app.jar -opt INSTALLCERTS -path /home/kafka/truststore -pass $JAVAX_NET_SSL_TRUSTSTOREPASSWORD &&

if [ $OPT == "mirrormaker" ]; then
  get_topics $@
  bash /home/kafka/docker/mirrormaker/run-mirror-maker.sh $TOPICS
else
  # Run the user command
  java -Djavax.net.ssl.trustStore="/home/kafka/truststore/ncdsTrustStore.p12" -Djavax.net.ssl.trustStorePassword=$JAVAX_NET_SSL_TRUSTSTOREPASSWORD -Doauth.client.id=$OAUTH_CLIENT_ID -Doauth.client.secret=$OAUTH_CLIENT_SECRET -jar app.jar -kafkaprops /home/kafka/docker/kafka.properties -authprops /home/kafka/docker/auth.properties $@
fi