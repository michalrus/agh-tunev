package edu.agh.tunev.model.cellular;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.agh.tunev.model.Common;
import edu.agh.tunev.model.cellular.agent.NotANeighbourException;
import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.model.cellular.agent.Person.Orientation;
import edu.agh.tunev.model.cellular.agent.WrongOrientationException;
import edu.agh.tunev.model.cellular.grid.Cell;

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
// TODO: move to cellular.agent (?)
public class AllowedConfigs {

	private final Map<ConfigKey, Double> intersectionMap;
	private final Double personWidth;
	private final Double personGirth;
	private final Double cellSize;
	private final Double tolerance;

	/**
	 * Maps agent configurations and respective intersection areas.
	 * 
	 * @param _personWidth
	 *            average width of {@code Person}
	 * @param _personGirth
	 *            averge girth of {@code Person}
	 * @param _cellSize
	 * @param _tolerance
	 *            maximum allowed (are of intersection / area of ellipse) ratio
	 * @throws NeighbourIndexException
	 * @throws WrongOrientationException
	 */
	public AllowedConfigs(Double _personWidth, Double _personGirth,
			Double _cellSize, Double _tolerance)
			throws NeighbourIndexException, WrongOrientationException {
		intersectionMap = new HashMap<ConfigKey, Double>();
		this.personWidth = _personWidth;
		this.personGirth = _personGirth;
		this.cellSize = _cellSize;
		this.tolerance = _tolerance
				* Common.ellipseArea(personWidth, personGirth);

		List<Person.Orientation> orientValues = Arrays
				.asList(Person.Orientation.values());
		int valuesLength = orientValues.size();

		// first Math.ceil(n/2) values
		ArrayList<Person.Orientation> consideredValues = new ArrayList<Person.Orientation>(
				orientValues.subList(0, (int) valuesLength / 2));

		int[] indexes = { 1, 2 };
		generateMap(consideredValues, indexes, consideredValues);
	}

	/**
	 * Checks if move to a cell is possible for certain agent orientation.
	 * 
	 * @param cell
	 * @param selfOrient
	 * @return true if possible, false otherwise
	 * @throws NeighbourIndexException
	 * @throws NotANeighbourException
	 */
	public boolean checkCellAvailability(Cell cell,
			Person.Orientation selfOrient, Person agent) throws NeighbourIndexException,
			NotANeighbourException {

		List<Cell> occupiedNeighbours = cell.getOccupiedNeighbours();
		selfOrient = Person.Orientation.translateOrient(selfOrient);

		for (Cell neighbour : occupiedNeighbours) {
			Person neighPerson = neighbour.getPerson();
			if(neighPerson.equals(agent))
				continue;
			
			int neighbourIndex = Cell.positionToIndex(cell, neighbour);
			Orientation neighbourOrient = neighPerson.getOrientation();
			neighbourOrient = Person.Orientation
					.translateOrient(neighbourOrient);

			boolean configFeasibility = checkConfigFeasibility(selfOrient,
					neighbourIndex, neighbourOrient);

			if (!configFeasibility)
				return false;
		}

		return true;
	}

	/**
	 * Checks feasibility of a specific agent configuration.
	 * 
	 * @param selfOrient
	 * @param neighbourIndex
	 * @param neighbourOrient
	 * @return true if config possible, false otherwise
	 */
	private boolean checkConfigFeasibility(Person.Orientation selfOrient,
			int neighbourIndex, Person.Orientation neighbourOrient) {
		ConfigKey key = new ConfigKey(selfOrient, neighbourIndex,
				neighbourOrient);
		Double intersection = intersectionMap.get(key);

		if (intersection < tolerance)
			return true;

		return false;
	}

	/**
	 * Generates {@code intersectionMap} by inserting values and their symmetric
	 * deflections for a given range of keys.
	 * 
	 * @param selfOrients
	 *            range of agent orientations
	 * @param neighbourIndexes
	 *            base of neighbour indexes (2 indexes of neighbouring cells are
	 *            sufficient to generate a whole map)
	 * @param neighbourOrients
	 *            range of neighbour orientations
	 * @throws NeighbourIndexException
	 * @throws WrongOrientationException
	 */
	private void generateMap(ArrayList<Person.Orientation> selfOrients,
			int[] neighbourIndexes,
			ArrayList<Person.Orientation> neighbourOrients)
			throws NeighbourIndexException, WrongOrientationException {

		for (Person.Orientation selfOrient : selfOrients)
			for (Integer neighbourIndex : neighbourIndexes)
				for (Person.Orientation neighbourOrient : neighbourOrients) {

					insertEntries(new ConfigKey(selfOrient, neighbourIndex,
							neighbourOrient));
				}
	}

	/**
	 * Calculates the intersection area for {@code key}, creates its symmetric
	 * deflections and puts this stuff into {@code intersectionMap}
	 * 
	 * @param orientValues
	 *            considered orientations of an agent
	 * @param cellSize
	 *            length of cell side
	 * @param neighbourIndex
	 * @throws NeighbourIndexException
	 * @throws WrongOrientationException
	 */
	private void insertEntries(ConfigKey key) throws NeighbourIndexException,
			WrongOrientationException {

		Integer neighbourIndex = key.getNeighbourIndex();
		Person.Orientation selfOrient = key.getSelfOrient();
		Person.Orientation neighbourOrient = key.getNeighbourOrient();

		Double intersection = 0.0;
		List<ConfigKey> keys = new ArrayList<ConfigKey>();
		ArrayList<ConfigKey> symmetries = new ArrayList<ConfigKey>();
		keys.add(key);

		// closer ones
		if ((neighbourIndex % 2) == 1) {
			intersection = countEllipseIntersection(0.0, cellSize, selfOrient,
					neighbourOrient);
			symmetries = deflectParallelSymmetries(key);
		}
		// diagonal ones
		else if (neighbourIndex % 2 == 0) {
			intersection = countEllipseIntersection(cellSize, cellSize,
					selfOrient, neighbourOrient);
			symmetries = deflectDiagSymmetries(key);
		} else
			throw new NeighbourIndexException();

		keys.addAll(symmetries);
		for (ConfigKey k : keys)
			intersectionMap.put(k, intersection);
	}

	/**
	 * Creates a list of configurations characterized by the same value of
	 * intersection area as {@code baseKey}. Works for neighbours with even
	 * indexes (placed on diagonal axes).
	 * 
	 * @param baseKey
	 * @param val
	 *            intersection area
	 * @return
	 */
	private ArrayList<ConfigKey> deflectDiagSymmetries(ConfigKey baseKey) {
		ArrayList<ConfigKey> keys = new ArrayList<ConfigKey>();

		Person.Orientation baseSelfOrient = baseKey.getSelfOrient();
		Person.Orientation baseNeighbourOrient = baseKey.getNeighbourOrient();

		// lopsided axis
		// neigbour index == 0
		Person.Orientation selfOrient0 = translateOrient(baseSelfOrient,
				Person.Orientation.NE);
		Person.Orientation neighbourOrient0 = translateOrient(
				baseNeighbourOrient, Person.Orientation.NE);
		keys.add(new ConfigKey(selfOrient0, 0, neighbourOrient0));

		// neighbour index == 7 -> same as index == 0
		keys.add(new ConfigKey(selfOrient0, 7, neighbourOrient0));

		// same axis
		// neighbour index == 5
		keys.add(new ConfigKey(baseSelfOrient, 5, baseNeighbourOrient));

		return keys;
	}

	/**
	 * Creates a list of configurations characterized by the same value of
	 * intersection area as {@code baseKey}. Works for neighbours with odd
	 * indexes (placed on parallel or perpendicular axes).
	 * 
	 * @param baseKey
	 * @param val
	 * @return
	 */
	private ArrayList<ConfigKey> deflectParallelSymmetries(ConfigKey baseKey) {
		ArrayList<ConfigKey> keys = new ArrayList<ConfigKey>();

		Person.Orientation baseSelfOrient = baseKey.getSelfOrient();
		Person.Orientation baseNeighbourOrient = baseKey.getNeighbourOrient();

		// lopsided axis
		// neigbour index == 3
		Person.Orientation selfOrient0 = translateOrient(baseSelfOrient,
				Person.Orientation.E);
		Person.Orientation neighbourOrient0 = translateOrient(
				baseNeighbourOrient, Person.Orientation.E);
		keys.add(new ConfigKey(selfOrient0, 3, neighbourOrient0));

		// neighbour index == 4 -> same as index == 3
		keys.add(new ConfigKey(selfOrient0, 4, neighbourOrient0));

		// same axis
		// neighbour index == 6
		keys.add(new ConfigKey(baseSelfOrient, 6, baseNeighbourOrient));

		return keys;

	}

	/**
	 * Compares {@code baseOrient} and {@code asymetricOrient}. Returns switched
	 * or intact {@code baseOrient} depending on the comparison result.
	 * 
	 * @param baseOrient
	 * @param asymetricOrient
	 * @return
	 */
	private Person.Orientation translateOrient(Person.Orientation baseOrient,
			Person.Orientation asymetricOrient) {

		if (baseOrient == asymetricOrient
				|| baseOrient == switchOrient(asymetricOrient))
			return switchOrient(baseOrient);
		else
			return baseOrient;
	}

	private Person.Orientation switchOrient(Person.Orientation orient) {
		Person.Orientation[] values = Person.Orientation.values();
		int ind = Person.Orientation.getIndexOf(orient);

		return values[(ind + 2) % (values.length / 2)];
	}

	/**
	 * Calculates an intersection area between two ellipses using
	 * {@link Common#intersectionArea}
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

		// TODO: angle calc in createEllipse works in a wrong way
		Double angle1 = Person.orientToAngle(selfOrient);
		Double angle2 = Person.orientToAngle(neighbourOrient);
		Shape ellipse1 = Common.createEllipse(new Point2D.Double(0.0, 0.0),
				personWidth, personGirth, angle1);
		Shape ellipse2 = Common.createEllipse(new Point2D.Double(neighbourX,
				neighbourY), personWidth, personGirth, angle2);

		return Common.intersectionArea(ellipse1, ellipse2);
	}

	// TODO: wyjebać
	public void printIntersectionMap() {
		int i = 0;
		for (Entry<ConfigKey, Double> e : intersectionMap.entrySet()) {
			ConfigKey k = (ConfigKey) e.getKey();
			Double v = (double) e.getValue() / tolerance;
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
