package edu.agh.tunev.model;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class Interpolator {

	public static class PersonState {
		public final Point2D.Double position;
		public final double orientation;
		public final AbstractPerson.Movement movement;
	
		public PersonState(AbstractPerson person) {
			position = person.getPosition();
			orientation = person.getOrientation();
			movement = person.getMovement();
		}
		
		public PersonState(Point2D.Double position, double orientation, AbstractPerson.Movement movement) {
			this.position = position;
			this.orientation = orientation;
			this.movement = movement;
		}
	}

	/**
	 * Zapisuje dyskretny stan w interpolatorze. -- m.
	 * 
	 * @param t
	 *            Dana chwila czasu dla jakiej zapisujemy stan.
	 */
	public void saveState(AbstractPerson person, double t) {
		NavigableMap<Double, Interpolator.PersonState> states = data.get(person);
		if (states == null) {
			states = new ConcurrentSkipListMap<Double, Interpolator.PersonState>();
			data.put(person, states);
		}

		states.put(t, new Interpolator.PersonState(person));
	}

	public Interpolator.PersonState getState(AbstractPerson person, double t) {
		NavigableMap<Double, Interpolator.PersonState> states = data.get(person);
		if (states == null)
			return null;

		final Entry<Double, Interpolator.PersonState> prevEntry = states.floorEntry(t);
		final Entry<Double, Interpolator.PersonState> nextEntry = states.ceilingEntry(t);
		
		if (prevEntry == null && nextEntry == null)
			return null;
		else if (prevEntry == null)
			return nextEntry.getValue();
		else if (nextEntry == null)
			return prevEntry.getValue();
		else if (prevEntry == nextEntry)
			return prevEntry.getValue();

		final PersonState prev = prevEntry.getValue();
		final PersonState next = nextEntry.getValue();
		
		final double prevT = prevEntry.getKey();
		final double nextT = nextEntry.getKey();
		
		// splajny 1-go stopnia? będzie git
		
		final double ratio = (t - prevEntry.getKey()) / (nextEntry.getKey() - prevEntry.getKey());

		final Point2D.Double p = prev.position;
		final Point2D.Double n = next.position;

		final double px = p.getX();
		final double py = p.getY();
		final double nx = n.getX();
		final double ny = n.getY();
		
		final double x = px + ratio * (nx - px);
		final double y = py + ratio * (ny - py);
		
		return new Interpolator.PersonState(new Point2D.Double(x, y), prev.orientation, prev.movement);
	}

	public Interpolator() {
		data = new ConcurrentHashMap<AbstractPerson, NavigableMap<Double, Interpolator.PersonState>>();
	}

	private final Map<AbstractPerson, NavigableMap<Double, Interpolator.PersonState>> data;

}
