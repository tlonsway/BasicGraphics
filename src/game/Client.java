package game;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
public class Client implements Runnable{
	private String hostName;
	private int port;
	private int id;
	private String requestState;
	private ArrayList<String> messages;
	private MBoolean running;
	private String username;
	//sl = server list, h = host
	private ArrayList<String> requests;
	private HostedSession hostedGame;
	private boolean offline;
	Graphics game;
	public Client(String hostName, int port, Graphics game, String username) {
		this.hostName = hostName;
		this.port = port;
		this.game = game;
		this.username = username; 
		requests = new ArrayList<String>();
		messages = new ArrayList<String>();
		running = new MBoolean(true);
		hostedGame = null;
		requestState = "unknown";
		offline = false; 
	}
	public void run() {
		try (
			Socket connection = new Socket(hostName, port);
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		){
			Thread listener = new Thread(new SocketListener(connection, running, messages));
			listener.start();
			Thread.sleep(500);
			while(running.bool) {
				Thread.sleep(100);
				if(messages.size() > 0) {
					String[] data = messages.get(0).split(":");
					String command = data[0];
					if(command.equals("close")) {
						running.bool = false;
						break;
					}else if(command.equals("g")) {
						if(data[1].equals("pl")) {
							String player = data[2]; 
							float x = Float.parseFloat(data[3]);
							float y = Float.parseFloat(data[4]);
							float z = Float.parseFloat(data[4]);
							for(OtherPlayer p: hostedGame.getConnectedUsers()) {
								if(p.getName().equals(player)) {
									p.setPosition(new float[] {x, y, z});
								}
								System.out.println("Setting player position of "+player);
							}
						}
					}else if(command.equals("sl")) {
						System.out.println("Servers hosting: ");
						for(int i = 1; i < data.length; i++) {
							System.out.println(data[i]);
						}
					}
					else if(command.equals("id")) {
						id = Integer.parseInt(data[1]);
						System.out.println("I am client: "+id);
					}
					else if(command.equals("jr") && hostedGame != null) {
						boolean joinStatus = hostedGame.tryJoin(data[2], data[3]);
						System.out.println("Client: "+id+" responding to join request "+messages.get(0));
						if(joinStatus) {
							joinReply(joinStatus, data[1]+":"+hostedGame.getSeed());
						}else {
							joinReply(joinStatus, data[1]);
						}
					}else if(command.equals("js")) {
						System.out.println("Client "+id+" recieved a join status update: "+messages.get(0));
						if(data[1].equals("true")) {
							System.out.println("Client "+id+": I joined a game with the seed: "+data[2]);
							hostedGame = new HostedSession(null, null, 0, false, Integer.parseInt(data[2]) );
							requestState = "accepted";
						}
						else {
							requestState = "rejected"; 
							System.out.println("Client "+id+": I failed to joined a game");
						}
					}
					messages.remove(0);
				}
				if(requests.size() > 0) {
					String[] request = requests.get(0).split(":");
					if(request[0].equals("sl")) {
						sendMessage(out, "sl");
					}else if (request[0].equals("h")) {
						sendMessage(out, "h:"+hostedGame.getMaxUsers()+":"+hostedGame.getSessionName());
					}else if (request[0].equals("j")) {
						System.out.println("Client "+id+": I want to join a game. "+requests.get(0));
						sendMessage(out, "j:"+request[1]+":"+request[2]+":"+request[3]);
					}else if(request[0].equals("js")) {
						System.out.println("I am replying client's join request: "+requests.get(0)); 
						sendMessage(out, "js:"+request[1]+":"+request[2]+":"+request[3]);
					}else {
						sendMessage(out, requests.get(0));
					}
					//System.out.println("Client: "+id+" Requesting: "+requests.get(0));
					requests.remove(0);
				}
			}
			System.out.println("Client session over: "+id);
		}catch (ConnectException e) {
			offline = true;
			System.out.println("Could not connect to "+hostName+":"+port);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void broadCastPlayerLocation(String player) {
		request("broadcast location", hostedGame.getPlayerLocation(player));
	}
	public void updateLoction() {
		request("update location", hostedGame.getPlayerLocation("local"));
	}
	public String getConnectionStatus() {
		if(offline) 	
			return "Offline";
		else
			return "Connected";
	}
	public HostedSession getHostedSession() {
		return hostedGame; 
	}
	public String getRequestState() {
		String temp = requestState;
		requestState = "unknown";
		return temp; 
	}
	public void endSession() {
		running.bool = false;
	}
	public void hostGame(String sessionName, String pw, int maxUsers) {
		int seed = (int)(Math.random()*10000000);
		hostedGame = new HostedSession(sessionName, pw, maxUsers, true, seed);
		System.out.println("Hosting a game with the seed "+seed);
		request("host server", null);
	}
	public void getServerList() {
		request("server list", null);
	}
	private void request(String request, String data) {
		if(request.equals("server list")) {
			requests.add("sl");
		}
		else if(request.equals("host server")) {
			requests.add("h:");
		}else if(request.equals("join")) {
			requests.add("j:"+data);
		}else if(request.equals("join reply")) {
			requests.add("js:"+data);
		}else if(request.equals("update location")) {
			requests.add("g:pl:"+username+":"+data);
		}else if(request.equals("broadcast location")) {
			requests.add("g:pl:"+data);
		}
	}
	public boolean isHost() {
		return hostedGame.isHost();
	}
	public boolean isRunning() {
		return running.bool;
	}
	public void joinReply(boolean status, String ID) {
		request("join reply", status+":"+ID);
	}
	public void joinGame(String sessionName, String password, String playerName) {
		request("join", sessionName+":"+password+":"+playerName);
	}
	private void sendMessage(PrintWriter out, String message) {
		out.println(message);
		out.flush();
	}
}
