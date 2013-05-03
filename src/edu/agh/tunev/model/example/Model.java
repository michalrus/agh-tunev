package edu.agh.tunev.model.example;

import java.awt.geom.Point2D;
import java.util.Vector;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;
import edu.agh.tunev.statistics.Statistics.AddCallback;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public final class Model extends AbstractModel {

	// nazwa pod jaką nasz model jest widoczny w UI; obecna prawdopodobnie do
	// zmiany
	//
	// Żeby "zarejestrować" nowy model, żeby był widoczny w UI, trzeba dodać
	// linijkę z nazwą jego klasy do <code>edu.agh.tunev.Main.main()</code>.
	public final static String MODEL_NAME = "model przykładowy";

	public Model(World world) {
		super(world);
	}

	@Override
	public void simulate(double duration, Vector<PersonProfile> profiles,
			ProgressCallback progressCallback, AddCallback addCallback) {
		// TODO Auto-generated method stub

		// zobacz helpa do rodzica (AbstractModel.simulate) -- po prostu
		// potrzymaj myszkę nad nazwą metody 4 linijki wyżej =)

		// przykładzik:

		// utwórzmy sobie jakieś reprezentacje ludzi
		Vector<Person> people = new Vector<Person>();
		for (PersonProfile profile : profiles)
			people.add(new Person(profile));

		final double dt = 0.01;
		final int num = (int) Math.round(duration / dt);
		double t = 0.0;
		for (int i = 1; i <= num; i++) {
			for (Person p : people) {
				// ruszamy ludzikiem, update'ujemy .x i .y

				// ... cokolwiek

				// przeszkody to World.getObstacles

				// ważne: zapisujemy jej stan w danej chwili t w interpolatorze
				// -- niezwykle taż czynność istotna!
				interpolator.saveState(p.profile, t, new PersonState(
						new Point2D.Double(3.14, 4.13), 45.0,
						PersonState.Movement.CRAWLING));
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
