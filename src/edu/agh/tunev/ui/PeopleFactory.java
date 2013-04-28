package edu.agh.tunev.ui;

import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.Person;
import edu.agh.tunev.world.World;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<Person> random(World world, int num) {
		Vector<Person> r = new Vector<Person>();

		for (int i = 0; i < num; i++)
			r.add(new Person(world, rng.nextDouble() * world.getXDimension(),
					rng.nextDouble() * world.getYDimension()));

		return r;
	}

}
