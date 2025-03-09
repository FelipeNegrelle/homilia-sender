#!/bin/bash

cd "$HOME"/documents/pastaDev/java/LaiderLib && mvn clean install
cd "$HOME"/documents/pastaDev/java/homilia-sender && mvn clean install

echo "Deploying homilia-sender to" "$NGINX_IP"

sshpass -p "$NGINX_PASSWORD" scp ./target/homilia-sender-1.0-SNAPSHOT.jar "$NGINX_USER"@"$NGINX_IP":/home/felipe/rotinas/homilia-sender/homilia-sender.jar
sshpass -p "$NGINX_PASSWORD" scp ./properties.json "$NGINX_USER"@"$NGINX_IP":/home/felipe/rotinas/homilia-sender/properties.json

echo "Deployed!"
