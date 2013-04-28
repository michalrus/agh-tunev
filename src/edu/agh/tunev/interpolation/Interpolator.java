package edu.agh.tunev.interpolation;

import edu.agh.tunev.model.AbstractMovable;

public class Interpolator {

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * Po ustaleniu this.x i this.y, wywo³aæ this.saveState(t).
	 * 
	 * @param t
	 *            Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(AbstractMovable movable, double t) {
		State state = new State(movable);
	}

	public State getState(AbstractMovable movable, double t) {
		// TODO Auto-generated method stub
		return null;
	}

	public class State {
		public final double x;
		public final double y;

		public State(AbstractMovable movable) {
			this.x = movable.getX();
			this.y = movable.getY();
		}
	}

}
