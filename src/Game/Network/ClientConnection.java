package Game.Network;
import java.net.*;
import java.io.*;
import java.util.*;
public class ClientConnection implements Runnable{
	private Socket socket;
	private Server server;
	private int clientID;
	private MBoolean running;
	private ArrayList<String> requests;
	private ArrayList<String> messages;
	public ClientConnection(Server server, Socket socket, int clientID, MBoolean running) {
		this.server = server;
		this.socket = socket;
		this.clientID = clientID;
		messages = new ArrayList<String>();
		requests = new ArrayList<String>();
		this.running = running;
	}
	public void run() {
		try(
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		){
			Thread listener = new Thread(new SocketListener(socket, running, messages));
			listener.start();
			sendMessage(out, "id:"+clientID);
			Thread.sleep(500);
			while(running.bool) {
				Thread.sleep(100);
				if(messages.size() > 0) {
					String[] data = messages.get(0).split(":");
					String command = data[0];
					//System.out.println("Server: client "+clientID+" said "+messages.get(0));
					if(command.equals("sl")) {
						System.out.println("Sending server list to client: "+clientID);
						sendMessage(out, "sl:"+server.getServerList());
					}
					else if(command.equals("h")){
						System.out.println("Server: client "+clientID+" wants to host a game");
						server.addHostedServer(this, Integer.parseInt(data[1]), data[2]);
					}else if(command.equals("j")) {
						System.out.println("Server: client "+clientID+" wants to join "+data[1]);
						server.joinRequest(clientID, data[1], data[2], data[3]);
					}else if(command.equals("js")) {
						System.out.println("Server: "+messages.get(0));
						server.joinResponse(Integer.parseInt(data[2]), clientID, data[1]+":"+data[3]);
					}else if(command.equals("g")) {
						server.sendGameMessge(clientID, messages.get(0));
						//System.out.println("Client connection: "+messages.get(0)+" ID: "+clientID);
					}
					messages.remove(0);
					
				}
				if(requests.size() > 0) {
					sendMessage(out, requests.get(0));
					requests.remove(0);
				}
			}
			sendMessage(out, "close:");
			running.bool = false;
			System.out.println("Server session over");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public int getID() {
		return clientID;
	}
	private void request(String request, String data) {
		if(request.equals("join response")) {
			requests.add("js:"+data);
		}else if(request.equals("request to join")) {
			requests.add("jr:"+data);
		}else if(request.equals("send game message")) {
			requests.add(data);
		}
	}
	public void sendGameMessage(String message) {
		request("send game message", message);
	}
	public void requestToJoin(int id, String password, String playerName) {
		request("request to join", id+":"+password+":"+playerName);
	}
	public void respondToJoinRequest(String status) {
		request("join response", status);
	}
	public String toString() {
		return socket.getInetAddress().toString();
	}
	private void sendMessage(PrintWriter out, String message) {
		out.println(message);
		out.flush();
	}
}
