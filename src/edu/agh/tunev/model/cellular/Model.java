package edu.agh.tunev.model.cellular;

import java.awt.Point;
import java.util.Vector;

import sun.java2d.cmm.ProfileActivator;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.model.cellular.agent.WrongOrientationException;
import edu.agh.tunev.model.cellular.grid.Board;
import edu.agh.tunev.model.cellular.grid.Cell;
import edu.agh.tunev.statistics.KilledStatistics;
import edu.agh.tunev.statistics.Statistics.AddCallback;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public final class Model extends AbstractModel {

	// TODO: zmieniiiiiiiiiić!!!111111 bo będzie wstyd
	public final static String MODEL_NAME = "Social Distances Cellular Automata";
	private final static double INTERSECTION_TOLERANCE = 0.1;

	public Model(World world) {
		super(world);
	}

	// przykładowa dyskretyzacja czasu -- czyli co ile czasu nasze osobniki
	// podejmują decyzję o skoku? inaczej: co ile rzeczywistego czasu
	// update'ujemy stan naszego automatu -- oczywiście w sekundach -- do zmiany
	private static final double DT = 0.5;

	private Board board;
	private AllowedConfigs allowedConfigs;

	@Override
	public void simulate(double duration, Vector<PersonProfile> profiles,
			ProgressCallback progressCallback, AddCallback addCallback) {

		// stwórz automat (planszę komórek)
		board = new Board(world);
		
		//TODO: exception handling
		try {
			allowedConfigs = new AllowedConfigs(PersonProfile.WIDTH,
					PersonProfile.GIRTH, Cell.CELL_SIZE, INTERSECTION_TOLERANCE);
		} catch (NeighbourIndexException | WrongOrientationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// stwórz sobie swoje reprezentacje ludzi:
		Vector<Person> people = new Vector<Person>();
		for (PersonProfile profile : profiles)
			people.add(new Person(profile, board.getCellAt(Cell
					.c2d(profile.initialPosition)), allowedConfigs));

		// pozaznaczaj inicjalne (t=0) pozycje osób w interpolatorze (dlatego,
		// że w niektórych modelach -- w tym też! -- te pozycje mogą się różnić
		// od tych wyklikanych przez usera (np. być zaokrąglane do rozmiaru
		// komórki, jak tutaj)
		for (PersonProfile profile : profiles)
			interpolator
					.saveState(
							profile,
							0.0,
							new PersonState(Cell.d2c(Cell
									.c2d(profile.initialPosition)),
									profile.initialOrientation,
									profile.initialMovement));
		// BTW, nie mam pojęcia dlaczego Eclipse postanowiło w ten sposób to
		// automatycznie sformatować jak wyżej... -,-

		// TODO: pododawaj jakieś wykresy do UI związane z tym modelem
		//
		// sidenote: zobacz helpa do interfejsu Statistics: gdy dany wykres
		// pasuje do wielu modeli (np. liczba zabitych jako f(t)), to dodaj jego
		// klasę do pakietu tunev.statistics; jeśli pasuje tylko do tego modelu,
		// to dodaj do pakietu tego modelu
		KilledStatistics killedStatistics = new KilledStatistics();
		addCallback.add(killedStatistics);

		// TODO: pozaznaczaj przeszkody na planszy

		// TODO: pozaznaczaj wyjścia na planszy

		// kolejne iteracje automatu -- uwaga, żadnego czekania w stylu
		// Thread.sleep() -- to ma się policzyć *jak najszybciej*! --
		// wyświetlanie "filmu" z symulacji jest niezależne od obliczania (no,
		// tyle tylko zależne, że możemy wyświetlać tylko do momentu, który już
		// się policzył)
		int num = (int) Math.round(Math.ceil(world.getDuration() / DT));
		double t = 0;
		for (int iteration = 1; iteration <= num; iteration++) {
			// uaktualnij rzeczywisty czas naszej symulacji
			t += DT;

			// pościągaj aktualną fizykę do komórek
			Point i = new Point();
			Point n = board.getDimension();
			for (i.y = 0; i.y < n.y; i.y++)
				for (i.x = 0; i.x < n.x; i.x++)
					board.getCellAt(i).setPhysics(
							world.getPhysicsAt(t, Cell.d2c(i)));

			// przejdź do następnego stanu automatu
			board.update();

			// porób zdjęcia osobom w aktualnym rzeczywistym czasie
			for (Person p : people)
				interpolator.saveState(p.profile, t, p.getCurrentState());

			// TODO: uaktualnij wykresy, które mogą być aktualizowane w trakcie
			// symulowania
			int currentNumDead = 123; // prawdopodobnie ta dana ustawiana
										// gdzie indziej ;p~
			killedStatistics.add(t, currentNumDead);

			// grzeczność: zwiększ ProgressBar w UI
			progressCallback.update(iteration, num,
					(iteration < num ? "Still simulating..." : "Ready!"));
		}

		// TODO: ew. wypełnij wykresy, które mogą być wypełnione dopiero po
		// zakończeniu całej symulacji

		// i tyle ^_^
	}

}
