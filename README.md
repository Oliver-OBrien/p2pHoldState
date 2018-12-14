# p2pHoldState
Simple peer-to-peer CLI message board for a small number of peers on one network written in Java. Created for a class project.

## How to use:
javac *.java to compile.
java MeshPeer [local IP address] [ServerSocket port number] to create an unconnected peer.
java MeshPeer [local IP address] [ServerSocket port number] [other IP] [other port] to create a peer and connect it to another.
On any peer, help will print a list of valid commands. Commands don't take arguments; parameters are asked for when needed.

## Using runtest.sh:
runtest.sh should be modified so that '192.168.1.167' is replaced by the local ip address of the machine running the test.
./runtest.sh will start three peers that are connected to eachother in screen sessions after compiling.
screen -r p2pHStest1 will connect to the first peer, change 1 to 2 or 3 for the others.
help will list valid commands once connected.
./endtest.sh will end those screen sessions.
