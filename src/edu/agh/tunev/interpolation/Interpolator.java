package edu.agh.tunev.interpolation;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import edu.agh.tunev.model.AbstractMovable;

public final class Interpolator {

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * @param t
	 *            Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(AbstractMovable movable, double t) {
		NavigableMap<Double, State> states = data.get(movable);
		if (states == null) {
			states = new ConcurrentSkipListMap<Double, State>();
			data.put(movable, states);
		}

		states.put(t, new State(movable));
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

	public Interpolator() {
		data = new ConcurrentHashMap<AbstractMovable, NavigableMap<Double, State>>();
	}

	private final Map<AbstractMovable, NavigableMap<Double, State>> data;

}
