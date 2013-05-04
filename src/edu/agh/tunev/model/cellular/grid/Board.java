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

	public Board(World _world) {
		this.world = _world;
		exits = world.getExits();
		obstacles = world.getObstacles();
		spawnCells();
		assignObstacles();

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
		Point worldDimension = Cell.c2d(world.getDimension());

		for (int iy = 0; iy < worldDimension.y; ++iy) {
			cells.add(new Vector<Cell>());
			for (int ix = 0; ix < worldDimension.x; ++ix) {
				Cell c = new Cell(new Point(ix, iy), this);
				cells.get(iy).add(c);
			}
		}
	}
	
	private void assignExits(){
		
	}
	
	private void assignObstacles(){
		for(Obstacle ob : obstacles){
			Point p1 = Cell.c2d(ob.p1);
			Point p2 = Cell.c2d(ob.p2);
			//TODO: p1 < p2 (?)  <- this should be checked
			
			for(int iy = p1.y - 1; iy <= p2.y + 1; ++iy)
				for(int ix = p1.x - 1; ix <= p2.x + 1; ++ix){
					Cell c = getCellAt(new Point(ix, iy));
					
					if(c != null)
						c.setObstacle(ob);
				}
		}
			
	}

	public Point getDimension() {
		return new Point(cells.get(0).size(), cells.size());
	}

	public Vector<Exit> getExits() {
		return exits;
	}
	

}
