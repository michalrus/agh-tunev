package edu.agh.tunev.interpolation;

import java.util.Map;
import java.util.Map.Entry;
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
		NavigableMap<Double, State> states = data.get(movable);
		if (states == null)
			return null;

		Entry<Double, State> prev = states.floorEntry(t);
		Entry<Double, State> next = states.ceilingEntry(t);
		
		if (prev == null && next == null)
			return null;
		else if (prev == null)
			return next.getValue();
		else if (next == null)
			return prev.getValue();
		else if (prev == next)
			return prev.getValue();
		
		// splajny 1-go stopnia? bêdzie git
		
		double ratio = (t - prev.getKey()) / (next.getKey() - prev.getKey());
		
		double x = prev.getValue().x + ratio * (next.getValue().x - prev.getValue().x);
		double y = prev.getValue().y + ratio * (next.getValue().y - prev.getValue().y);
		
		return new State(x, y);
	}

	public class State {
		public final double x;
		public final double y;

		public State(AbstractMovable movable) {
			this.x = movable.getX();
			this.y = movable.getY();
		}
		
		public State(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	public Interpolator() {
		data = new ConcurrentHashMap<AbstractMovable, NavigableMap<Double, State>>();
	}

	private final Map<AbstractMovable, NavigableMap<Double, State>> data;

}
