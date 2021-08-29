package game;
import java.io.*;
public class RunServer {
	public static void main(String[] args ) {
		int connectionPort = 30000;
		int maxClients = 20;
		String[][] commandList = new String[][] {{"connections", "returns a list of all active connections"}, 
												 {"hosts", "returns a list of all servers hosting a game"}, 
												 {"close", "Drops all connections and end the server"}};
		Server server = new Server(connectionPort, maxClients);
		try(
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				) {
			new Thread(server).start();
			String input = in.readLine();
			while(input.equals("close")) {
				if(input.equals("hosts")) {
					System.out.println("Servers currently hosting:\n"+server.getServerList());
				}
				else if(input.equals("connections")) {
					System.out.println("Current Connections: \n"+server.getConnectionsList()); 	
				}
				else if(input.equals("help")) {
					for(int i = 0; i < commandList.length; i++) {
						System.out.println(commandList[i][0]+"\n\t- "+commandList[i]);}
				}
				input = in.readLine();
			}
			server.shutDown();
		}catch(Exception e) {
			e.printStackTrace();
		}
		 
	}
}
