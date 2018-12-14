import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{
	MessageType type;
	ArrayList<Peer> peers; //only used by CHECK
	ArrayList<BoardPost> posts;
	Peer peer1, peer2;

	public Message(StateHolder s){ //for CHECK
		type = MessageType.CHECK;
		peers = new ArrayList<Peer>();
		for (Peer q : s.getPeers())
			peers.add(q);
		posts = new ArrayList<BoardPost>();
		for (BoardPost q : s.getPosts())
			posts.add(q);
		peer1 = null;
		peer2 = null;
	}

	public Message(Peer p1, Peer p2){ //sent by peer that starts connection
		type = MessageType.FIRST;
		peers = null;
		peer1 = p1;
		peer2 = p2;
	}

	public Message(){ //close the connection
		type = MessageType.LAST;
		peers = null;
		peer1 = null;
		peer2 = null;
	}
}
