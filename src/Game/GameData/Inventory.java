package Game.GameData;
import java.util.*;
public class Inventory {
	private final String name;
	ArrayList<Item> items;
	float maxVolume;
	public Inventory(String name, float maxVolume) {
		this.name = name;
		this.maxVolume = maxVolume;
	}
	
	//The item obj passed in will have its count modified
	public void addItem(Item item) {
		int maxToAdd = item.getCount();
		
		for(Item i: items) {
			i.addItem(item);
			if(item.getCount() == 0) {
				break;
			}
		}
	}
	
	public float getMaxVolume() {
		return maxVolume;
	}
	
	public float getUsedVolume() {
		float used = 0;
		for(Item i: items) {
			used += i.getTotalVolume();
		}
		return used;
	}
	
	public float getWeight() {
		float weight = 0; 
		for(Item i: items) {
			weight+=i.getTotalWeight();
		}
		return weight;
	}
	
	//includes quantity
	public boolean hasItem(Item item) {
		for(Item i: items) {
			if(i.equals(item)) {
				if(i.getCount() >= item.getCount()) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	//returns the item that was removed
	public Item removeItem(Item item) {
		for(Item i: items) {
			if(i.equals(item)) {
				if(i.getCount() > item.getCount()) {
					i.remove(item.getCount());
					return item.clone();
				}else {
					Item a = i.clone();
					items.remove(i);
					return a;
				}
			}
		}
		return null;
	}
}
