package edu.agh.tunev.ui;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.world.Obstacle;

final class PeopleFactory {

	static Random rng = new Random();

	//TODO: validation!
	static Vector<PersonProfile> random(int num,Point2D.Double minPosition, Point2D.Double maxPosition,
			Vector<Obstacle> obstacles) {
		Vector<PersonProfile> r = new Vector<PersonProfile>();
		double deltaX = Math.abs(maxPosition.x - minPosition.x);
		double deltaY = Math.abs(maxPosition.y - minPosition.y);

		for (int i = 0; i < num; i++) {
			Point2D.Double p;
			boolean insideAnyObstacle;
			do {
				p = new Point2D.Double(rng.nextDouble() * deltaX + minPosition.x,
						rng.nextDouble() * deltaY+minPosition.y);
				insideAnyObstacle = false;
				for (Obstacle o : obstacles)
					if (o.contains(p)) {
						insideAnyObstacle = true;
						break;
					}
			} while (insideAnyObstacle);
			r.add(new PersonProfile(p));
		}

		return r;
	}
	
	/*
	 * metoda generacji z poprzedniego modelu (kkm)
	 */
	static Vector<PersonProfile> nearObstacles(Vector<Obstacle> obstacles) {
		
		Vector<PersonProfile> r = new Vector<PersonProfile>();
		for (Obstacle ob : obstacles) {
			Point2D.Double start = ob.p1;
			Point2D.Double end = ob.p2;
			double veh_len = end.y - start.y;
			int passengers = rng.nextInt(3) + 1; 
			//int passengers = 1;

			for (int i = 0; i < passengers; ++i) {
				Point2D.Double coord = (i % 2 == 0) ? new Point2D.Double(start.x - 2
						* PersonProfile.WIDTH, start.y + (i / 2) * (veh_len / 2)
						+ 2 * PersonProfile.WIDTH) : new Point2D.Double(end.x + 2
						* PersonProfile.WIDTH, start.y + (i / 2) * (veh_len / 2)
						+ 2 * PersonProfile.WIDTH);
				r.add(new PersonProfile(coord));
			}
		}
		
		return r;
	}


	private PeopleFactory() {
	}

}
