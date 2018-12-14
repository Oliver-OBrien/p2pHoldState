import java.io.Serializable;

public class Peer implements Serializable{
	String address;
	int port;

	public Peer(String addr, int p){
		address=addr;
		port=p;
	}

	public String toString(){
		return address+":"+port;
	}

	public boolean equals(Object o){
		return (o instanceof Peer) && (((Peer)o).address.equals(address)) && (((Peer)o).port==port);
	}

	public int HashCode(){
		return 0;
	}
}
