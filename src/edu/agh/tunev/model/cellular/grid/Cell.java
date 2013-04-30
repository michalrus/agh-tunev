package edu.agh.tunev.model.cellular.grid;

import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.world.Physics;

public final class Cell {

	private Person person = null;
	private Physics physics = null;

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

}
