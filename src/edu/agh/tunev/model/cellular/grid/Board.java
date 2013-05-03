package edu.agh.tunev.model.cellular.grid;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import edu.agh.tunev.world.World;

public final class Board {

	private Vector<Vector<Cell>> cells;
	private World world;

	public Board(World _world) {
		this.world = _world;
		spawnCells();

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

	/**
	 * 
	 * 
	 * @param p
	 * @return
	 */
	public Cell getCellAt(Point p) {
		return cells.get(p.y).get(p.x);
	}
	
	public Point getDimension() {
		return new Point(cells.get(0).size(), cells.size());
	}


	public void update() {
		// TODO Auto-generated method stub
	}

}
