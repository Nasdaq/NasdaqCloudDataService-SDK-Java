#!/usr/bin/env sh

file="/truststore/ncdsTrustStore.p12"

# Remove the existing truststore if exists
if [ -f $file ] ; then
   rm $file
fi

# Install Trust Store
java -jar app.jar -opt INSTALLCERTS -path /truststore -pass $JAVAX_NET_SSL_TRUSTSTOREPASSWORD &&

# Run the user command
java -Djavax.net.ssl.trustStore="/truststore/ncdsTrustStore.p12" -Djavax.net.ssl.trustStorePassword=$JAVAX_NET_SSL_TRUSTSTOREPASSWORD -Doauth.client.id=$OAUTH_CLIENT_ID -Doauth.client.secret=$OAUTH_CLIENT_SECRET -jar app.jar -kafkaprops /docker/kafka.properties -authprops /docker/auth.properties $@
