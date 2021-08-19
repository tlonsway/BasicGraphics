package game;
import java.net.*;
public class Host {
	private InetAddress ip;
	private int port;
	public Host(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	public String getIP() {
		return ip.toString();
	}
	public int getPort() {
		return port;
	}
	public boolean equals(Host h) {
		if(getIP().equals(h.getIP()) && port == h.getPort()) {
			return true;
		}
		return true;
	}
	public String toString() {
		return getIP()+":"+getPort();
	}
}
