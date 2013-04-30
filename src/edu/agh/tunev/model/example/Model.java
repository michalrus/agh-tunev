package edu.agh.tunev.model.example;

import java.util.Vector;

import edu.agh.tunev.interpolation.Interpolator;
import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.statistics.Statistics.AddCallback;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

// zauważ, że rozszerzając, podajemy w parametrze klasę, która modeluje nam
// osobę w tym modelu
public final class Model extends AbstractModel<Person> {

	// nazwa pod jaką nasz model jest widoczny w UI; obecna prawdopodobnie do
	// zmiany
	//
	// Żeby "zarejestrować" nowy model, żeby był widoczny w UI, trzeba dodać
	// linijkę z nazwą jego klasy do <code>edu.agh.tunev.Main.main()</code>.
	public final static String MODEL_NAME = "model przykładowy";

	public Model(World world, Interpolator interpolator) {
		super(world, interpolator);
	}

	@Override
	public void simulate(double duration, Vector<Person> people,
			ProgressCallback progressCallback, AddCallback addCallback) {
		// TODO Auto-generated method stub

		// zobacz helpa do rodzica (AbstractModel.simulate) -- po prostu
		// potrzymaj myszkę nad nazwą metody 4 linijki wyżej =)

		// przykładzik:

		final double dt = 0.01;
		final int num = (int) Math.round(duration / dt);
		double t = 0.0;
		for (int i = 1; i <= num; i++) {
			for (Person p : people) {
				// ruszamy ludzikiem, update'ujemy .x i .y

				// ... cokolwiek

				// wymiary świata to World.getXDimension() i Y accordingly

				// przeszkody to World.getObstacles, ale tego jeszcze nie ma

				// ważne: zapisujemy jej stan w danej chwili t w interpolatorze
				// -- niezwykle taż czynność istotna!
				interpolator.saveState(p, t);
			}

			// ważne: i czas podawany w .saveState(t) i współrzędne .x i .y są
			// rzeczywiste (czyli sekundy i metry!)

			// zwiększamy nasz wewnętrzny czas symulatora
			t += dt;

			// zwiększamy ProgressBar w UI, żeby user nie myślał, że nic się nie
			// dzieje =)~
			progressCallback.update(i, num, "Simulating...");

			// a jakie jeszcze klasy są w tym pakiecie, czy to implementujące
			// automat komórkowy itd. -- dla mnie nieistotne. Też ofc ta funkcja
			// nie musi tak wyglądać. Dla mnie jest ważne, żebym dostał
			// .saveState(t) na każdej osobie w odpowiednich dyskretnych czasach
			// i tyle

			// możemy umówić się, że jak Person znika z planszy, to przypisujemy
			// do .x i .y wartość Double.NaN

			// warto też wywołać callback.update() okresowo, żeby pokazać postęp
			// obliczeń

			// to tyle :B powodzenia, ja zabieram się za drugą stronę

			// 05:09, ja pierniczę -,-
		}
	}

}
