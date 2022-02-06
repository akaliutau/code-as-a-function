#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "usage: send_local_message.sh message"
fi
msg64="$(echo $1 | base64)"

curl localhost:8080 -H "Content-Type: application/json" -d "{\"data\":\"$msg64\"}"