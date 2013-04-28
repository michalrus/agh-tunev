package edu.agh.tunev.model.cellular;

import java.util.Vector;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.Person;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public final class Model extends AbstractModel {

	public final static String MODEL_NAME = "w¹sowy automat komórkowy";

	public Model(World world) {
		super(world);
	}

	// przyk³adowa dyskretyzacja œwiata -- czyli rozmiar jednej komórki na
	// planszy -- oczywiœcie w metrach -- do zmiany
	private static final double DX = 0.5;
	private static final double DY = 0.4;

	// przyk³adowa dyskretyzacja czasu -- czyli co ile czasu nasze osobniki
	// podejmuj¹ decyzjê o skoku? inaczej: co ile rzeczywistego czasu
	// update'ujemy stan naszego automatu -- oczywiœcie w sekundach -- do zmiany
	private static final double DT = 0.5;

	private Board board;

	@Override
	public void simulate(double duration, Vector<Person> people,
			ProgressCallback callback) {
		// jakie s¹ rzeczywiste wymiary œwiata?
		double dimX = world.getXDimension();
		double dimY = world.getYDimension();

		// stwórz automat (planszê komórek) o obliczonych dyskretnych wymiarach;
		// u¿ywa funkcji do t³umaczenia wymiarów z ci¹g³ych na dyskretne z
		// uwzglêdnieniem DX i DY. Zobacz poni¿ej ich definicje.
		board = new Board(c2dX(dimX) + 1, c2dY(dimY) + 1);

		// pozaznaczaj osoby na naszej modelowej, wewnêtrznej, planszy
		for (Person p : people) {
			// w której komórce jest ta osoba?
			int ix = c2dX(p.getX());
			int iy = c2dY(p.getY());

			// przesuñ j¹ dok³adnie na œrodek tej komórki...
			p.setPosition(d2cX(ix), d2cY(iy));
			// ... i zrób jej tam "zdjêcie" dla interpolatora w chwili t=0[s]
			p.saveState(0.0);

			// zaznacz w odpowiedniej komórce automatu, ¿e któr¹ osobê
			Cell c = board.get(ix, iy);
			c.person = p;
		}

		// TODO: pozaznaczaj przeszkody na planszy
		
		// TODO: pozaznaczaj wyjœcia na planszy

		// kolejne iteracje automatu -- uwaga, ¿adnego czekania w stylu
		// Thread.sleep() -- to ma siê policzyæ *jak najszybciej*! --
		// wyœwietlanie "filmu" z symulacji jest niezale¿ne od obliczania (no,
		// tyle tylko zale¿ne, ¿e mo¿emy wyœwietlaæ tylko do momentu, który ju¿
		// siê policzy³)
		int num = (int)Math.round(Math.ceil(world.getDuration() / DT));
		double t = 0;
		for (int i = 0; i < num; i++) {
			// uaktualnij rzeczywisty czas naszej symulacji
			t += DT;
			
			// przejdŸ do nastêpnego stanu automatu
			board.update();
			
			// porób zdjêcia osobom w aktualnym rzeczywistym czasie
			for (Person p : people)
				p.saveState(t);
			
			// grzecznoœæ: zwiêksz ProgressBar w UI
			callback.update(i + 1, num, (i + 1 == num ? "Gotowe!" : "Wci¹¿ liczê..."));
		}
		
		// i tyle ^_^
	}

	/**
	 * Discreet to continuous dimensions for OX.
	 */
	private static double d2cX(int ix) {
		// zwróæ pozycjê w œrodku komórki
		return (0.5 + ix) * DX;
	}

	/**
	 * Discrete to continuous dimensions for OY.
	 */
	private static double d2cY(int iy) {
		// zwróæ pozycjê w œrodku komórki
		return (0.5 + iy) * DY;
	}

	/**
	 * Continuous to discrete dimensions for OX.
	 */
	private static int c2dX(double x) {
		return (int) Math.round(Math.floor(x / DX));
	}

	/**
	 * Continuous to discrete dimensions for OY.
	 */
	private static int c2dY(double y) {
		return (int) Math.round(Math.floor(y / DY));
	}

}
