package game;
import java.util.*;
public class HostedSession {
	private String sessionName;
	private String password;
	private ArrayList<OtherPlayer> connectedUsers;
	private int seed;
	private World world;
	private boolean host;
	private int maxUsers;
	public HostedSession(String sessionName, String password, int maxUsers, boolean host, int seed) {
		this.sessionName = sessionName;
		this.password = password;
		this.maxUsers = maxUsers;
		connectedUsers = new ArrayList<OtherPlayer>();
		this.host = host;
		this.seed = seed;
		if(!host) {
			System.out.println("Generating world");
			world = new World(seed);
			System.out.println("Created world and starting appliction");
			
			System.out.println("Launching game window");
			new Thread(new GraphicsThread(world)).start();
			System.out.println("Started game Thread");
		}	
	}
	public boolean tryJoin(String pw, String name) {
		if(connectedUsers.size() < maxUsers && password.equals(pw)) {
			connectedUsers.add(new OtherPlayer(name, connectedUsers.size()));
			System.out.println("Client hosting: "+name+" has joined "+sessionName);
			return true;
		}
		return false;
	}
	public int getSeed() {
		return seed;
	}
	public void leave(int ID) {
		connectedUsers.remove(ID);
		for(int i = ID; i < connectedUsers.size(); i++) {
			connectedUsers.get(i).setID(i);
		}
	}
	public String getSessionName() {
		return sessionName;
	}
	public int getMaxUsers() {
		return maxUsers;
	}
}
