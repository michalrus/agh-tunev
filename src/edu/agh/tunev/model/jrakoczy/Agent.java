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

import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.world.Exit;
import edu.agh.tunev.world.FireSource;
import edu.agh.tunev.world.Obstacle;
import edu.agh.tunev.world.Physics;

public final class Agent {

	/** Temperature-to-threat coefficient */
	static final double TEMP_THREAT_COEFF = 0.06;

	/** Broadness of Agent's ellipse in [m]. */
	public static final double BROADNESS = 0.33;

	/** Thickness of Agent's ellipse in [m]. */
	public static final double THICKNESS = 0.2;

	/** Angle between radiuses defining circle sector that is a Neighborhood */ 
	private static final double CIRCLE_SECTOR = 45; // 360/8

	/**
	 * Base of exponential function used to calculate neighborhood radius
	 * depending on its angle.
	 */
	private static final double BASE_RADIUS_CALC = 1.2;

	/**
	 * Base of exponential function used to calculate attractiveness coefficient
	 * of given direction. Coefficients for directions of smaller angles which will
	 * more-or-less keep escape direction are of greater value.
	 */
	private static final double BASE_ATTR_CALC = 1.01;

	/**
	 * Scaling coefficient for exponential function used to calculate
	 * neighborhood radius.
	 */
	private static final double POW_RADIUS_COEFF = 8;

	/**
	 * Scaling coefficient for exponential function used to calculate
	 * attractiveness coefficient.
	 */
	private static final double POW_ATTR_COEFF = 1;

	/**
	 * Minimal value of threat coefficient causing change of direction.
	 * Agent always heads to exit, except when environmental condition
	 * doesn't allow for that.
	 */
	private static final double MIN_THREAT_VAL = 60;

	/**
	 * Distance from exit for which an agent stops paying attention to
	 * external factors and pounces on the door/portal
	 */
	private static final double EXIT_RUSH_DIST = 3;

	/** Minimal temperature for which an agent sees flame */
	private static final double MIN_FLAME_TEMP = 70;

	/** Deathly temperature at eye-height of 1.5 m */
	private static final double LETHAL_TEMP = 80;

	/** CO concentration causing instant death [ppm] */
	private static final double LETHAL_CO_CONCN = 30000.0;

	/** HbCO blood concentration causing instant death [%] */
	private static final double LETHAL_HbCO_CONCN = 75.0;

	/** HbCO excretion velocity */
	private static final double CLEANSING_VELOCITY = 0.08;

	/** Coefficient used to calculate smoke density based on CO concentration */
	// TODO: very far-fetched but it's too variable and we have no data
	private static final double CO_SMOKE_COEFF = 6.5;
	
	/** Mol/mol to ppm conversion */
	private static final double MOL_TO_PPM = 1E11;

	/** Coefficient of function transforming distance to reaction time */
	private static final double REACTION_COEFF = 0.3;

	/** Agent's position on the board in real meters. */
	Point2D.Double position;

	/** Currently selected evac exit */
	Exit exit;

	/** Reference to the Board */
	Board board;

	/**
	 * Orientation: angle between sight vector and X axe in [deg] (like in
	 * analytic geometry).
	 */
	double phi;

	/** Dead or alive? */
	private boolean alive;

	/** Have we escaped succesfully? */
	boolean exited;

	/** Time that will pass before the Agent will make some movement decision. */
	private double pre_movement_t;

	/** Current HbCO blood concentration */
	private double hbco;

	/** Agent's movement time */
	double dt;

	/** Agent's movement "module" */
	private Motion motion;

	/** Agent's psyche characteristic */
	private Psyche psyche;

	/**
	 * Agent's constructor. Initiates all field required for his existence on
	 * the Board.
	 */
	public final PersonProfile profile;
	public Agent(Board board, PersonProfile profile) {
		this.profile = profile;
		this.board = board;
		this.position = profile.initialPosition;
		motion = new Motion(this);
		psyche = new Psyche(this);

		phi = profile.initialOrientation;
		motion.stance = profile.initialMovement;

		alive = true;
		exited = false;
		hbco = 0;
		dt = 0;

		final FireSource nearest = board.getNearestFireSrc(position);
		pre_movement_t = (REACTION_COEFF
				* (nearest == null ? 1 : position.distance(nearest)) + psyche.reaction_t);
	}

	/**
	 * Agent's actions taken in an iteration.
	 * 
	 * 1. Checks if it should die.
	 * 
	 * 2. Chooses an exit.
	 * 
	 * 3. Updates list of checkpoints.
	 * 
	 * 4. Updates anxiety of agent.
	 * 
	 * 5. Adjusts movement velocity based on conditions and anxiety.
	 * 
	 * 6. Makes a decision based on data in collected in previous steps.
	 * 
	 * 7. Makes a movement.
	 * 
	 * @param dt
	 *            Time [s] that passed since last movement. Can be used to
	 *            calculate current movement like:
	 *            {@code dx = dt * v * cos(phi); dy = dt * v * sin(phi);}
	 * @throws NoPhysicsDataException
	 */
	public void update(double _dt) {
		this.dt = _dt;
		double curr_temp = getMeanPhysics(0, 360, BROADNESS,
				Physics.Type.TEMPERATURE);
		double curr_co = MOL_TO_PPM * getMeanPhysics(0, 360, BROADNESS, Physics.Type.CO);
		checkIfIWillLive(curr_co, curr_temp);

		if (alive) {
			double smoke_density = curr_co * CO_SMOKE_COEFF;

			chooseExit();
			motion.updateCheckpoints();
			psyche.expAnxiety(TEMP_THREAT_COEFF * curr_temp);
			motion.adjustVelocity(smoke_density, psyche.anxiety);
			makeDecision();
			motion.move();
		}

		// when we're beyond the board, then we've exited? exited = true
		// will cause an agent to stop being displayed and a raise in
		// rescued stats :]
		// TODO: set to true only when we're at an Exit
		exited = (distToExit(exit) < THICKNESS)
				|| (position.x < 0 || position.y < 0
						|| position.x > board.getDimension().x || position.y > board
						.getDimension().y);
	}

	public Point2D.Double getPosition() {
		return position;
	}

	/**
	 * 
	 * @return Orientation angle.
	 */
	public double getOrientation() {
		return phi;
	}

	/**
	 * Returns irrespective of exited status.
	 * 
	 * @return health
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Checks if the agent is on the Board
	 * 
	 * @return
	 */
	public boolean isExited() {
		return exited;
	}

	/**
	 * @return time that will pass before movement starts
	 */
	public double getPreMoveTime() {
		return pre_movement_t;
	}

	/**
	 * Returns HbCO blood concentration.
	 * 
	 * @return HbCO
	 */
	public double getHBCO() {
		return hbco;
	}

	/**
	 * Returns current speed of the agent
	 * 
	 * @return velocity
	 */
	public double getVelocity() {
		return motion.velocity;
	}

	public PersonState.Movement getStance(){
		return motion.stance;
	}
	
	/**
	 * Checks if the agent will live based on temperature of neightborhood
	 * and toxins blood concentration.
	 * 
	 * @param curr_co
	 *            CO concentration in neighborhood
	 * @param curr_temp
	 *            average temperature in neighborhood
	 */
	private void checkIfIWillLive(double curr_co, double curr_temp) {
		evaluateHbCO(curr_co);
		// <michał> commented out
		//System.out.println(curr_co + " " + hbco);

		if (hbco > LETHAL_HbCO_CONCN || curr_temp > LETHAL_TEMP)
			alive = false;
	}

	/**
	 * Calculates current HbCO concentration accounting for ability of the
	 * organism to excrete toxins.
	 */
	private void evaluateHbCO(double curr_co) {
		// TODO: Tune the params below
		if (hbco > dt * CLEANSING_VELOCITY)
			hbco -= dt * CLEANSING_VELOCITY;

		hbco += dt * LETHAL_HbCO_CONCN * (curr_co / LETHAL_CO_CONCN);
	}

	/**
	 * Makes a decision regarding new phi or sets new checkpoint.
	 */
	private void makeDecision() {
		phi = calculateNewPhi();
		double attractivness_ahead = computeThreatComponent(0);
		Object barrier = motion.isStaticCollision(0);

		if (distToExit(exit) > EXIT_RUSH_DIST
				&& attractivness_ahead > MIN_THREAT_VAL && barrier == null) {

			double attractivness = Double.POSITIVE_INFINITY;
			for (double angle = -180; angle < 180; angle += CIRCLE_SECTOR) {
				if (angle == 0)
					continue;

				double attr_coeff = 1 / computeMagnitudeByAngle(POW_ATTR_COEFF,
						BASE_ATTR_CALC, angle);
				double curr_attractivness = attr_coeff
						* computeThreatComponent(angle);

				if (curr_attractivness < attractivness
						&& motion.isStaticCollision(angle) == null) {

					attractivness = curr_attractivness;
					phi += angle;
				}
			}
		}

		if (barrier instanceof Obstacle)
			motion.addCheckpoint(motion.avoidCollision((Obstacle) barrier));
	}

	/**
	 * Calculates an angle that the Agent has to choose to get to selected
	 * checkpoint.
	 * 
	 * @return angle in [-180, 180)
	 */
	private double calculateNewPhi() {
		if (motion.checkpoints.isEmpty()) // TODO: fixed
											// ArrayIndexOutOfBoundsException --
											// m.
			return phi;

		Point2D.Double checkpoint = motion.checkpoints
				.get(motion.checkpoints.size() - 1);
		double deltaY = checkpoint.y - position.y;
		double deltaX = checkpoint.x - position.x;
		double angle = Math.atan2(deltaY, deltaX);

		return Math.toDegrees(angle);
	}

	/**
	 * Chooses nearest Exit which is reachable
	 * 
	 * @throws NoPhysicsDataException
	 */
	private void chooseExit() {
		Exit chosen_exit = null;
		Exit curr_exit = getNearestExit(-1);

		do {
			chosen_exit = curr_exit;
			double dist_exit = distToExit(chosen_exit);
			curr_exit = getNearestExit(dist_exit);
		} while (checkForBlockage(chosen_exit) > 0 && curr_exit != null);

		exit = chosen_exit;

	}

	/**
	 * Uses distances on one axe only. Seeks for nearest Exit that is not
	 * closer than min_dist, so that we can find alternative exits. When
	 * min_dist < 0, finds the nearest Exit.
	 */
	private Exit getNearestExit(double min_dist) {
		double shortest_dist = board.getDimension().x + board.getDimension().y;
		Exit nearest_exit = null;

		for (Exit e : board.getExits()) {
			double dist = Math.abs(distToExit(e));
			if (dist < shortest_dist && dist > min_dist) {
				shortest_dist = dist;
				nearest_exit = e;
			}
		}
		return nearest_exit;
	}

	/**
	 * This algorithm works by moving along two axes: X - always, Y - only if it
	 * finds a blockade. Starts with Agent's Y coordinate and moves along this
	 * axis towards potential Exit. If it stumbles upon a blockade, it then
	 * checks if the overall width of the tunnel for this Y value is blocked.
	 * 
	 * Moving along X axis of Agent's width, it checks whether entire line
	 * of tunnel-width length is blocked. If there's at least one pass, it then
	 * checks next points on Y axis. If it doesn't exist, the method returns
	 * Y coordinate of the blockade.
	 * 
	 * TODO: In a more real model, the Agent will choose a direction opposite
	 * to fire source.
	 * 
	 * @param _exit
	 *            an exit towards which we want to escape
	 * @return -1 if path to exit is not blocked in any way
	 *         y of blockade if there's no passing by
	 * @throws NoPhysicsDataException
	 */
	// TODO: rework, watch out for (....XXX__XX...)
	private double checkForBlockage(Exit _exit) {
		boolean viable_route = true;
		double exit_y = board.getExitY(_exit);
		double dist = Math.abs(position.y - exit_y);
		double ds = 0.5; /*board.getDataCellDimension().y*/; //TODO:<michał> what here?

		if (position.y > exit_y)
			ds = -ds;

		// move along Y axis towards the exit
		double y_coord = position.y + ds;
		while (Math.abs(y_coord - position.y) < dist) {
			double x_coord = 0 + BROADNESS;
			double checkpoint_y_temp = 0;
			checkpoint_y_temp = board.getPhysics(
					new Point2D.Double(x_coord, y_coord), Physics.Type.TEMPERATURE);

			// move along X axis if we got to a blockade
			if (checkpoint_y_temp > MIN_FLAME_TEMP) {
				viable_route = false;
				while (x_coord < board.getDimension().x) {
					double checkpoint_x_temp = MIN_FLAME_TEMP;
					Point2D.Double checkpoint_x = new Point2D.Double(x_coord, y_coord);
					checkpoint_x_temp = board.getPhysics(checkpoint_x,
							Physics.Type.TEMPERATURE);

					if (checkpoint_x_temp < MIN_FLAME_TEMP
							|| motion.isObstacleInPos(checkpoint_x) == null)
						viable_route = true;

					x_coord += BROADNESS;
				}
			}
			// if there's no exit, return Y coordinate of the blockade
			if (!viable_route)
				return y_coord;

			y_coord += ds;
		}
		return -1;
	}

	/**
	 * Calculates distance from current position to given exit.
	 * 
	 * @param _exit
	 *            selected exit
	 * @return distance
	 */
	double distToExit(Exit _exit) {
		if (_exit == null)
			return Double.POSITIVE_INFINITY;
		Point2D.Double exit_closest_p = board.getExitClosestPoint(_exit, position);
		return position.distance(exit_closest_p);
	}

	/**
	 * Returns average value of some physical param in a circular sector
	 * which "starts" at Agent's position. Surface integral... kind of.
	 * 
	 * Concept: 1) discretize alpha by dalpha; 2) for each of these angles
	 * discretize r by dr; 3) take param's value in point specified by dalpha
	 * and dr, add it to accumulator and 4) return accumulator divided by
	 * number of discrete points.
	 * 
	 * This is 1) ultrasimple, 2) points closer to Agent are more densely
	 * placed, so value closer to the Agent has more impact.
	 * 
	 * @param orientation
	 *            Angle between Agent's orientation vector and symmetry axis of
	 *            circular sector. Agent's left hand is +90.0 [deg], right hand
	 *            is -90.0 [deg].
	 * @param alpha
	 *            Angle [deg] of sector's arc.
	 *            
	 *            Of course we can call this with alpha == 0.0 and get
	 *            line-average only.
	 *            
	 *            We can also set alpha == 360.0 and calculate average value
	 *            in whole neighborhood.
	 * @param r
	 *            Radius of the sector's circle.
	 * @param what
	 *            Which physical param we want to use for calculations.
	 * @return
	 */
	private double getMeanPhysics(double orientation, double alpha, double r,
			Physics.Type what) {
		if (alpha < 0)
			throw new IllegalArgumentException("alpha < 0");
		if (r < 0)
			throw new IllegalArgumentException("r < 0");

		double dalpha = 10; // [deg]
		double dr = 0.5; // [m]

		double alphaA = phi + orientation - alpha / 2;
		double alphaB = phi + orientation + alpha / 2;
		double rA = 0;
		double rB = r;

		double sum = 0.0;
		long num = 0;

		alpha = alphaA;
		// do-while needed here to run it at least once (not sure if 0* angle
		// would trigger for-loop -- numerical errors)
		do {
			double sin = Math.sin(Math.toRadians(alpha));
			double cos = Math.cos(Math.toRadians(alpha));
			r = rA;
			do {
				sum += board.getPhysics(new Point2D.Double(position.x + cos * r,
						position.y + sin * r), what);
				num++;
				r += dr;
			} while (r <= rB);
			alpha += dalpha;
		} while (alpha <= alphaB);

		return sum / num;
	}

	/**
	 * Computes threat coefficient for a given direction.
	 * 
	 * @param angle
	 *             potential direction
	 * @return threat coeff, the larger, the WORSE
	 */
	private double computeThreatComponent(double angle) {
		double attractivness_comp = 0.0;
		double r_ahead = computeMagnitudeByAngle(POW_RADIUS_COEFF,
				BASE_RADIUS_CALC, angle);

		attractivness_comp += getMeanPhysics(angle, CIRCLE_SECTOR, r_ahead,
				Physics.Type.TEMPERATURE);
		return attractivness_comp;
	}

	/**
	 * Computes neighborhood radius based on its rotation angle.
	 * 
	 * @param base
	 *            Power base. Strongly influeneces diversity of radiuses that
	 *            change exponentially
	 * @param angle
	 * @return radius
	 */
	private double computeMagnitudeByAngle(double pow_coeff, double base,
			double angle) {
		return pow_coeff
				* Math.pow(base, (180 - Math.abs(angle)) / CIRCLE_SECTOR);
	}
}
