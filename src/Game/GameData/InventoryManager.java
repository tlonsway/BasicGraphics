package Game.GameData;
import java.util.*;
public class InventoryManager {
	ArrayList<Inventory> inventories;
	public InventoryManager() {
		inventories = new ArrayList<>();
	}
	
	public void addInventory(String name, float volume) {
		inventories.add(new Inventory(name, volume));
	}
	
	public float getWeight() {
		float sum = 0;
		for(Inventory in: inventories) {
			sum+=in.getWeight();
		}
		return sum;
	}
	
	public Item removeItem(Item rem) {
		Item removed = rem.clone();
		removed.setCount(0);
		for(Inventory in: inventories) {
			Item ret = in.removeItem(rem);
			rem.remove(ret.getCount());
			removed.addItem(ret);
			if(rem.getCount() == 0) {
				break; 
			}
		}
		return removed;
	}
	
	//add will be set to null if 
	public void addItem(Item add) {
		for(Inventory in: inventories) {
			in.addItem(add);
			if(add.getCount() == 0) {
				add = null;
				break;
			}
		}
	}
	
	
}
