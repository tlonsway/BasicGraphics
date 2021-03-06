package Game.GameContent;

import Game.Graphics.*;
import java.util.ArrayList;
import org.jblas.*;

public abstract class PhysicalResource extends Model {
	
	
	String type;
	int health;
	boolean onScreen;
	
	public PhysicalResource(VAOStorage vao, Mesh m, float[] position, float[] rotation, String type) {
		super(vao,m,position,rotation);
		health = 100;
		onScreen = false;	
		this.type = type;
	}
	public boolean isOnScreen() {
		return onScreen;
	}
	public String getType() {
		return type;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int newHealth) {
		health = newHealth;
	}
	public void modifyHealth(int amount) {
		health += amount;
	}
	
	
}
