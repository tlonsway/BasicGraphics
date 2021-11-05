package Game.Network;
import java.net.*;
import java.util.*;
public class ServerUDPThread implements Runnable{
	private MBoolean running;
	private ArrayList<InetAddress[]> connections; 
	private byte[] buf = new byte[256];
	public ServerUDPThread(MBoolean running) {
		this.running = running;
	}
	public void run() {
		try(DatagramSocket socket = new DatagramSocket(30001);){
			while(running.bool) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Server recieved: "+message+" from "+packet.getAddress()+":"+packet.getPort());
				packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
				socket.send(packet);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void addConnection(InetAddress c1, InetAddress c2) {
		connections.add(new InetAddress[] {c1, c2});
		connections.add(new InetAddress[] {c2, c1});
	}
}
