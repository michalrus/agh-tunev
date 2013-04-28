package edu.agh.tunev.model.cellular;

import java.util.Vector;

public class Board {

	private Vector<Vector<Cell>> cells;

	public Board(int nx, int ny) {
		cells = new Vector<Vector<Cell>>();

		for (int iy = 0; iy < ny; iy++) {
			Vector<Cell> row = new Vector<Cell>();
			for (int ix = 0; ix < nx; ix++)
				row.add(new Cell());
			cells.add(row);
		}
	}

	public Cell get(int ix, int iy) {
		return cells.get(iy).get(ix);
	}

}
