package edu.agh.tunev.model;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import edu.agh.tunev.model.AbstractModel.PersonState;

public final class Interpolator {

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * @param t
	 *            Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(AbstractPerson person, double t) {
		NavigableMap<Double, PersonState> states = data.get(person);
		if (states == null) {
			states = new ConcurrentSkipListMap<Double, PersonState>();
			data.put(person, states);
		}

		states.put(t, new PersonState(person));
	}

	public PersonState getState(AbstractPerson person, double t) {
		NavigableMap<Double, PersonState> states = data.get(person);
		if (states == null)
			return null;

		final Entry<Double, PersonState> prev = states.floorEntry(t);
		final Entry<Double, PersonState> next = states.ceilingEntry(t);
		
		if (prev == null && next == null)
			return null;
		else if (prev == null)
			return next.getValue();
		else if (next == null)
			return prev.getValue();
		else if (prev == next)
			return prev.getValue();
		
		// splajny 1-go stopnia? będzie git
		
		final double ratio = (t - prev.getKey()) / (next.getKey() - prev.getKey());

		final Point2D.Double p = prev.getValue().position;
		final Point2D.Double n = next.getValue().position;

		final double px = p.getX();
		final double py = p.getY();
		final double nx = n.getX();
		final double ny = n.getY();
		
		final double x = px + ratio * (nx - px);
		final double y = py + ratio * (ny - py);
		
		return new PersonState(new Point2D.Double(x, y));
	}

	public Interpolator() {
		data = new ConcurrentHashMap<AbstractPerson, NavigableMap<Double, PersonState>>();
	}

	private final Map<AbstractPerson, NavigableMap<Double, PersonState>> data;

}
