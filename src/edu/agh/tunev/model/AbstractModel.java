package edu.agh.tunev.model;

import java.util.Vector;

import edu.agh.tunev.interpolation.Interpolator;
import edu.agh.tunev.world.World;

/**
 * Po tym dziedziczy klasa g³ówna ka¿dego modelu. -- m.
 * 
 * ¯eby "zarejestrowaæ" nowy model, ¿eby by³ widoczny w UI, trzeba dodaæ linijkê
 * z nazw¹ jego klasy do <code>edu.agh.tunev.Main.main()</code>.
 * 
 * @param <T>
 *            mówi o tym, która klasa reprezentuje osobê w danym modelu (musi
 *            dziedziczyæ po AbstractPerson).
 */
public abstract class AbstractModel<T extends AbstractPerson> {

	final protected World world;
	final protected Interpolator interpolator;

	/**
	 * Nazwa modelu w UI. Jak to nie bêdzie ustawione w klasie dziedzicz¹cej, to
	 * register() w main() rzuci wyj¹tek.
	 */
	public static String MODEL_NAME;

	public AbstractModel(World world, Interpolator interpolator) {
		this.world = world;
		this.interpolator = interpolator;
	}

	/**
	 * Metoda startuj¹ca symulacjê.
	 * 
	 * @param duration
	 *            Czas trwania symulacji.
	 * @param people
	 *            Lista osób w danym œwiecie.
	 * @param callback
	 *            Wywo³ujemy po ka¿dej iteracji
	 *            <code>callback.update(done, total,
	 *            msg</code>), gdzie <code>done</code> to numer aktualnej
	 *            iteracji, a <code>total</code> to liczba wszystkich
	 *            zaplanowanych, a <code>msg</code> to jakiœ komunikat tekstowy,
	 *            mo¿e byæ <code>""</code>/<code>null</code>. Po to, ¿eby
	 *            rysowaæ ProgressBar ile ju¿ siê policzy³o z ca³oœci.
	 */
	public abstract void simulate(double duration, Vector<T> people,
			World.ProgressCallback callback);

	/** tego nie ruszamy :] t³umaczy Vector<AbstractPerson> -> Vector<T> */
	@SuppressWarnings("unchecked")
	public final void simulateWrapper(double duration,
			Vector<AbstractPerson> people, World.ProgressCallback callback) {
		Vector<T> castedPeople = new Vector<T>();
		for (AbstractPerson p : people)
			castedPeople.add((T) p);
		simulate(duration, castedPeople, callback);
	}
}
