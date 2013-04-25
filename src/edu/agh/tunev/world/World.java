package edu.agh.tunev.world;

import java.io.File;
import java.util.Vector;

import edu.agh.tunev.model.AbstractMovable;

public class World {

	public interface ProgressCallback {
		void update(int done, int total, String msg);
	}

	public void readData(File dir, ProgressCallback callback) {
	}

	public void saveState(AbstractMovable movable, double t, AbstractMovable.State state) {
	}
	
	public double getXDimension() {
		return 1000.0;
	}
	
	public double getYDimension() {
		return 20.0;
	}
	
	public Vector<Obstacle> getObstacles() {
		return new Vector<Obstacle>();
	}

}
