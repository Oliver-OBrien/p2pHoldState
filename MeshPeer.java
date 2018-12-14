import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.lang.Runnable;
import java.net.InetAddress;
import java.util.ArrayList;

public class MeshPeer{
	public static void main(String args[]){
		try{
			if (args.length!=2 && args.length!=4) {
				System.out.println("Use either 2 or 4 arguments.");
				System.exit(1);
			}

			StateHolder state=new StateHolder();
			int port=Integer.parseInt(args[1]);
			String address=args[0];
			ServerSocket sSoc=new ServerSocket(port);
			Peer peer=new Peer(address,port);
			WaitForCons wait=new WaitForCons(sSoc,state,peer);
			state.open();
			state.addPeer(peer);
			state.close();
			System.out.println("added peer (init): "+peer);
			if (args.length==4){
				//not first peer, so connect to another
				String peerAddr=args[2];
				int peerPort=Integer.parseInt(args[3]);
				Socket con=new Socket(peerAddr,peerPort);
				Peer p=new Peer(peerAddr,peerPort);
				System.out.println("new con (init): "+p);
				ConnectionThread ct=new ConnectionThread(con,state,peer,p);
				state.open();
				state.addConnection(ct);
				state.addPeer(p);
				System.out.println("added peer (init2): "+p);
				state.close();
				ct.start();
			} 
			wait.start();
			//command interface
			String cmd[]={""};
			Scanner r=new Scanner(System.in);
			System.out.println("Enter commands:");
			while(!cmd[0].equals("quit")){
				cmd=r.nextLine().split(" ");
				cmd[0]=cmd[0].toLowerCase();
				if(cmd[0].equals("showpeers")){
					System.out.println("Peers:");
					state.open();
					for (Peer p : state.getPeers())
						System.out.println("\t"+p);
					state.close();
				} else if (cmd[0].equals("showconnections")){
					System.out.println("Connections:");
					state.open();
					for (ConnectionThread ct : state.getConnections()){
						System.out.println("\t"+ct+" ("+ct.peer1+","+ct.peer2+")");
					}
					state.close();
				} else if (cmd[0].equals("newpost")){
					System.out.print("Enter author: ");
					String author = r.nextLine();
					System.out.print("Enter text: ");
					String text = r.nextLine();
					BoardPost post = new BoardPost(author, text, System.currentTimeMillis());
					state.open();
					state.addPost(post);
					state.close();
					System.out.println("post added.");
				} else if (cmd[0].equals("showposts")){
					System.out.println("Posts:");
					state.open();
					for (BoardPost p : state.getPosts())
						System.out.println("\t"+p);
					state.close();
				} else if (cmd[0].equals("addpeer")){       
					String peerAddr=cmd[1];
					int peerPort=Integer.parseInt(cmd[2]);
					Socket con=new Socket(peerAddr,peerPort);
					Peer p=new Peer(peerAddr,peerPort);
					System.out.println("new con (cmd): "+p);
					ConnectionThread ct=new ConnectionThread(con,state,peer,p);
					state.open();
					state.addConnection(ct);
					state.addPeer(p);
					state.close();
					ct.start();
				} else if (cmd[0].equals("help")){
					System.out.println("valid commands are: showpeers, showconnections, showposts, newpost, addpeer [address] [port], help, quit");
				} else {
					if (!cmd[0].equals("quit"))
						System.out.println("command not recognized: '"+cmd[0]+"'");
					else{
						System.out.println("quitting..");
						//send LAST to all connected peers
						state.open();
						for (ConnectionThread ct : state.getConnections())
							ct.endTime = true;
						state.close();
						int waiting = 1;
						while (waiting>0){
							Thread.sleep(500);
							state.open();
							waiting = state.getConnections().size();
							state.close();
						}
						System.out.println("done.");
					}
				}
			}
			//user issued quit command, close connections
			state.open();
			for (ConnectionThread ct : state.getConnections()){
				ct.con.close();
			}
			state.close();
			System.exit(0);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

class WaitForCons implements Runnable{
	ServerSocket sSoc;
	StateHolder state;
	Thread thread;
	Peer thisPeer;

	public WaitForCons(ServerSocket ss, StateHolder st, Peer tp){
		sSoc = ss;
		state = st;
		thisPeer = tp;
	}

	public void run(){
		while(true){
			System.out.println("Waiting for connection..");
			try{
				Socket con=sSoc.accept();
				ConnectionThread ct=new ConnectionThread(con,state);
				state.open();
				state.addConnection(ct);
				state.close();
				ct.start();
				System.out.println("Started new connection on port "+con.getPort()+" to "+con.getInetAddress()+".");
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void start(){
		if(thread==null){
			thread=new Thread(this);
		}
		thread.start();
	}
}
