package edu.agh.tunev.world;

import java.io.File;
import java.util.Vector;

import edu.agh.tunev.model.AbstractMovable;

public class World {

	// -- world-data access methods

	public double getXDimension() {
		return data.getXDimension();
	}

	public double getYDimension() {
		return data.getYDimension();
	}

	public Vector<Exit> getExits() {
		return data.getExits();
	}

	public Vector<Obstacle> getObstacles() {
		return data.getObstacles();
	}

	public Physics getPhysicsAt(double t, double x, double y) {
		return data.getPhysicsAt(t, x, y);
	}

	// -- end of world-data access methods

	public interface ProgressCallback {
		void update(int done, int total, String msg);
	}

	private AbstractDataSource data;

	public void readData(File dir, ProgressCallback callback) {
		if (data != null)
			throw new IllegalArgumentException(
					"World.readData() already called on this World");
		
		data = new FDSDataSource();
		data.readData(dir, callback);
	}
	
	private MovableInterpolator interpolator = new MovableInterpolator();

	public void saveMovableState(AbstractMovable movable, double t,
			AbstractMovable.State state) {
		interpolator.addDiscreetState(movable, t, state);
	}
	
	public AbstractMovable.State getMovableState(AbstractMovable movable, double t) {
		return interpolator.getInterpolatedState(movable, t);
	}

}
