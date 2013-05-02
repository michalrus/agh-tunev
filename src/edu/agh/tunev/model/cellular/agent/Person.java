package edu.agh.tunev.model.cellular.agent;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.agh.tunev.model.AbstractPerson;
import edu.agh.tunev.model.cellular.grid.Cell;

public final class Person extends AbstractPerson {

	public enum Orientation {
		E, NE, N, NW, W, SW, S, SE;

		public static int getIndexOf(Orientation orient) {
			Person.Orientation[] values = Person.Orientation.values();
			int ind;

			for (ind = 0; ind < values.length && values[ind] != orient; ++ind)
				;

			return ind;
		}
	}

	private Cell cell;

	public Person(Cell _cell) {
		super(Cell.d2c(_cell.getPosition()));
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
		return null;
	}

	/**
	 * <pre>
	 * A snippet mapping position to neighbour index required in AllowedCfgs.
	 * Indexes:
=======
	 * <pre> A snippet mapping position to neighbour index required in AllowedCfgs.
	 *	Indexes:
>>>>>>> THEIRS
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
		
		return (posOth.x - posCell.x + 2) + 3
				* Math.abs(posOth.y - posCell.y - 2);
	}

}
