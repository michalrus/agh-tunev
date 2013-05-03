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

		for (int i = 0; i < worldDimension.y; ++i) {
			cells.add(new Vector<Cell>());
			for (int j = 0; j < worldDimension.x; ++j) {
				Cell c = new Cell(new Point(i, j), this);
				cells.get(i).add(c);
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

	/**
	 * Finds neighbours (Moore's neighbourhood) for a given cell.
	 * 
	 * @param cell
	 * @return
	 */
	//TODO: OutOfBounds error prone
	public ArrayList<Cell> getCellNeighbours(Cell cell) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		Point position = cell.getPosition();

		for (int i = position.y - 1; i <= position.y + 1; ++i)
			for (int j = position.x - 1; j < position.x + 1; ++j) {
				Cell c = getCellAt(new Point(i, j));
				if (!c.equals(cell))
					neighbours.add(c);
			}

		return neighbours;
	}

	public void update() {
		// TODO Auto-generated method stub
	}

}
