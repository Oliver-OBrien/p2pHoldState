#!/bin/bash
javac *.java
[ -e testoutput/ ] || mkdir testoutput
screen -d -m -L testoutput/2121.txt -S p2pHStest1 java MeshPeer 192.168.1.167 2121
sleep 1
screen -d -m -L testoutput/2222.txt -S p2pHStest2 java MeshPeer 192.168.1.167 2222 192.168.1.167 2121
sleep 1
screen -d -m -L testoutput/2323.txt -S p2pHStest3 java MeshPeer 192.168.1.167 2323 192.168.1.167 2222
echo 'Test started.'
