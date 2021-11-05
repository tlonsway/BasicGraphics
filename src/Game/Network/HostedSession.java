package Game.Network;
import java.util.*;
import Game.GameData.*;
public class HostedSession {
	private String sessionName;
	private String password;
	private ArrayList<OtherPlayer> connectedUsers;
	private int seed;
	private World world;
	private boolean host;
	private int maxUsers;
	private float[] playerLocation;
	private String username;
	public HostedSession(String sessionName, String password, int maxUsers, boolean host, int seed, String username) {
		this.sessionName = sessionName;
		this.password = password;
		this.maxUsers = maxUsers;
		connectedUsers = new ArrayList<OtherPlayer>();
		this.host = host;
		this.seed = seed; 
		this.username = username;
		playerLocation = new float[] {0, 0, 0};
		/*
		if(!host) {
			System.out.println("Generating world");
			world = new World(seed);
			System.out.println("Created world and launching window");
			new Thread(new GraphicsThread(world)).start();
			System.out.println("Started game Thread");
		}*/	
	}
	public boolean isHost() {
		return host;
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
	public void setPlayerLocation(String playerName, float x, float y, float z) {
		if(playerName.equals("local")) {
			playerLocation = new float[] {x, y, z};
		}else {
			boolean foundPlayer = false;
			for(OtherPlayer p: connectedUsers) {
				if(p.getName().equals(playerName)) {
					p.setPosition(new float[] {-x, -y, -z});
					foundPlayer = true;
				}
			}
			if(!foundPlayer) {
				connectedUsers.add(new OtherPlayer(playerName, 0, new float[] {x, y, z}, new float[] {0, 0, 0})); 
			}
			
		}
	}
	public String getPlayerLocation(String playerName) {
		if(playerName.equals("local")) {
			
			return username+":"+playerLocation[0]+":"+playerLocation[1]+":"+playerLocation[2];
		}else {
			for(OtherPlayer p: connectedUsers) {
				if(p.getName().equals(playerName)) {
					return playerName+":"+p.getLocation()[0]+":"+p.getLocation()[1]+":"+p.getLocation()[2];
				}
			}
		}
		return null;
	}
	public ArrayList<OtherPlayer> getConnectedUsers(){
		return connectedUsers;
	}
	public String getSessionName() {
		return sessionName;
	}
	public int getMaxUsers() {
		return maxUsers;
	}
}
