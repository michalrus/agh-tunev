package edu.agh.tunev.ui;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.world.Obstacle;

final class PeopleFactory {

	static Random rng = new Random();

	static Vector<PersonProfile> random(int num, Point2D.Double maxPosition,
			Vector<Obstacle> obstacles) {
		Vector<PersonProfile> r = new Vector<PersonProfile>();

		for (int i = 0; i < num; i++) {
			Point2D.Double p;
			boolean insideAnyObstacle;
			do {
				p = new Point2D.Double(rng.nextDouble() * maxPosition.x,
						rng.nextDouble() * maxPosition.y);
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
	 * 
	public void initAgents() {
		for (Obstacle ob : obstacles) {
			Point start = ob.getStartPoint();
			Point end = ob.getEndPoint();
			double veh_len = end.y - start.y;
			int passengers = rng.nextInt(3) + 1;

			for (int i = 0; i < passengers; ++i) {
				Point coord = (i % 2 == 0) ? new Point(start.x - 2
						* Agent.BROADNESS, start.y + (i / 2) * (veh_len / 2)
						+ 2 * Agent.BROADNESS) : new Point(end.x + 2
						* Agent.BROADNESS, start.y + (i / 2) * (veh_len / 2)
						+ 2 * Agent.BROADNESS);
				agents.add(new Agent(this, coord));
			}
		}
	}
	 *
	 */

	private PeopleFactory() {
	}

}
