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

package edu.agh.tunev.model.jrakoczy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.agh.tunev.world.Exit;
import edu.agh.tunev.world.FireSource;
import edu.agh.tunev.world.Obstacle;
import edu.agh.tunev.world.Physics;
import edu.agh.tunev.world.World;

public class Board {
	
	public Point2D.Double getDimension() {
		return dimension;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void addAgent(Agent agent) {
		agents.add(agent);
	}

	public List<Exit> getExits() {
		return world.getExits();
	}

	public List<Obstacle> getObstacles() {
		return world.getObstacles();
	}

	public FireSource getNearestFireSrc(Point2D.Double p) {
		double min = Double.POSITIVE_INFINITY;
		FireSource nearest_src = null;

		for (FireSource src : fire_srcs) {
			double dist = src.distance(p);
			if (dist < min) {
				min = dist;
				nearest_src = src;
			}
		}

		return nearest_src;
	}

	public double getPhysics(Point2D.Double point, Physics.Type what) {
		return world.getPhysicsAt(t, point).get(what);
	}

	// ------------- internals start here, an Agent should not use those
	private Point2D.Double dimension;

	/** Central points of fire sources */
	private List<FireSource> fire_srcs;

	private List<Agent> agents;
	
	private World world;

	public Board(World world) {
		this.world = world;
		this.dimension = world.getDimension();
		agents = new ArrayList<Agent>();
		fire_srcs = world.getFireSources();
	}

	/**
	 * One iteration of simulation. Agent updates its state only if it's
	 * alive, on-board and its pre-movement time has already passed.
	 * 
	 * @param dt
	 *            time in [s] that passed since previous iteration
	 * @throws NoPhysicsDataException
	 */
	private double t = 0;
	public void update(double t, double dt) {
		this.t = t;
		for (Agent agent : agents) {
			if (agent.isAlive() && !agent.isExited()
					&& t > agent.getPreMoveTime())
				agent.update(dt);
		}
	}

	public double getExitY(Exit e) {
		return (e.p1.y + e.p2.y) / 2;
	}

	public double getExitX(Exit e) {
		return (e.p1.x + e.p2.x) / 2;
	}

	/**
	 * Finds a point on Exit that is closest to given point.
	 * 
	 * @param p
	 *            given point
	 * @return closest point
	 */
	public Point2D.Double getExitClosestPoint(Exit e, Point2D.Double p) {
		Point2D.Double closestPoint;

		double delta_x = e.p2.x - e.p1.x;
		double delta_y = e.p2.y - e.p1.y;

		if ((delta_x == 0) && (delta_y == 0)) {
			// throw sth
		}

		double u = ((p.x - e.p1.x) * delta_x + (p.y - e.p1.y) * delta_y)
				/ (delta_x * delta_x + delta_y * delta_y);

		if (u < 0) {
			closestPoint = new Point2D.Double(e.p1.x, e.p2.y);
		} else if (u > 1) {
			closestPoint = new Point2D.Double(e.p2.x, e.p2.y);
		} else {
			closestPoint = new Point2D.Double(
					(int) Math.round(e.p1.x + u * delta_x),
					(int) Math.round(e.p1.y + u * delta_y));
		}

		return closestPoint;
	}
	
	public boolean isInsideObstacle(Obstacle obstacle, Point2D.Double p, double reserve) {
		return obstacle.contains(p, reserve);
	}

	public final static class Wall {
	}

	public boolean isOutOfBounds(Point2D.Double p) {
		return !(p.x >= 0 && p.y >= 0 && p.x <= dimension.x && p.y <= dimension.y);
	}

}
