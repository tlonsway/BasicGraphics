package Game.GameData;

public class Item {
	private final String name;
	private final float weight;
	private final float volume;
	private int count;
	
	public Item(String name, float volume, float weight) {
		this.name = name;
		this.volume = volume;
		this.weight = weight;
		count = 1;
	}
	
	public Item(String name, float volume, float weight, int count) {
		this.name = name;	
		this.volume = volume;
		this.weight = weight;
		this.count = count;
	}
	
	public String getName() {
		return name;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public float getTotalVolume() {
		return count*volume;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public float getTotalWeight() {
		return weight*count;
	}
	
	public int getCount() {
		return count;
	}
	
	//returns the amount that was not added
	public void addItem(Item item) {
		if(item.equals(this)) {
			count+=item.getCount();;
			item.remove(item.getCount());
		}
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	public void remove(int rCount) {
		count -= rCount;
		if(count < 0) {
			count = 0;
		}
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Item) {
			Item item = (Item)obj;
			if(item.getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public Item clone() {
		return new Item(name, volume, weight, count);
	}
}
