package game;
import java.net.*;
import java.util.*;
public class Server implements Runnable{
	private int connectionPort;
	private ArrayList<ClientConnection> activeClients;
	private int maxClients;
	private ArrayList<Host> hostedServers;
	private MBoolean running;
	public Server(int connectionPort, int maxClients){
		this.connectionPort = connectionPort;
		activeClients = new ArrayList<ClientConnection>();
		this.maxClients = maxClients;
		hostedServers = new ArrayList<Host>();
		running = new MBoolean(true);
	}
	public void run() {
		try(
			ServerSocket ss = new ServerSocket(connectionPort);
			){
			while(activeClients.size() < maxClients) {
				ClientConnection c = new ClientConnection(this, ss.accept(), activeClients.size(), running);
				activeClients.add(c);
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
	public void addHostedServer(InetAddress ip, int port) {
		hostedServers.add(new Host(ip, port));
	}
	public String getServerList() {
		String servers = "sl:";
		for(Host hosts: hostedServers) {
			servers+=hosts.toString()+",";
		}
		return servers;
	}
}
