package edu.agh.tunev.model.cellular;

import java.util.Vector;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.Person;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public class Model extends AbstractModel {

	public Model(World world) {
		super(world);
	}

	@Override
	public void simulate(double duration, Vector<Person> people,
			ProgressCallback callback) {
		// TODO Auto-generated method stub

		// przyk³adzik:

		final double dt = 0.01;
		final int num = (int) Math.round(duration / dt);
		double t = 0.0;
		for (int i = 1; i <= num; i++) {
			for (Person p : people) {
				// ruszamy ludzikiem, update'ujemy .x i .y

				// ... cokolwiek

				// wymiary œwiata to World.getXDimension() i Y accordingly

				// przeszkody to World.getObstacles, ale tego jeszcze nie ma

				// wa¿ne: zapisujemy jej stan w danej chwili t w interpolatorze
				// -- niezwykle ta¿ czynnoœæ istotna!
				p.saveState(t);
			}

			// wa¿ne: i czas podawany w .saveState(t) i wspó³rzêdne .x i .y s¹
			// rzeczywiste (czyli sekundy i metry!)

			// zwiêkszamy nasz wewnêtrzny czas symulatora
			t += dt;

			// zwiêkszamy ProgressBar w UI, ¿eby user nie myœla³, ¿e nic siê nie
			// dzieje =)~
			callback.update(i, num, "Simulating...");

			// a jakie jeszcze klasy s¹ w tym pakiecie, czy to implementuj¹ce
			// automat komórkowy itd. -- dla mnie nieistotne. Te¿ ofc ta funkcja
			// nie musi tak wygl¹daæ. Dla mnie jest wa¿ne, ¿ebym dosta³
			// .saveState(t) na ka¿dej osobie w odpowiednich dyskretnych czasach
			// i tyle

			// mo¿emy umówiæ siê, ¿e jak Person znika z planszy, to przypisujemy
			// do .x i .y wartoœæ Double.NaN

			// warto te¿ wywo³aæ callback.update() okresowo, ¿eby pokazaæ postêp
			// obliczeñ

			// to tyle :B powodzenia, ja zabieram siê za drug¹ stronê

			// 05:09, ja pierniczê -,-
		}
	}

}
