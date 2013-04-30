package edu.agh.tunev.model.cellular.grid;

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

	public void update() {
		// TODO Auto-generated method stub

		// uwaga -- w funkcji przejœcia, gdy zmieniasz komórkê osoby,
		// pamiêtaj, ¿eby w samej osobie uaktualniæ jej rzeczywist¹ pozycjê:
		// Person.setPosition(x,y) -- ale (x,y) s¹ rzeczywiste, wiêc musisz u¿yæ
		// Model.d2cX() i Model.d2cY() =)
		//
		// a potem, ¿eby przeiterowaæ po osobach i zrobiæ
		// interpolator.saveState, ale to ju¿ chyba w Model.simulate, tak jak
		// jest teraz?
	}

}
