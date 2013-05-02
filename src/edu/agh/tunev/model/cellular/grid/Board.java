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

	/**
	 * Finds neighbours (Moore's neighbourhood) for a given cell.
	 * 
	 * @param cell
	 * @return
	 */
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

		// uwaga -- w funkcji przejścia, gdy zmieniasz komórkę osoby,
		// pamiętaj, żeby w samej osobie uaktualnić jej rzeczywistą
		// pozycję:
		// Person.setPosition(x,y) -- ale (x,y) są rzeczywiste, więc musisz
		// użyć
		// Model.d2cX() i Model.d2cY() =)
		//
		// a potem, żeby przeiterować po osobach i zrobić
		// interpolator.saveState, ale to już chyba w Model.simulate, tak jak
		// jest teraz?
	}

}
