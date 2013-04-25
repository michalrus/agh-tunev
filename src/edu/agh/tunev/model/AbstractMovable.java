package edu.agh.tunev.model;

import edu.agh.tunev.world.World;

/**
 * Po tej klasie musi dziedziczyæ wszystko co siê rusza -- m.
 * 
 * Ale póki co chyba tylko model.Person. -- m.
 *
 */
public abstract class AbstractMovable {

	protected double x;
	protected double y;
	final protected World world;

	public AbstractMovable(World world) {
		this.world = world;
	}

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * Po ustaleniu this.x i this.y, wywo³aæ this.saveState(t).
	 * 
	 * @param t  Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(double t) {
		State state = new State();
		state.x = x;
		state.y = y;
		world.saveState(this, t, state);
	}
	
	public class State {
		public double x = 0.0;
		public double y = 0.0;
	}
	
}
