package Game.Network;
import java.net.*;
public class ClientUDPListenThread implements Runnable{
	private MBoolean running;
	private DatagramSocket socket;
	private byte[] buf = new byte[256];
	private Client client;
	public ClientUDPListenThread(MBoolean running, Client client) {
		this.running = running;
		this.client = client;
	}
	public void run() {
		try{
			socket = new DatagramSocket();
			while(running.bool) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				System.out.println("Client "+client.getID()+": "+new String(packet.getData(), 0, packet.getLength()));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getIP() {
		return socket.getInetAddress().toString();
	}
	public int getPort() {
		return socket.getLocalPort();	
				}
}
