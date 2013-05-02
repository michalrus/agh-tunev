package edu.agh.tunev.model.cellular.grid;

import java.awt.Point;

import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.world.Physics;
import edu.agh.tunev.world.Physics.Type;

public final class Cell {

	/** side of a cell represented by square */
	private final static double CELL_SIZE = 0.25;

	/** Physics coefficient useful for static field value evaluation */
	private final static double PHYSICS_COEFF = 0.2; // TODO: set

	private final Board board;
	private final Point position;
	private Person person = null;
	private Physics physics = null;
	private Double staticFieldVal;
	private int distToExit;

	public Cell(Point _position, Board _board) {
		this.position = _position;
		this.board = _board;
	}
	
	public boolean isOccupied(){
		return (person != null);
	}

	public void update(Physics phys){
		setPhysics(phys);
		calculateDistToExit();
		evaluateStaticFieldVal();	
	}
	
	// TODO: change formula
	private void evaluateStaticFieldVal() {
		staticFieldVal = PHYSICS_COEFF
				* (physics.get(Physics.Type.TEMPERATURE)) + distToExit;
	}

	private void calculateDistToExit() {
		// TODO:
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Physics getPhysics() {
		return physics;
	}

	public void setPhysics(Physics physics) {
		this.physics = physics;
	}

	public Board getBoard() {
		return board;
	}

	public Point getPosition() {
		return position;
	}

	public Double getStaticFieldVal() {
		return staticFieldVal;
	}

}
