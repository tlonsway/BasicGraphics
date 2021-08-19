package game;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
public class Client implements Runnable{
	private String hostName;
	private int port;
	private int id;
	private ArrayList<String> messages;
	private MBoolean running;
	//sl = server list, h = host
	private ArrayList<String> requests;
	Graphics game;
	public Client(String hostName, int port, Graphics game) {
		this.hostName = hostName;
		this.port = port;
		this.game = game;
		requests = new ArrayList<String>();
		messages = new ArrayList<String>();
		running = new MBoolean(true);
	}
	public void run() {
		try (
			Socket connection = new Socket(hostName, port);
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		){
			Thread listener = new Thread(new SocketListener(in, running, messages));
			listener.start();
			Thread.sleep(500);
			while(true) {
				Thread.sleep(100);
				if(messages.size() > 0) {
					String command = messages.get(0).substring(0,messages.get(0).indexOf(":"));
					String content = messages.get(0).substring(messages.get(0).indexOf(":")+1);
					if(command.equals("close")) {
						break;
					}
					else if(command.equals("sl")) {
						System.out.println("Client "+id+": "+content);
					}
					else if(command.equals("id")) {
						id = Integer.parseInt(content);
						System.out.println("I am client: "+id);
						if(id%2==0) {
							requests.add("h:"+30000+id);
						}
						else {
							requests.add("sl:");
						}
					}
					messages.remove(0);
				}
				if(requests.size() > 0) {
					sendMessage(out, requests.get(0)+":");
					System.out.println("Client: "+id+" Requesting: "+requests.get(0));
					requests.remove(0);
				}
			}
			running.bool = false;
			System.out.println("Client session over: "+id);
		}catch (UnknownHostException e) {
			System.out.println("Could not connect to "+hostName+":"+port);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void request(String request) {
		if(request.equals("server list")) {
			requests.add("sl");
		}
	}
	private void sendMessage(PrintWriter out, String message) {
		out.println(message);
		out.flush();
	}
	
}
