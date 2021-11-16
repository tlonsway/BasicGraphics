package Game.GameData;
import java.util.*;
public class ItemManager {
	private HashMap<String, Item> itemList;
	public ItemManager() {
		itemList = new HashMap<>();
		try(Scanner scn = new Scanner("Data/Items/resources.txt")){
			while(scn.hasNextLine()) {
				String line = scn.nextLine();
				String[] tokens = line.split(":");
				itemList.put(tokens[0], new Item(tokens[0], Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Item getItem(String name) {
		return itemList.get(name);
	}
}
