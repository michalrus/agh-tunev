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
import java.util.Collections;
import java.util.List;

import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.world.Obstacle;

class Motion {
	/** Velocity coefficient for bent position */
	private final static double BENT_COEFF = 0.75;

	/** Velocity coefficient for crawling position */
	private final static double CRAWL_COEFF = 0.1;

	/** Smoke density that makes an agent bend */
	private final static double SMOKE_BENT_DENSITY = 5200;

	/** Smoke density that makes an agent crawl */
	private final static double SMOKE_CRAWL_DENSITY = 20800;

	/** Default (initial) movement velocity */
	private final static double AVG_MOVING_SPEED = 1.6;

	/** Current stance of an agent */
	PersonState.Movement stance;

	/**
	 * List of points we're going to "visit". Chosen exit has index 0.
	 */
	List<Point2D.Double> checkpoints;

	/** Current velocity */
	double velocity;

	/** Reference to the boss */
	private Agent agent;

	/** Velocity coefficient, individual characteristic */
	private double velocity_coeff;

	Motion(Agent _agent) {
		this.agent = _agent;
		checkpoints = new ArrayList<Point2D.Double>();
		velocity_coeff = (Math.random() / 2) + 0.75; // range [0.75, 1.25]
		velocity = velocity_coeff * AVG_MOVING_SPEED;
		stance = PersonState.Movement.STANDING;
	}

	/** Move in given direction with current velocity */
	void move() {
		double x = agent.position.x + velocity * agent.dt
				* Math.cos(Math.toRadians(agent.phi));
		double y = agent.position.y + velocity * agent.dt
				* Math.sin(Math.toRadians(agent.phi));

		Point2D.Double dest = new Point2D.Double(x, y);

		if (!isDynamicCollision(dest))
			agent.position = dest;
	}

	/**
	 * Adjusts Agent's velocity to environmental conditions and takes his
	 * dismay into account.
	 * 
	 * @param smoke_density
	 *            density of smoke in current position
	 * @param anxiety
	 *            dismay level
	 */
	void adjustVelocity(double smoke_density, double anxiety) {
		changeStance(smoke_density);
		velocity = velocity_coeff * anxiety * AVG_MOVING_SPEED;

		if (stance == PersonState.Movement.SQUATTING)
			velocity *= BENT_COEFF;
		else if (stance == PersonState.Movement.CRAWLING)
			velocity *= CRAWL_COEFF;

	}

	/**
	 * Checks if there's no obstacle in the point we're heading to.
	 * This point is calculated based on angle and current velocity.
	 * 
	 * @param angle
	 *            direction of movement
	 * @return Obstacle or Board.Wall or null, if there's free space
	 */
	// TODO: quick-fix: <michał> change the hierarchy? Rewritten to use Object for now -,-
	Object isStaticCollision(double angle) {
		double path_length = velocity * agent.dt + Agent.BROADNESS;
		double alpha = angle + agent.phi;
		double sin = Math.sin(Math.toRadians(alpha));
		double cos = Math.cos(Math.toRadians(alpha));

		Point2D.Double p = new Point2D.Double(agent.position.x + path_length * cos,
				agent.position.y + path_length * sin);

		if (agent.board.isOutOfBounds(p))
			return new Board.Wall();

		return isObstacleInPos(p);
	}

	/**
	 * Checks if there's an Obstacle in given point
	 * 
	 * @param p
	 *            point
	 * @return reference to the Obstacle or null
	 */
	Obstacle isObstacleInPos(Point2D.Double p) {
		for (Obstacle ob : agent.board.getObstacles()) {
			if (ob.contains(p, 2 * Agent.BROADNESS))
				return ob;
		}

		return null;
	}

	/**
	 * Calculates the point to which an Agent needs to go to bypass the obstacle.
	 * 
	 * First it checks on which side of the Obstacle it stands and then decides
	 * to which vertex to move. (Chooses one closer to an Exit.)
	 * 
	 * @param ob
	 *            an Obstacle we wanto to bypass
	 * @return coordinate of chosen checkpoint
	 */
	// TODO: Motion
	Point2D.Double avoidCollision(Obstacle ob) {
		Point2D.Double start_point = ob.p1;
		Point2D.Double end_point = ob.p2;

		// calculate coordinates of Obstacle vertices (with a little margin)
		Point2D.Double left_bot = new Point2D.Double(start_point.x - Agent.BROADNESS,
				start_point.y - Agent.BROADNESS);
		Point2D.Double left_top = new Point2D.Double(start_point.x - Agent.BROADNESS, end_point.y
				+ Agent.BROADNESS);
		Point2D.Double right_bot = new Point2D.Double(end_point.x + Agent.BROADNESS,
				start_point.y - Agent.BROADNESS);
		Point2D.Double right_top = new Point2D.Double(end_point.x + Agent.BROADNESS, end_point.y
				+ Agent.BROADNESS);

		// calculate distances between Agent and Obstacle sides (again with
		// a margin)
		List<Double> dist_list = new ArrayList<Double>();
		dist_list.add(Math.abs(agent.position.x
				- (start_point.x - 2 * Agent.BROADNESS))); // left
		dist_list.add(Math.abs(agent.position.y
				- (end_point.y + 2 * Agent.BROADNESS))); // top
		dist_list.add(Math.abs(agent.position.x
				- (end_point.x + 2 * Agent.BROADNESS))); // right
		dist_list.add(Math.abs(agent.position.y
				- (start_point.y - 2 * Agent.BROADNESS))); // bottom

		// choose the least distance
		double min_dist = Collections.min(dist_list);
		Point2D.Double[] selected_points = new Point2D.Double[2];

		// choose suitable vertices
		// left
		if (min_dist == dist_list.get(0)) {
			selected_points[0] = left_bot;
			selected_points[1] = left_top;
			// top
		} else if (min_dist == dist_list.get(1)) {
			selected_points[0] = left_top;
			selected_points[1] = right_top;
			// right
		} else if (min_dist == dist_list.get(2)) {
			selected_points[0] = right_bot;
			selected_points[1] = right_top;
			// bottom
		} else if (min_dist == dist_list.get(3)) {
			selected_points[0] = left_bot;
			selected_points[1] = right_bot;
		}

		Point2D.Double exit_pos = agent.board.getExitClosestPoint(agent.exit, agent.position);
		double[] p_dists = new double[2];
		p_dists[0] = exit_pos.distance(selected_points[0]);
		p_dists[1] = exit_pos.distance(selected_points[1]);

		// choose a vertex closer to the exit
		if (p_dists[0] < p_dists[1])
			return selected_points[0];
		else
			return selected_points[1];
	}

	/**
	 * Adds a checkpoint to the list. If its distance from exit is less
	 * than others, crops the list.
	 * 
	 * @param new_checkpoint
	 */
	void addCheckpoint(Point2D.Double new_checkpoint) {
		trimCheckpoints(new_checkpoint);
		checkpoints.add(new_checkpoint);
	}

	/**
	 * Analyzes the list of checkpoints, accounting for Agent's current position.
	 * If it is closer to the exit than any of the points in the list, then
	 * this point is removed. Coordinate of chosen exit is always first on the
	 * list.
	 */
	void updateCheckpoints() {
		if (agent.exit == null) // TODO: added this check to fix
								// NullPointerException --
								// m.
			return;
		Point2D.Double exit_pos = agent.board.getExitClosestPoint(agent.exit, agent.position);
		if (!checkpoints.isEmpty())
			checkpoints.set(0, exit_pos);
		else
			checkpoints.add(exit_pos);
		trimCheckpoints(agent.position);
	}

	/**
	 * Removes these checkpoints from the list that are further from exit than p
	 * 
	 * @param p
	 */
	private void trimCheckpoints(Point2D.Double p) {
		if (!checkpoints.isEmpty()) {
			Point2D.Double exit_pos = agent.board.getExitClosestPoint(agent.exit, agent.position);
			double new_dist = exit_pos.distance(p);

			int index = 0;
			while (index <= checkpoints.size() - 1
					&& new_dist > exit_pos.distance(checkpoints.get(index)))
				++index;

			checkpoints.subList(index, checkpoints.size()).clear();
		}
	}

	/**
	 * Updates Agent's stance depending on smoke density.
	 * 
	 * @param smoke_density
	 */
	private void changeStance(double smoke_density) {
		if (smoke_density < SMOKE_BENT_DENSITY)
			stance = PersonState.Movement.STANDING;
		else if (smoke_density >= SMOKE_BENT_DENSITY
				&& smoke_density < SMOKE_CRAWL_DENSITY)
			stance = PersonState.Movement.SQUATTING;
		else
			stance = PersonState.Movement.CRAWLING;
	}

	/**
	 * Checks if there's no other Agent in the point we want to move to.
	 * 
	 * @param dest
	 *            point to which we want to move to
	 * @return
	 */
	private boolean isDynamicCollision(Point2D.Double dest) {
		for (Agent a : agent.board.getAgents()) {
			if (!a.isAlive() || a.isExited() || a.equals(agent))
				continue;
			
			double dest_dist = a.getPosition().distance(dest);
			if (dest_dist < Agent.THICKNESS)
				return true;
		}

		return false;
	}
}
