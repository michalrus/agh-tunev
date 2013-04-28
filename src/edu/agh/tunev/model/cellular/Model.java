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

		// stwórz automat (planszê komórek) o dyskretnych wymiarach
		board = new Board((int) Math.round(Math.floor(dimX / DX)) + 1,
				(int) Math.round(Math.floor(dimY / DY)) + 1);

	}

}
