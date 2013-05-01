package edu.agh.tunev.ui;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.AbstractPerson;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<AbstractPerson> random(Class<?> type, int num,
			Point2D.Double max) {
		Vector<AbstractPerson> r = new Vector<AbstractPerson>();

		for (int i = 0; i < num; i++)
			r.add(newAbstractPerson(type, new Point2D.Double(rng.nextDouble()
					* max.x, rng.nextDouble() * max.y)));

		return r;
	}

	static AbstractPerson newAbstractPerson(Class<?> type, Point2D.Double p) {
		try {
			return (AbstractPerson) type.getDeclaredConstructor(
					Point2D.Double.class).newInstance(p);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error during instantiation of "
					+ type.getName() + ".");
		}
	}

}
