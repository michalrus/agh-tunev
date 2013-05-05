package edu.agh.tunev.ui;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.world.Obstacle;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<PersonProfile> random(int num, Point2D.Double maxPosition,
			Vector<Obstacle> obstacles) {
		Vector<PersonProfile> r = new Vector<PersonProfile>();
		Vector<Rectangle2D.Double> obstacleRectangles = new Vector<Rectangle2D.Double>();

		for (Obstacle o : obstacles) {
			final double x = Math.min(o.p1.x, o.p2.x);
			final double y = Math.min(o.p1.y, o.p2.y);
			final double w = Math.abs(o.p2.x - o.p1.x);
			final double h = Math.abs(o.p2.y - o.p1.y);
			obstacleRectangles.add(new Rectangle2D.Double(x, y, w, h));
		}

		for (int i = 0; i < num; i++) {
			Point2D.Double p;
			boolean insideAnyObstacle;
			do {
				p = new Point2D.Double(rng.nextDouble() * maxPosition.x,
						rng.nextDouble() * maxPosition.y);
				insideAnyObstacle = false;
				for (Rectangle2D.Double rect : obstacleRectangles)
					if (rect.contains(p)) {
						insideAnyObstacle = true;
						break;
					}
			} while (insideAnyObstacle);
			r.add(new PersonProfile(p));
		}

		return r;
	}

	private PeopleFactory() {
	}

}
