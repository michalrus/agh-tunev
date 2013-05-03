package edu.agh.tunev.ui;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.PersonProfile;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<PersonProfile> random(int num, Point2D.Double maxPosition) {
		Vector<PersonProfile> r = new Vector<PersonProfile>();

		for (int i = 0; i < num; i++)
			r.add(new PersonProfile(new Point2D.Double(rng.nextDouble()
					* maxPosition.x, rng.nextDouble() * maxPosition.y)));

		return r;
	}

}
