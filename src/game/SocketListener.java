package game;
import java.io.*;
import java.util.*;
public class SocketListener implements Runnable{
	private BufferedReader stream;
	private MBoolean running;
	private ArrayList<String> messages;
	
	public SocketListener(BufferedReader stream, MBoolean running, ArrayList<String> messages) {
		this.stream = stream;
		this.running = running;
		this.messages = messages;
	}
	public void run() {
		try {
			while(running.bool) {
				String message = stream.readLine();
				if(message != null) {
					messages.add(message);
					if(message.equals("close:")) {
						break; 
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
