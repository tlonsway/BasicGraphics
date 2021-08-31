package game;
import java.util.*;
public class PlayerLocationThread implements Runnable{
	private Client client;
	public PlayerLocationThread(Client client) {
		this.client = client; 
	}
	public void run() {
		int updateTime = 1000;
		if(client.isHost()) {
			while(client.isRunning()) {
				try {
					client.broadCastPlayerLocation("local");
					for(OtherPlayer p: client.getHostedSession().getConnectedUsers()) {
						client.broadCastPlayerLocation(p.getName());
					}
					Thread.sleep(updateTime);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			while(client.isRunning()) {
				try {
					client.updateLoction();
					Thread.sleep(updateTime);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
