package edu.agh.tunev.world;

import java.io.File;
import java.util.Vector;

import edu.agh.tunev.model.AbstractMovable;

public class World {

	// -- world-data access methods
	
	public double getDuration() {
		return data.getDuration();
	}

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

	/**
	 * Zawsze zwróci jak¹œ wartoœæ. Kiedy zapytasz o coœ spoza granic œwiata,
	 * zwróci wartoœæ z najbli¿szego punktu le¿¹cego na brzegu.
	 * 
	 * Przyk³ad u¿ycia:
	 * 
	 * <pre><code>
	 * Physics p = world.getPhysicsAt(13.33, 0.0, 1.0); 
	 * double temp = p.get(Physics.Type.TEMPERATURE);
	 * double co = p.get(Physics.Type.CO);
	 * </code></pre>
	 * 
	 * @param t
	 *            Rzeczywisty czas, o który pytasz.
	 * @param x
	 *            Pozycja na OX.
	 * @param y
	 *            Pozycja na OY.
	 * @return
	 */
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

	public AbstractMovable.State getMovableState(AbstractMovable movable,
			double t) {
		return interpolator.getInterpolatedState(movable, t);
	}

}
