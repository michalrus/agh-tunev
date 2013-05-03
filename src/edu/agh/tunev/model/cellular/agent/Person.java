package edu.agh.tunev.model.cellular.agent;

import java.util.List;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.model.cellular.AllowedConfigs;
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
	private PersonState currentState;
	private Orientation orientation;
	private final AllowedConfigs allowedConfigs;
	public final PersonProfile profile;

	public Person(PersonProfile _profile, Cell _cell,
			AllowedConfigs _allowedConfigs) {
		this.profile = _profile;
		this.cell = _cell;
		this.allowedConfigs = _allowedConfigs;
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

	private Cell selectField() throws NeighbourIndexException {
		List<Cell> neighbours = cell.getCellNeighbours();
		Cell selectedField = this.cell;
		Double lowestPotential = evaluateCostFunc(this.cell);

		for(Cell neighbour : neighbours){
			Double neighbourPotential = getFieldPotential(neighbour);
			if(neighbourPotential < lowestPotential){
				selectedField = neighbour;
				lowestPotential = neighbourPotential;
			}
		}
		
		return selectedField;
	}

	
	private Double getFieldPotential(Cell c) throws NeighbourIndexException {
		if (checkFieldAvailability(c))
			return evaluateCostFunc(c);

		return Double.MAX_VALUE;
	}
	
	// TODO:change cost function, adjust to social dist model;
	private Double evaluateCostFunc(Cell c){
		return c.getStaticFieldVal();
	}

	/**
	 * Checks if coming onto {@code cell} is possible.
	 * 
	 * @param cell
	 * @return
	 * @throws NeighbourIndexException
	 */
	private boolean checkFieldAvailability(Cell cell)
			throws NeighbourIndexException {
		if (cell.isOccupied())
			return false;

		boolean cellAvailability = allowedConfigs.checkCellAvailability(cell,
				turnTowardCell(cell));

		if (!cellAvailability)
			return false;

		// TODO: check for obstacles

		return true;
	}

	private Orientation turnTowardCell(Cell c) throws NeighbourIndexException {
		int index = Cell.positionToIndex(this.cell, c);
		return Orientation.neighbourIndexToOrient(index);
	}

	public Cell getCell() {
		return cell;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public PersonState.Movement getMovement() {
		return null; // TODO
	}

	public PersonState getCurrentState() {
		return currentState;
	}

}
