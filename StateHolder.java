import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Collections;

public class StateHolder{
	private final Semaphore lock=new Semaphore(1);
	private ArrayList<Peer> peers;
	private ArrayList<ConnectionThread> connections;
	private ArrayList<BoardPost> posts;
	private boolean postsSorted;

	public StateHolder(){
		peers = new ArrayList<Peer>();
		connections = new ArrayList<ConnectionThread>();
		posts = new ArrayList<BoardPost>();
		postsSorted = true;
	}

	public void open(){
		try{
			lock.acquire();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void close(){
		if (!postsSorted)
			sortPosts();
		lock.release();
	}

	public void addPeer(Peer p){
		peers.add(p);
	}

	public void removePeer(Peer p){
		peers.remove(p);
	}

	public ArrayList<Peer> getPeers(){
		return peers;
	}

	public void addConnection(ConnectionThread ct){
		connections.add(ct);
	}

	public void removeConnection(ConnectionThread ct){
		connections.remove(ct);
	}

	public ArrayList<ConnectionThread> getConnections(){
		return connections;
	}

	public void addPost(BoardPost p){
		posts.add(p);
		postsSorted = false;
	}

	public ArrayList<BoardPost> getPosts(){
		return posts;
	}

	private void sortPosts(){ //untested, I should really do that
		//should be fast when posts at beginning of list are already in order
		Collections.sort(posts);
		postsSorted = true;
	}
}
