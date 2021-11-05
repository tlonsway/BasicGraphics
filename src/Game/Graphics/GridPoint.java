package Game.Graphics;

import java.util.ArrayList;

public class GridPoint{
	private float[] location;
	public GridPoint(float[] location) {
		this.location = location;
	}
	public ArrayList<GridPoint> getClosest(ArrayList<GridPoint> pts, double gridUnit) {
		ArrayList<GridPoint> closestPoints = new ArrayList<GridPoint>();
		float[] distances = new float[3];
		distances[0] = (float)gridUnit;
		distances[1] = (float)Math.sqrt(2*Math.pow(gridUnit, 2));
		distances[2] = (float)Math.sqrt(Math.pow(gridUnit, 2)+Math.pow(distances[1], 2));
		for(GridPoint p: pts) {
			float dis = distanceTo(p);
			if(dis == distances[0] || dis == distances[1] || dis == distances[2]) {
				if(closestPoints.size() == 0 && !equals(p)) {
					closestPoints.add(p);
				}
				else {
					int index = 0;
					boolean hitEnd = true;
					while(index < closestPoints.size()) {
						if(distanceTo(closestPoints.get(index)) > dis && !equals(closestPoints.get(index))) {
							closestPoints.add(index, p);
							hitEnd = false;
							break;
						}
						index++;
					}
					if(hitEnd) {
						closestPoints.add(p);
					}
				}
			}
		}
		return closestPoints;
	}
	
	public float distanceTo(GridPoint p) {
		float value = (float)Math.sqrt(Math.pow(p.getLocation()[0]-location[0], 2) + Math.pow(p.getLocation()[1]-location[1], 2) + Math.pow(p.getLocation()[2]-location[2], 2));
		return value;
	}
	public boolean equals(Object other) {
		GridPoint gp = (GridPoint)other;
		float[] p =  gp.getLocation();
		if (p[0] == location[0] && p[1] == location[1] && p[2] == location[2]) {
			return true;
		}
		return false;
	}
	public float[] getLocation() {
		return location;
	}
	public String toString() {
		return "("+location[0]+", "+location[1]+", "+location[2]+")";
	}
}
