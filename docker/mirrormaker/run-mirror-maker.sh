#!/usr/bin/env sh

export OAUTH_TOKEN_ENDPOINT_URI="https://clouddataservice.auth.nasdaq.com/auth/realms/pro-realm/protocol/openid-connect/token"
export OAUTH_CLIENT_ID="$OAUTH_CLIENT_ID"
export OAUTH_CLIENT_SECRET=$OAUTH_CLIENT_SECRET
export OAUTH_USERNAME_CLAIM=preferred_username

topics=$1
group_id_suffix=$2

groupid="$OAUTH_CLIENT_ID$group_id_suffix"
echo $groupid

#update the client Id in consumer
sed -i "s/group.id=/group.id=$groupid/" /home/kafka/docker/mirrormaker/consumer.properties

/opt/kafka/bin/kafka-mirror-maker.sh --consumer.config /home/kafka/docker/mirrormaker/consumer.properties --producer.config /home/kafka/docker/mirrormaker/producer.properties --num.streams 3 --whitelist $topics
