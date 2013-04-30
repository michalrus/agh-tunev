package edu.agh.tunev.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import edu.agh.tunev.model.AbstractModel.MovableState;

public final class Interpolator {

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * @param t
	 *            Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(AbstractMovable movable, double t) {
		NavigableMap<Double, MovableState> states = data.get(movable);
		if (states == null) {
			states = new ConcurrentSkipListMap<Double, MovableState>();
			data.put(movable, states);
		}

		states.put(t, new MovableState(movable));
	}

	public MovableState getState(AbstractMovable movable, double t) {
		NavigableMap<Double, MovableState> states = data.get(movable);
		if (states == null)
			return null;

		Entry<Double, MovableState> prev = states.floorEntry(t);
		Entry<Double, MovableState> next = states.ceilingEntry(t);
		
		if (prev == null && next == null)
			return null;
		else if (prev == null)
			return next.getValue();
		else if (next == null)
			return prev.getValue();
		else if (prev == next)
			return prev.getValue();
		
		// splajny 1-go stopnia? będzie git
		
		double ratio = (t - prev.getKey()) / (next.getKey() - prev.getKey());
		
		double x = prev.getValue().x + ratio * (next.getValue().x - prev.getValue().x);
		double y = prev.getValue().y + ratio * (next.getValue().y - prev.getValue().y);
		
		return new MovableState(x, y);
	}

	public Interpolator() {
		data = new ConcurrentHashMap<AbstractMovable, NavigableMap<Double, MovableState>>();
	}

	private final Map<AbstractMovable, NavigableMap<Double, MovableState>> data;

}
