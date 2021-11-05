package Game.Network;
import java.net.*;
import java.util.*;

import testing.Host;
public class Server implements Runnable{
	private int connectionPort;
	private ArrayList<ClientConnection> activeClients;
	private int maxClients;
	private ArrayList<Host> hostedServers;
	private MBoolean running;
	private ArrayList<int[]> connections; 
	public Server(int connectionPort, int maxClients){
		this.connectionPort = connectionPort;
		activeClients = new ArrayList<ClientConnection>();
		this.maxClients = maxClients;
		hostedServers = new ArrayList<Host>();
		running = new MBoolean(true);
		connections = new ArrayList<int[]>();
		new Thread(new ServerUDPThread(running)).start();
	}
	public void run() {
		try(
			ServerSocket ss = new ServerSocket(connectionPort);
			){
			while(activeClients.size() < maxClients && running.bool) {
				ClientConnection c = new ClientConnection(this, ss.accept(), activeClients.size(), running);
				activeClients.add(c);
				System.out.println("Player connected");
				new Thread(c).start();
			}
			System.out.println("Server has hit max pop");
			Thread.sleep(10000);
			running.bool = false;
			System.out.println("Terminating the server.");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void joinRequest(int clientID, String sessionName, String password, String playerName) {
		for(Host h: hostedServers) {
			if(h.getSessionName().equals(sessionName)) {
				h.joinRequest(clientID, password, playerName);
				break;
			}
		}
	}
	public void joinResponse(int clientID, int host, String status) {
		for(ClientConnection cc: activeClients) {
			if(cc.getID() == clientID) {
				System.out.println("Server: Join response to client: "+clientID+" host: "+host+" status: "+status);
				boolean state = false;
				if(status.contains(":")) {
					state = true;
				}
				if(state) {
					connections.add(new int[] {host, clientID});
					connections.add(new int[] {clientID, host});
				}
				cc.respondToJoinRequest(status);
				break;
			}
		}
	}
	public void sendGameMessge(int ID, String message) {
		//System.out.println("Server: "+message+" Client: "+ID+" Game connections: "+connections.size()); 
		for(int[] connect: connections) {
			if(connect[0] == ID) {
				activeClients.get(connect[1]).sendGameMessage(message);
				//System.out.println("Sending "+message+" from client "+ID);
			}
		}
	}
	public void addHostedServer(ClientConnection cc, int max, String name) {
		hostedServers.add(new Host(cc, 1, max, name));
	}
	public String getServerList() {
		String servers = "sl:";
		for(Host hosts: hostedServers) {
			servers+=hosts.toString()+",";
		}
		return servers;
	}
	public String getConnectionsList() {
		String connections = "There are "+activeClients.size()+" active connections\n";
		for(ClientConnection cc: activeClients) {
			connections += cc.toString()+"\n";
		}
		return connections;
	}
	public void shutDown() {
		running.bool = false;
	}
}
