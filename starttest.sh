#!/bin/bash
LOCAL_IP=$1
echo $LOCAL_IP
javac *.java
[ -e testoutput/ ] || mkdir testoutput
screen -d -m -L testoutput/2121.txt -S p2pHStest1 java MeshPeer $LOCAL_IP 2121
sleep 1
screen -d -m -L testoutput/2222.txt -S p2pHStest2 java MeshPeer $LOCAL_IP 2222 $LOCAL_IP 2121
sleep 1
screen -d -m -L testoutput/2323.txt -S p2pHStest3 java MeshPeer $LOCAL_IP 2323 $LOCAL_IP 2222
echo 'Test started.'
