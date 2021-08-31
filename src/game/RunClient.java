package game;
import java.io.*;
import java.util.*;
public class RunClient {
	public static void main(String[] args) {
		String[][] commandList = new String[][] {{"host list", "Returns a list of possible hosts to join."}, 
												 {"host game", "Tells the server you wish to host a game."}, 
												 {"join game", "Tells the server you want to join a game."}, 
												 {"close", "Exits the program."}};
		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
			System.out.println("Enter username: ");
			String inputLine = in.readLine();
			String username = inputLine;
			System.out.println("Enter server IP address: ");
			inputLine = in.readLine();
			String ip = inputLine;
			//String ip = "127.0.0.1";
			System.out.println("Enter port number: ");
			//inputLine = in.readLine();
			//int port = Integer.parseInt(inputLine);
			int port = 30000;
			Client client = new Client(ip, port, null, username);
			if(client.getConnectionStatus().equals("Connected")) {
				new Thread(client).start();
			}
			System.out.println("What would you like to do:\n\t-say \"help\" to get the list of commands");
			while(!inputLine.equals("close")) {
				inputLine = in.readLine();
				if(inputLine.equals("host list")) {
					client.getServerList();
				}else if(inputLine.equals("join game")) {
					if(client.getConnectionStatus().equals("Connected")) {
						System.out.println("Session name: ");
						String sessionName = in.readLine();
						System.out.println("Password: ");
						String password = in.readLine();
						client.joinGame(sessionName, password, username);
						String joinState = client.getRequestState();
						int count = 0;
						while(joinState.equals("unknown")) {
							joinState = client.getRequestState();
							try {
								Thread.sleep(120);
							}catch(Exception e) {
								e.printStackTrace();
							}
							if(count > 500) {
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
					}else {
						System.out.println("You are in offline mode, you cannot join a game");
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
						System.out.println(commandList[i][0]+"\n\t- "+commandList[i][1]);}
				}
			}
			System.out.println("Terminating the session"); 
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
		float worldWid = 800;
		float worldHgt = 800;
		Random generator = new Random(hostedGame.getSeed());
		/*for(int i=0;i<100;i++) {
			Mesh m = ObjectGeneration.generateTree((int)(hostedGame.getSeed()%1000+100000.0*i), 7);
			GameObject go1 = new GameObject("Tree",world,m);
			//float tXLoc = (float)Math.random()*worldWid;
			//float tYLoc = (float)Math.random()*worldHgt;
			//float tXLoc = (float)((float)hostedGame.getSeed()%1000+(100.0*i))*worldWid;
			float tXLoc = (float)generator.nextFloat()*worldWid;
			float tYLoc = (float)generator.nextFloat()*worldHgt;
			go1.translate(tXLoc, 150f, tYLoc);
			graphic.addGameObject(go1);
		}*/
		System.out.println("Tree generation complete");
		new Thread(new GameToServerPlayerLocationThread(client, graphic)).start();
		new Thread(new PlayerLocationThread(client)).start();
		graphic.loop();
	}
}
