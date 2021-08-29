package game;
import java.io.*;
public class RunClient {
	public static void main(String[] args) {
		String username = "BRUH";
		String[][] commandList = new String[][] {{"host list", "Returns a list of possible hosts to join"}, 
												 {"host game", "Tells the server you wish to host a game"}, 
												 {"join game", "Tells the server you want to join a game"}};
		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
			System.out.print("Enter server IP address: ");
			String inputLine = in.readLine();
			String ip = inputLine;
			System.out.print("Enter port number: ");
			inputLine = in.readLine();
			int port = Integer.parseInt(inputLine);
			Client client = new Client(ip, port, null);
			new Thread(client).start();
			while(!inputLine.equals("close")) {
				inputLine = in.readLine();
				if(inputLine.equals("host list")) {
					client.getServerList();
				}else if(inputLine.equals("join game")) {
					System.out.print("Session name: ");
					String sessionName = in.readLine();
					System.out.print("Password: ");
					String password = in.readLine();
					client.joinGame(sessionName, password, username);
					String joinState = client.getRequestState();
					int count = 0;
					while(joinState.equals("unknown")) {
						joinState = client.getRequestState();
						try {
							Thread.sleep(50);
						}catch(Exception e) {
							e.printStackTrace();
						}
						if(count > 200) {
							break;
						}
						count++;
					}
					if(joinState.equals("unknown")) {
						System.out.println("Didn't recive message from server");
					}else if(joinState.equals("accepted")) {
						System.out.println("Joining the game...");
						startGame(client); 
					}else{
						System.out.println("Unable to join, server might be at max population or wrong password provided");
					}
				}else if(inputLine.equals("host game")) {
					System.out.print("Session name: ");
					String sessionName = in.readLine(); 
					System.out.print("Password: ");
					String password = in.readLine();
					System.out.print("Max number of users: ");
					int maxUsers = Integer.parseInt(in.readLine()); 
					client.hostGame(sessionName, password, maxUsers);
					startGame(client);
				}else if(inputLine.equals("help")) {
					for(int i = 0; i < commandList.length; i++) {
						System.out.println(commandList[i][0]+"\n\t- "+commandList[i]);}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void startGame(Client client) {
		HostedSession hostedGame = client.getHostedSession();
		World world = new World(hostedGame.getSeed());
		int[] screenDims = new int[] {1920,1080};
		Graphics graphic = new Graphics(screenDims);
		graphic.setWorld(world);
		float[] vert = world.vertices;
		int[] ind = world.indices;
		graphic.updateData(vert, ind);
		graphic.loop();
	}
}
