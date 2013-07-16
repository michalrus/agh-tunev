/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
