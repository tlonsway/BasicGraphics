package Game.GameData;
import java.util.*;
public class CraftableItem extends Item{
	private ArrayList<Item> requirements;
	public CraftableItem(String name, int count, int maxCount, ArrayList<Item> requirements) {
		super(name, count, maxCount);
		this.requirements = requirements;
	}
	
	public ArrayList<Item> getRequirments(){
		ArrayList<Item> req = new ArrayList<Item>();
		for(Item i: requirements) {
			req.add(i.clone());
		}
		return req;
	}
	
	public Item getItem() {
		return super.clone();
	}
}
