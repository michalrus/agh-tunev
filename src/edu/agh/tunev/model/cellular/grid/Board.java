package edu.agh.tunev.model.cellular.grid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import edu.agh.tunev.world.Exit;
import edu.agh.tunev.world.Obstacle;
import edu.agh.tunev.world.World;

public final class Board {

	private Vector<Vector<Cell>> cells;
	private final World world;
	private final Vector<Exit> exits;
	private final Vector<Obstacle> obstacles;
	private final Point worldDimension;

	public Board(World _world) {
		this.world = _world;
		worldDimension = Cell.c2d(world.getDimension());
		exits = world.getExits();
		obstacles = world.getObstacles();
		spawnCells();
		assignObstacles();
		assignExits();

	}

	public void update(Double t) {
		for (Vector<Cell> v : cells)
			for (Cell c : v) {
				Point2D.Double position = Cell.d2c(c.getPosition());
				c.update(world.getPhysicsAt(t, position));
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

	public Point getDimension() {
		return worldDimension;
	}

	public Vector<Exit> getExits() {
		return exits;
	}

}
