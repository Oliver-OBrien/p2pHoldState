import java.lang.Runnable;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ConnectionThread implements Runnable{
	Socket con;
	Thread t;
	StateHolder state;
	Peer peer1, peer2;
	boolean startedConnection, endTime;

	public ConnectionThread(Socket c, StateHolder s, Peer p1, Peer p2){
		con = c;
		state = s;
		startedConnection = true;
		peer1 = p1;
		peer2 = p2;
		endTime = false;
	}

	public ConnectionThread(Socket c, StateHolder s){
		con = c;
		state = s;
		startedConnection = false;
		peer1 = null;
		peer2 = null;
		endTime = false;
	}

	public boolean hasPeer(Peer p){
		return !p.equals(peer1) && !p.equals(peer2);
	}

	private void compareStates(Message message){	
		state.open();
		//compare peer lists between peers, adding any that are missing
		for (Peer p : message.peers){
			boolean inState = false;
			for (Peer q : state.getPeers())
				if (p.equals(q))
					inState = true;
			if (!inState){
				state.addPeer(p);
				System.out.println("peer added: "+p);
				try{
					Socket con = new Socket(p.address, p.port);
					ConnectionThread ct = new ConnectionThread(con, state, peer1, p);
					ct.start();
					state.addConnection(ct);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		//compare post lists between peers, adding any that are missing
		for (BoardPost p : message.posts){
			boolean inState = false;
			for (BoardPost q : state.getPosts())
				if (p.equals(q))
					inState = true;
			if (!inState){
				state.addPost(p);
				System.out.println("post added: "+p);
			}
		}
		state.close();
	}

	public void run(){
		try{
			ObjectOutputStream out=new ObjectOutputStream(con.getOutputStream());
			out.flush();
			Peer temp = peer2;
			ObjectInputStream in=new ObjectInputStream(con.getInputStream());

			if (startedConnection){
				//send FIRST message
				Message message = new Message(peer1,peer2);
				out.writeObject(message);
				//repeatedly send CHECK messages
			       	while(true){
					state.open();
					//send LAST if the time is right
					if (endTime){
						message = new Message();
						out.writeObject(message);
						state.removeConnection(this);
						con.close();
						state.close();
						return;
					}
					//send over info
					message = new Message(state);
					out.writeObject(message);
					state.close();
					//listen for info
					message = (Message)(in.readObject());
					if (message.type == MessageType.LAST)
						break;
					//check for new info
					compareStates(message);
					//wait a bit
					Thread.sleep(1000);
				}
			} else {
				//get peer info from FIRST, let other peer make the decisions (timing)
				Message message = (Message)(in.readObject());
				peer1 = message.peer2;
				peer2 = message.peer1;
				state.open();
				boolean inState = false;
				for (Peer q : state.getPeers()){
					if (peer2.equals(q))
						inState = true;
				}
				if (!inState){
					state.addPeer(peer2);
					System.out.println("added peer (rec): "+peer2);
				} else { //peers are already connected, so stop it
					message = new Message();
					out.writeObject(message);
					state.removeConnection(this);
					state.close();
					return;
				}
				state.close();
				while(true){
					message = (Message)(in.readObject());
					if (message.type == MessageType.LAST)
						break;
					compareStates(message);
					state.open();
					//send LAST if the time is right
					if (endTime){
						message = new Message();
						out.writeObject(message);
						state.removeConnection(this);
						con.close();
						state.close();
						return;
					}
					message = new Message(state);
					out.writeObject(message);
					state.close();
				}
			}
			//LAST message must have been sent, so remove this
			state.open();
			state.removeConnection(this);
			state.removePeer(peer2);
			state.close();
			return;
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Connection ended.");
		}
	}

	public void start(){
		if(t==null){
			t=new Thread(this);
		}
		t.start();
	}

	public String toString(){
		return "connection to: "+con.getInetAddress()+":"+con.getPort();
	}
}
