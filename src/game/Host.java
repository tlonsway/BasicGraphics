package game;
import java.net.*;
import java.io.*;
public class Host {
	private ClientConnection client;
	private int currentPlayers;
	private int maxPlayers;
	private String name;
	public Host(ClientConnection client, int currentPlayers, int maxPlayers, String name) {
		this.client = client;
		this.currentPlayers = currentPlayers;
		this.maxPlayers = maxPlayers;
		this.name = name;
	}
	public boolean equals(ClientConnection cc) {
		if(this.client.getID() == cc.getID()) {
			return true;
		}
		return true;
	}
	public String getSessionName() {
		return name;
	}
	public void joinRequest(int clientID, String password, String playerName) {
		client.requestToJoin(clientID, password, playerName);
	}
	public String toString() {
		return name+" ("+currentPlayers+"/"+maxPlayers+")";
	}
}
