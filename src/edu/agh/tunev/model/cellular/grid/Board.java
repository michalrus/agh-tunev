package edu.agh.tunev.model.cellular.grid;

import java.util.Vector;

public final class Board {

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

		// uwaga -- w funkcji przejścia, gdy zmieniasz komórkę osoby,
		// pamiętaj, żeby w samej osobie uaktualnić jej rzeczywistą pozycję:
		// Person.setPosition(x,y) -- ale (x,y) są rzeczywiste, więc musisz użyć
		// Model.d2cX() i Model.d2cY() =)
		//
		// a potem, żeby przeiterować po osobach i zrobić
		// interpolator.saveState, ale to już chyba w Model.simulate, tak jak
		// jest teraz?
	}

}
