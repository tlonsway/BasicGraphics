package Game.Network;
import java.io.*;
import java.net.*;
import java.util.*;
public class SocketListener implements Runnable{
	private Socket socket;
	private MBoolean running;
	private ArrayList<String> messages;
	
	public SocketListener(Socket socket, MBoolean running, ArrayList<String> messages) {
		this.socket = socket;
		this.running = running;
		this.messages = messages;
	}
	public void run() {
		try (BufferedReader stream = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			Thread.sleep(100);
			while(running.bool) {
				if(stream.ready()) {
					String message = stream.readLine();
					if(message != null) {
						messages.add(message);
						if(message.equals("close:")) {
							System.out.println("I recieved close");
							break; 
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
