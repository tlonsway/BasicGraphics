package Game.Network;
import java.net.*;
import java.util.*;
public class ClientUDPUpdateThread implements Runnable{
	private String connection;
	private int port;
	private byte[] buf = new byte[256];
	private Client client;
	private MBoolean running;
	public ClientUDPUpdateThread(String connection, int port, Client client, MBoolean running) {
		this.connection = connection;
		this.port = port;
		this.client = client;
		this.running = running;
	}
	public void run() {
		try(DatagramSocket socket = new DatagramSocket();){
			int count = 0;
			while(count < 20) {
				String message = "Testing "+client.getID();
				buf = message.getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(connection), port);
				socket.send(packet);
				Thread.sleep(500);
			}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
