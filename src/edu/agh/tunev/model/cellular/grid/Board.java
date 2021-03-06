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

package edu.agh.tunev.model.cellular.grid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.world.Exit;
import edu.agh.tunev.world.FireSource;
import edu.agh.tunev.world.Obstacle;
import edu.agh.tunev.world.Physics;
import edu.agh.tunev.world.Physics.Type;
import edu.agh.tunev.world.World;

public final class Board {

	private static final double BLOCKED_AREA_TOL = 0.7;

	private Vector<Vector<Cell>> cells;
	private final World world;
	private final Vector<Exit> exits;
	private final Vector<Obstacle> obstacles;
	private final Point worldDimension;
	private final FireSource fireSrc;

	public Board(World _world) {
		this.world = _world;
		worldDimension = Cell.c2d(world.getDimension());
		exits = world.getExits();
		obstacles = world.getObstacles();
		fireSrc = world.getFireSources().get(0); // TODO: !!!
		spawnCells();
		assignObstacles();
		assignExits();

	}

	/**
	 * Updates board state in a certain slice of time.
	 * 
	 * @param t
	 */
	public void update(Double t) {
		Point blockage = checkForBlockage();
		
		for (Vector<Cell> v : cells)
			for (Cell c : v) {
				Point2D.Double position = Cell.d2c(c.getPosition());
				c.update(world.getPhysicsAt(t, position), blockage);
			}
	}

	/**
	 * Returns cell with a given index.
	 * 
	 * @param p
	 * @return {@code Cell} at {@code p}
	 */
	public Cell getCellAt(Point p) {
		if (p.y >= 0 && p.x >= 0 && p.y < getDimension().y
				&& p.x < getDimension().x)
			return cells.get(p.y).get(p.x);

		return null;
	}

	/**
	 * Creates a 2D vector of cells fitted to the {@code world} size.
	 */
	private void spawnCells() {
		cells = new Vector<Vector<Cell>>();

		for (int iy = 0; iy < worldDimension.y; ++iy) {
			cells.add(new Vector<Cell>());
			for (int ix = 0; ix < worldDimension.x; ++ix) {
				Cell c = new Cell(new Point(ix, iy), this);
				cells.get(iy).add(c);
			}
		}
	}

	/**
	 * Assign exits to proper cells.
	 */
	private void assignExits() {
		for (Exit e : exits) {
			Point p1 = Cell.c2d(e.p1);
			Point p2 = Cell.c2d(e.p2);

			for (int iy = p1.y; iy <= p2.y; ++iy)
				for (int ix = p1.x; ix <= p2.x; ++ix) {
					Cell c = getCellAt(new Point(ix, iy));

					if (c != null)
						c.setExit(e);
				}
		}
	}

	/**
	 * Assign obstacles to proper cells.
	 */
	private void assignObstacles() {
		for (Obstacle ob : obstacles) {
			Point p1 = Cell.c2d(ob.p1);
			Point p2 = Cell.c2d(ob.p2);
			// TODO: p1 < p2 (?) <- this should be checked

			for (int iy = p1.y - 1; iy <= p2.y + 1; ++iy)
				for (int ix = p1.x - 1; ix <= p2.x + 1; ++ix) {
					Cell c = getCellAt(new Point(ix, iy));

					if (c != null)
						c.setObstacle(ob);
				}
		}

	}

	private Point checkForBlockage() {
		Point boundries = getParallelIterBoundries();

		for (int iy = 0; iy <= boundries.y; ++iy)
			for (int ix = 0; ix <= boundries.x; ++ix) {
				Point checkedPoint = new Point(ix, iy);
				Cell c = getCellAt(checkedPoint);
				boolean blocked = (c.checkTempLethality() || c.isBlocked());
				if (blocked && isRowBlocked(checkedPoint))
					return checkedPoint;			
			}
		
		return null;
	}

	private boolean isRowBlocked(Point pointer){
		int cntr = 0;
		
		Point boundries = getPerpendicularIterBoundries(pointer);
		for(int iy = pointer.y; iy <= boundries.y; ++iy)
			for(int ix = pointer.x; ix <= boundries.x; ++ix){
				Cell c = getCellAt(new Point(ix, iy));
				if(c.checkTempLethality() || c.isBlocked())
					++cntr;
			}
		
		return cntr > BLOCKED_AREA_TOL*Math.min(worldDimension.x, worldDimension.y);
		
	}

	private Point getParallelIterBoundries() {
		if (worldDimension.x <= worldDimension.y)
			return new Point(1, worldDimension.y - 1);
		else
			return new Point(worldDimension.x - 1, 1);
	}

	private Point getPerpendicularIterBoundries(Point p) {
		if (worldDimension.x <= worldDimension.y)
			return new Point(worldDimension.x - 1, p.y);
		else
			return new Point(p.x, worldDimension.y - 1);
	}

	/**
	 * Checks for blockage for one specific corridor orientation.
	 * 
	 * @return
	 */
	/*
	 * private int checkForYBlockage() { for (int iy = 1; iy < worldDimension.y;
	 * ++iy) { Cell cY = getCellAt(new Point(1, iy)); Physics physY =
	 * cY.getPhysics(); double tempY = physY.get(Type.TEMPERATURE);
	 * 
	 * if (cY.isOccupied() || tempY > BLOCKAGE_TEMP) { for (int ix = 2; ix <
	 * worldDimension.x; ++ix) { Cell cX = getCellAt(new Point(ix, iy)); Physics
	 * physX = cX.getPhysics(); double tempX = physX.get(Type.TEMPERATURE);
	 * 
	 * if (!cX.isOccupied() || tempX > BLOCKAGE_TEMP) return -1; } return iy; }
	 * } return -1; }
	 */

	public Point getDimension() {
		return worldDimension;
	}

	public Vector<Exit> getExits() {
		return exits;
	}

	public FireSource getFireSrc() {
		return fireSrc;
	}

}
