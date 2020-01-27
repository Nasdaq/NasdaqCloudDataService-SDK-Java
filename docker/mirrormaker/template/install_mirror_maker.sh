#!/bin/bash

script="install_mirror_maker"

function help {
  usage
    echo -e "MANDATORY:"
    echo -e "  -n, --namespace VAL  NAMESPACE of kafka cluster\n"
    echo -e "  -id, --clientId VAL clientId\n"
    echo -e "  -secret, --clientSecret VAL clientSecret\n"
    echo -e "  -h,  --help  Prints this help\n"
  example
}

function usage {
    echo -e "usage: $script MANDATORY [OPTION]\n"
}

function example {
    echo -e "example: $script -n kafka-cluster -id clientId -secret secret"
}

function margs_precheck {
        if [ $2 ] && [ $1 -lt $margs ]; then
                if [ $2 == "--help" ] || [ $2 == "-h" ]; then
                        help
                        exit
                else
                  usage
                        example
                exit 1 # error
                fi
        fi
}

function margs_check {
        if [ $# -lt $margs ]; then
            usage
                example
            exit 1 # error
        fi
}

NAMESPACE=
CLIENTID=
CLIENTSECRET=
while [ "$1" != "" ];
do
   case $1 in
   -n | --namespace )  shift
    NAMESPACE=$1
    ;;
   -id | --clientId ) shift
   CLIENTID=$1
    ;;
   -secret | --clientSecret ) shift
   CLIENTSECRET=$1
    ;;
  -h | --help )        help
                          exit
    ;;
    *)
   echo "$script: illegal option $1"
   usage
   example
  exit 1 # error
   ;;
    esac
    shift
done

margs_check $NAMESPACE $CLIENTID $CLIENTSECRET

echo "Downloading certificate"

wget -O ca.crt $(wget https://clouddataservice.nasdaq.com/api/v1/get-certificate && cat get-certificate | jq -r '.one_time_url')

echo "Creating secret for certificate in namespace "$NAMESPACE

kubectl create secret generic kafka-cluster-cluster-ca-cert -n $NAMESPACE --from-file=ca.crt

echo "Creating secret for clientSecret"

kubectl create secret generic clientsecret -n $NAMESPACE --from-literal=secret=$CLIENTSECRET

sed -i "s/clientId: .*/clientId: $CLIENTID/" kafka-mirror-maker.yaml

echo "Apply mirror-maker to cluster"

kubectl apply -f kafka-mirror-maker.yaml -n $NAMESPACE