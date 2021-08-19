package game;
import java.net.*;
import java.io.*;
import java.util.*;
public class ClientConnection implements Runnable{
	private Socket socket;
	private Server server;
	private int clientID;
	private MBoolean running;
	private ArrayList<String> messages;
	public ClientConnection(Server server, Socket socket, int clientID, MBoolean running) {
		this.server = server;
		this.socket = socket;
		this.clientID = clientID;
		messages = new ArrayList<String>();
		this.running = running;
	}
	public void run() {
		try(
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		){
			Thread listener = new Thread(new SocketListener(in, running, messages));
			listener.start();
			sendMessage(out, "id:"+clientID);
			Thread.sleep(500);
			while(running.bool) {
				Thread.sleep(100);
				if(messages.size() > 0) {
					String command = messages.get(0).substring(0, messages.get(0).indexOf(":"));
					String content = messages.get(0).substring(messages.get(0).indexOf(":")+1);
					if(command.equals("sl")) {
						sendMessage(out, server.getServerList());
					}
					else if(command.equals("h")){
						content = content.substring(0, content.length()-1);
						System.out.println("Server: client "+clientID+" wants to host a game");
						server.addHostedServer(socket.getInetAddress(), Integer.parseInt(content));
					}
					messages.remove(0);
				}
			}
			sendMessage(out, "close:");
			//socket.close();
			running.bool = false;
			System.out.println("Server session over");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void sendMessage(PrintWriter out, String message) {
		out.println(message);
		out.flush();
	}
}
