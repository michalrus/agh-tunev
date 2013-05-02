package edu.agh.tunev.model.cellular;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.util.ShapeUtilities;

import edu.agh.tunev.model.Common;
import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.model.cellular.agent.WrongOrientationException;

/**
 * <pre>
 * Gathers information concerning possible configurations between two agents.
 * There are 4 possible {@link Person.Orientation} for each of them, and 8 positions for the
 * neighbouring cell. 
 * Initialization is a bit complicated due to optimization
 * reasons. 
 * An instance of this class stores the data for particular board and
 * agent parameters.
 * </pre>
 * 
 */
class AllowedConfigs {

	private final Map<ConfigKey, Double> intersectionMap;
	private final Double personWidth;
	private final Double personGirth;
	private final Double cellSize;

	public AllowedConfigs(Double _personWidth, Double _personGirth,
			Double _cellSize) throws NeighbourIndexException,
			WrongOrientationException {
		intersectionMap = new HashMap<ConfigKey, Double>();
		this.personWidth = _personWidth;
		this.personGirth = _personGirth;
		this.cellSize = _cellSize;

		List<Person.Orientation> orientValues = Arrays
				.asList(Person.Orientation.values());
		int valuesLength = orientValues.size();

		// first Math.ceil(n/2) values
		ArrayList<Person.Orientation> consideredValues = new ArrayList<Person.Orientation>(
				orientValues.subList(0, (int) valuesLength / 2));

		// the rest of them
		/*
		 * ArrayList<Person.Orientation> cloneableValues = new ArrayList(
		 * orientValues.subList((int) valuesLength / 2 + 1, valuesLength - 1));
		 */

		int[] indexes = { 1, 2 };
		insertEntries(consideredValues, indexes, consideredValues);
	}

	/**
	 * Calculates and inserts values for a given range of key values.
	 * 
	 * @param selfOrients
	 * @param neighbourIndexes
	 * @param neighbourOrients
	 * @throws NeighbourIndexException
	 * @throws WrongOrientationException
	 */
	private void insertEntries(ArrayList<Person.Orientation> selfOrients,
			int[] neighbourIndexes,
			ArrayList<Person.Orientation> neighbourOrients)
			throws NeighbourIndexException, WrongOrientationException {

		for (Person.Orientation selfOrient : selfOrients)
			for (Integer neighbourIndex : neighbourIndexes)
				for (Person.Orientation neighbourOrient : neighbourOrients) {
					Double intersection = countIntersection(neighbourIndex,
							selfOrient, neighbourOrient);
					ConfigKey key = new ConfigKey(selfOrient, neighbourIndex,
							neighbourOrient);
					intersectionMap.put(key, intersection);
				}
	}

	/**
	 * Selects proper calculating method taking into account a position of
	 * neighbouring cell.
	 * 
	 * @param orientValues
	 *            considered orientations of an agent
	 * @param cellSize
	 * @param neighbourIndex
	 * @throws NeighbourIndexException
	 * @throws WrongOrientationException
	 */
	private Double countIntersection(int neighbourIndex,
			Person.Orientation selfOrient, Person.Orientation neighbourOrient)
			throws NeighbourIndexException, WrongOrientationException {
		
		//closer ones
		if ((neighbourIndex % 2) == 1)
			return countEllipseIntersection(0.0, cellSize, selfOrient,
					neighbourOrient);
		//diagonal ones
		else if (neighbourIndex % 2 == 0) {
			Double dist = cellSize / Math.sqrt(2);
			return countEllipseIntersection(dist, dist, selfOrient,
					neighbourOrient);
		} else
			throw new NeighbourIndexException();
	}

	/**
	 * Calculates an intersection area between two ellipses using
	 * {@link Common#calculateIntersection}
	 * 
	 * @param neighbourX
	 * @param neighbourY
	 * @param selfOrient
	 * @param neighbourOrient
	 * @return
	 * @throws WrongOrientationException
	 */
	private Double countEllipseIntersection(Double neighbourX,
			Double neighbourY, Person.Orientation selfOrient,
			Person.Orientation neighbourOrient)
			throws WrongOrientationException {

		//TODO: angle calc in createEllipse works in a wrong way
		Double angle1 = Person.orientToAngle(selfOrient);
		Double angle2 = Person.orientToAngle(neighbourOrient);
		Shape ellipse1 = Common.createEllipse(0.0, 0.0, personWidth, personGirth, angle1);
		Shape ellipse2 = Common.createEllipse(neighbourX, neighbourY,
				personWidth, personGirth, angle2);
		
		return Common.intersectionArea(ellipse1, ellipse2);
	}

	// TODO: wyjebać
	public void printIntersectionMap() {
		int i = 0;
		for (Entry e : intersectionMap.entrySet()) {
			ConfigKey k = (ConfigKey) e.getKey();
			Double v = (double) e.getValue();
			System.out.println("Key: " + k.getSelfOrient() + " | "
					+ k.getNeighbourIndex() + " | " + k.getNeighbourOrient());
			System.out.println(v);
			System.out.println(++i);
			System.out.println();
		}
	}

	public Double getPersonWidth() {
		return personWidth;
	}

	public Double getPersonGirth() {
		return personGirth;
	}

	public Double getCellSize() {
		return cellSize;
	}

	/**
	 * A triple valued key to determine an intersection area for a given agent
	 * configuration.
	 */
	private final class ConfigKey {
		private final Person.Orientation selfOrient;
		private final Integer neighbourIndex;
		private final Person.Orientation neighbourOrient;

		/**
		 * @param _selfOrient
		 *            agent orientation
		 * @param _neighbourIndex
		 *            one of neighbours (from 1 to 8)
		 * @param _neighbourOrient
		 *            neighbour orientation
		 */
		public ConfigKey(Person.Orientation _selfOrient,
				Integer _neighbourIndex, Person.Orientation _neighbourOrient) {
			this.selfOrient = _selfOrient;
			this.neighbourIndex = _neighbourIndex;
			this.neighbourOrient = _neighbourOrient;
		}

		@Override
		// Based on key values
		// Sponsored by Eclipse
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConfigKey other = (ConfigKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (neighbourIndex == null) {
				if (other.neighbourIndex != null)
					return false;
			} else if (!neighbourIndex.equals(other.neighbourIndex))
				return false;
			if (neighbourOrient != other.neighbourOrient)
				return false;
			if (selfOrient != other.selfOrient)
				return false;
			return true;
		}

		@Override
		// Based on key values
		// Sponsored by Eclipse
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((neighbourIndex == null) ? 0 : neighbourIndex.hashCode());
			result = prime
					* result
					+ ((neighbourOrient == null) ? 0 : neighbourOrient
							.hashCode());
			result = prime * result
					+ ((selfOrient == null) ? 0 : selfOrient.hashCode());
			return result;
		}

		public Person.Orientation getSelfOrient() {
			return selfOrient;
		}

		public Integer getNeighbourIndex() {
			return neighbourIndex;
		}

		public Person.Orientation getNeighbourOrient() {
			return neighbourOrient;
		}

		private AllowedConfigs getOuterType() {
			return AllowedConfigs.this;
		}

	}
}
