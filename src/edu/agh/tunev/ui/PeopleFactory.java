package edu.agh.tunev.ui;

import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.Person;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<Person> random(int num, double maxX, double maxY) {
		Vector<Person> r = new Vector<Person>();

		for (int i = 0; i < num; i++)
			r.add(new Person(rng.nextDouble() * maxX, rng.nextDouble() * maxY));

		return r;
	}

}
