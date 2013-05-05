package edu.agh.tunev.model.cellular.agent;

import java.awt.geom.Point2D;
import java.util.List;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.model.PersonState.Movement;
import edu.agh.tunev.model.cellular.AllowedConfigs;
import edu.agh.tunev.model.cellular.NeighbourIndexException;
import edu.agh.tunev.model.cellular.grid.Cell;
import edu.agh.tunev.world.Physics.Type;
import edu.agh.tunev.world.Physics;

public final class Person {

	private final static int PERCEPTION_RANGE = 20;

	/** Physics coefficient useful for field value evaluation */
	private final static double PHYSICS_COEFF = 0.3; // TODO: set

	/** Distance coefficient useful for field value evaluation */
	private final static double DIST_COEFF = 1.0; // TODO: set

	private final static double STATIC_COEFF = 1.0; // TODO:

	private final static double DYNAMIC_COEFF = 1.0; // TODO:

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
		 * Check if an orientation belongs to {{@code W,SW,S,SE} and if so -
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
	private Movement pose;
	private boolean active;
	private final AllowedConfigs allowedConfigs;
	public final PersonProfile profile;

	public Person(PersonProfile _profile, Cell _cell,
			AllowedConfigs _allowedConfigs) throws WrongOrientationException {
		this.profile = _profile;
		this.cell = _cell;
		cell.setPerson(this);
		this.allowedConfigs = _allowedConfigs;
		this.orientation = Orientation.randomizeOrient();
		this.pose = profile.initialMovement;
		this.active = true;
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
			WrongOrientationException, NotANeighbourException {

		checkActivity();
		
		if (active) {
			Cell destination = selectField();
			Orientation orient = turnTowardCell(destination);
			orientation = orient;

			cell.release();
			cell = destination;
			cell.setPerson(this);
		}

		saveState();
	}
	
	private void checkActivity(){
		if(this.cell.isExit()){
			getThroughExit();
		}
	}
	
	private void getThroughExit(){
		active = false;
		pose = Movement.HIDDEN;
		this.cell.release();
	}

	// TODO: remove currentState field, refactor function below
	private void saveState() throws WrongOrientationException {
		Point2D.Double position = Cell.d2c(cell.getPosition());
		Double numOrient = orientToAngle(orientation);
		Movement movement = Movement.STANDING; // TODO:
																		// adjusting
																		// pose
																		// to
																		// external
																		// conditions

		currentState = new PersonState(position, numOrient, movement);
	}
	

	private Cell selectField() throws NeighbourIndexException,
			NotANeighbourException {
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

	private Double getFieldPotential(Cell c) throws NeighbourIndexException,
			NotANeighbourException {

		if (c.equals(this.cell))
			throw new NotANeighbourException();

		if (checkFieldAvailability(c))
			return evaluateCostFunc(c);

		return Double.MAX_VALUE;
	}

	// TODO:change cost function, adjust to social dist model;
	private Double evaluateCostFunc(Cell cell) throws NeighbourIndexException,
			NotANeighbourException {

		if (cell.equals(this.cell))
			return Double.MAX_VALUE;

		Double dist = evaluateDistComponent(cell);
		Double heat = evaluateHeatComponent(cell);
		return STATIC_COEFF * cell.getStaticFieldVal() + DYNAMIC_COEFF
				* (DIST_COEFF * dist + PHYSICS_COEFF * heat);
	}

	/**
	 * Calculates heat component of a neighbouring cell. Component is an average
	 * temperature in row of cells.
	 * 
	 * @param neighbour
	 * @return heat component
	 * @throws NeighbourIndexException
	 * @throws NotANeighbourException
	 */
	private Double evaluateHeatComponent(Cell neighbour)
			throws NeighbourIndexException, NotANeighbourException {

		if (neighbour.equals(this.cell))
			throw new NotANeighbourException();

		int neighbourIndex = Cell.positionToIndex(this.cell, neighbour);
		List<Cell> row = neighbour.getRow(neighbourIndex, PERCEPTION_RANGE);
		Double sum = 0.0;
		Double acc = 0.0;

		for (Cell c : row) {
			Physics phys = c.getPhysics();
			if (phys != null) {
				sum += c.getPhysics().get(Type.TEMPERATURE);
				++acc;
			}
		}

		if (acc == 0)
			return Double.MAX_VALUE;

		return sum / acc;
	}

	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	private Double evaluateDistComponent(Cell neighbour) {
		Double dist = 0.0;

		if (!neighbour.equals(this.cell)) {
			Point2D.Double neighbourRealPosition = neighbour.getRealPosition();
			Point2D.Double baseRealPosition = this.cell.getRealPosition();
			dist = baseRealPosition.distance(neighbourRealPosition);
		} else
			dist = Math.sqrt(2) * Cell.CELL_SIZE;

		return dist;
	}

	/**
	 * Checks if coming onto {@code cell} is possible.
	 * 
	 * @param c
	 * @return
	 * @throws NeighbourIndexException
	 * @throws NotANeighbourException
	 */
	private boolean checkFieldAvailability(Cell c)
			throws NeighbourIndexException, NotANeighbourException {
		if (c.isOccupied() || c.isBlocked())
			return false;

		boolean cellAvailability = allowedConfigs.checkCellAvailability(c,
				turnTowardCell(c));

		return cellAvailability;

	}

	private Orientation turnTowardCell(Cell c) throws NeighbourIndexException,
			NotANeighbourException {
		if (c.equals(this.cell))
			return orientation;

		int index = Cell.positionToIndex(this.cell, c);
		return Orientation.neighbourIndexToOrient(index);
	}
	

	public Movement getPose() {
		return pose;
	}

	public void setPose(Movement pose) {
		this.pose = pose;
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
