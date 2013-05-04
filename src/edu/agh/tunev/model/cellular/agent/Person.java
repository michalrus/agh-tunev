package edu.agh.tunev.model.cellular.agent;

import java.awt.geom.Point2D;
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
			int index = (int) (Math.random() * 8);
			return values[index];
		}

		/**
		 * Agent needs to turn toward a cell before he moves. This method maps a
		 * neighbour index (check {@link Cell#positionToIndex(Cell, Cell)}) of
		 * that cell to the anticipated orientation of an agent.
		 * 
		 * @param index
		 * @return
		 * @throws NeighbourIndexException
		 */
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

		/**
		 * Check if an orientation belongs to {{@code W,SW,S,SE}} and if so -
		 * maps it to an orientation laying on the same axis but directed
		 * inversly, eg. S -> N; SW -> NE.
		 * 
		 * @param orient
		 * @return opposite orientation
		 */
		public static Orientation translateOrient(Orientation orient) {
			int index = getIndexOf(orient);
			int translatedIndex = index;
			Orientation[] values = values();

			if (index >= values.length / 2)
				translatedIndex = (index + (values.length / 2)) % values.length;

			return values[translatedIndex];
		}
	}

	// TODO: discard unnecessary fields
	private Cell cell;
	private PersonState currentState;
	private Orientation orientation;
	private final AllowedConfigs allowedConfigs;
	public final PersonProfile profile;

	public Person(PersonProfile _profile, Cell _cell,
			AllowedConfigs _allowedConfigs) throws WrongOrientationException {
		this.profile = _profile;
		this.cell = _cell;
		cell.setPerson(this);
		this.allowedConfigs = _allowedConfigs;
		this.orientation = Orientation.randomizeOrient();
		saveState();
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

	public void update() throws NeighbourIndexException,
			WrongOrientationException {
		Cell destination = selectField();
		Orientation orient = turnTowardCell(destination);
		orientation = orient;

		cell.release();
		cell = destination;
		cell.setPerson(this);

		saveState();
	}

	//TODO: remove currentState field, refactor function below
	private void saveState() throws WrongOrientationException {
		Point2D.Double position = Cell.d2c(cell.getPosition());
		Double numOrient = orientToAngle(orientation);
		PersonState.Movement movement = PersonState.Movement.STANDING; // TODO:
																		// adjusting
																		// pose
																		// to
																		// external
																		// conditions

		currentState = new PersonState(position, numOrient, movement);
	}

	private Cell selectField() throws NeighbourIndexException {
		List<Cell> neighbours = cell.getNeighbours();
		Cell selectedField = this.cell;
		Double lowestPotential = evaluateCostFunc(this.cell);

		for (Cell neighbour : neighbours) {
			Double neighbourPotential = getFieldPotential(neighbour);
			if (neighbourPotential < lowestPotential) {
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
	private Double evaluateCostFunc(Cell neighbour) {
		Double dist = evaluateDistComponent(neighbour);	
		return neighbour.getStaticFieldVal() + dist;
	}
	
	private Double evaluateDistComponent(Cell neighbour){
		Double dist = 0.0;
		
		if(!neighbour.equals(this.cell)){
			Point2D.Double neighbourRealPosition = neighbour.getRealPosition();
			Point2D.Double baseRealPosition = this.cell.getRealPosition();
			dist = baseRealPosition.distance(neighbourRealPosition);
		} else
			dist = Math.sqrt(2)*Cell.CELL_SIZE;
		
		return dist;
	}

	/**
	 * Checks if coming onto {@code cell} is possible.
	 * 
	 * @param c
	 * @return
	 * @throws NeighbourIndexException
	 */
	private boolean checkFieldAvailability(Cell c)
			throws NeighbourIndexException {
		if (c.isOccupied())
			return false;

		boolean cellAvailability = allowedConfigs.checkCellAvailability(c,
				turnTowardCell(c));

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
