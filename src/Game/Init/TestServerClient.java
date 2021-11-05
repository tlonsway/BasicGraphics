package Game.Init;
import java.util.*;
import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
public class TestServerClient {
	public static void main(String args[]) {
		int portNumber = 30000;
		String hostName = "127.0.0.1";
		int numClients = 20;
		Server server = new Server(portNumber, numClients);
		ArrayList<Client> clients = new ArrayList<Client>();
		for(int i = 0; i < numClients; i++) {
			clients.add(new Client(hostName, portNumber, null, ""));
		}
		try {
			new Thread(server).start();
			for(int i = 0; i < clients.size(); i++) {
				new Thread(clients.get(i)).start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
