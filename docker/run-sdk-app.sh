#!/usr/bin/env sh

OPT=$2
#Function to get Topics
function get_topics {
        if [[ $3 == "-topics" ]]; then
                TOPICS=$4
        else
                TOPICS='.*'
        fi
}

function get_groupids_suffix {
        if [[ $5 == "-groupidsuffix" ]]; then
                group_id_suffix=$6
        else
                group_id_suffix=$(uuidgen)
        fi
}

if [[ $OPT == "mirrormaker" ]]; then
  get_topics $@
  get_groupids_suffix $@
  bash /home/kafka/docker/mirrormaker/run-mirror-maker.sh $TOPICS $group_id_suffix
else
  # Run the user command
  java -Doauth.client.id=$OAUTH_CLIENT_ID -Doauth.client.secret=$OAUTH_CLIENT_SECRET -jar app.jar -kafkaprops /home/kafka/docker/kafka.properties -authprops /home/kafka/docker/auth.properties $@
fi
