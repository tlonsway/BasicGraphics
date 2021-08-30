package game;

public class GameToServerPlayerLocationThread implements Runnable{
	private Client client;
	private Graphics graphics;
	public GameToServerPlayerLocationThread(Client client, Graphics graphics) {
		this.client = client;
		this.graphics = graphics;
	}
	public void run() {
		while(client.isRunning()) {
			client.getHostedSession().setPlayerLocation("local", graphics.getCamera().getCamPos()[0], graphics.getCamera().getCamPos()[1], graphics.getCamera().getCamPos()[2]);
			if(graphics.getGameObjects().size() < client.getHostedSession().getConnectedUsers().size()) {
				graphics.getGameObjects().add(new GameObject(client.getHostedSession().getConnectedUsers().get(graphics.getGameObjects().size()).getName(), graphics.getWorld()));
			}
			for(int i = 0; i < graphics.getGameObjects().size(); i++) {
				graphics.getGameObjects().get(i).setPosition(client.getHostedSession().getConnectedUsers().get(i).getLocation());
			}
			try {
				Thread.sleep(200);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
