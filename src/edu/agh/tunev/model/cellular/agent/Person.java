package edu.agh.tunev.model.cellular.agent;

import java.awt.Point;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.model.cellular.NeighbourIndexException;
import edu.agh.tunev.model.cellular.grid.Cell;

public final class Person {

	public enum Orientation {
		E, NE, N, NW, W, SW, S, SE;

		/**
		 * Returns index of a specific {@code Person.Orienation}
		 * 
		 * @param orient
		 * @return index of orientation
		 */
		public static int getIndexOf(Orientation orient) {
			Person.Orientation[] values = Person.Orientation.values();
			int ind;

			for (ind = 0; ind < values.length && values[ind] != orient; ++ind)
				;

			return ind;
		}

		/**
		 * Randomizes {@code Person.Orientation}
		 * 
		 * @return random orientation
		 */
		public static Orientation randomizeOrient() {
			Orientation[] values = values();
			int index = (int) Math.random() * 8;
			return values[index];
		}

		public static Orientation neighbourIndexToOrient(int index)
				throws NeighbourIndexException {
			switch (index) {
			case 0:
				return NW;
			case 1:
				return N;
			case 2:
				return NE;
			case 3:
				return W;
			case 4:
				return E;
			case 5:
				return SW;
			case 6:
				return S;
			case 7:
				return SE;
			default:
				throw new NeighbourIndexException();
			}
		}
	}

	private Cell cell;
	public final PersonProfile profile;

	public Person(PersonProfile profile, Cell _cell) {
		this.profile = profile;
		this.cell = _cell;
	}

	/**
	 * Maps {@code Orientation} to corresponding angle.
	 * 
	 * @param orient
	 * @return
	 * @throws WrongOrientationException
	 */
	public static Double orientToAngle(Orientation orient)
			throws WrongOrientationException {
		// starting at east
		Double angle = 0.0;
		Person.Orientation[] orientValues = Person.Orientation.values();
		int i;

		for (i = 0; i < orientValues.length && orient != orientValues[i]; ++i) {
			angle += 45.0;
		}

		if (i > 7)
			throw new WrongOrientationException();
		else
			return angle;
	}

	private Double evaluateFieldPotential() {
		// <michał> czy to bezpieczne? będziesz pamiętał żeby sprawdzać
		// wszędzie? może lepiej double i return Double.NaN?
		return null;
	}

	/**
	 * <pre>
	 * A snippet mapping position to neighbour index required in AllowedCfgs.
	 * Indexes:
	 * 
	 * <pre>
	 * A snippet mapping position to neighbour index required in AllowedCfgs.
	 * Indexes:
	 *  0   1   2
	 *  3       4
	 *  5   6   7
	 * 
	 * @param c
	 * @return
	 */
	private int positionToIndex(Cell c) {
		Point posOth = c.getPosition();
		Point posCell = this.cell.getPosition();

		// <michał> nie rozumiem tego poniżej, skąd te liczby :P
		return (posOth.x - posCell.x + 2) + 3
				* Math.abs(posOth.y - posCell.y - 2);
	}

	public Cell getCell() {
		return cell;
	}

	public double getOrientation() {
		return 0; // TODO
	}

	public PersonState.Movement getMovement() {
		return null; // TODO
	}

}
