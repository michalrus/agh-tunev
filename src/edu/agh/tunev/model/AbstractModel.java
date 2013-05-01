package edu.agh.tunev.model;

import java.awt.geom.Point2D;
import java.util.Vector;

import edu.agh.tunev.world.World;
import edu.agh.tunev.statistics.Statistics;

/**
 * Po tym dziedziczy klasa główna każdego modelu. -- m.
 * 
 * Żeby "zarejestrować" nowy model, żeby był widoczny w UI, trzeba dodać linijkę
 * z nazwą jego klasy do <code>edu.agh.tunev.Main.main()</code>.
 * 
 * @param <T>
 *            mówi o tym, która klasa reprezentuje osobę w danym modelu (musi
 *            dziedziczyć po AbstractPerson).
 */
public abstract class AbstractModel<T extends AbstractPerson> {

	final protected World world;
	final protected Interpolator interpolator;

	/**
	 * Nazwa modelu w UI. Jak to nie będzie ustawione w klasie dziedziczącej, to
	 * register() w main() rzuci wyjątek.
	 */
	public static String MODEL_NAME;

	public AbstractModel(World world) {
		this.world = world;
		interpolator = new Interpolator();
	}

	/**
	 * Metoda startująca symulację.
	 * 
	 * @param duration
	 *            Czas trwania symulacji.
	 * @param people
	 *            Lista osób w danym świecie.
	 * @param progressCallback
	 *            Wywołujemy po każdej iteracji
	 *            <code>callback.update(done, total,
	 *            msg</code>), gdzie <code>done</code> to numer aktualnej
	 *            iteracji, a <code>total</code> to liczba wszystkich
	 *            zaplanowanych, a <code>msg</code> to jakiś komunikat tekstowy,
	 *            może być <code>""</code>/<code>null</code>. Po to, żeby
	 *            rysować ProgressBar ile już się policzyło z całości.
	 * @param addCallback
	 *            Wywołujemy gdy chcemy dodać jakiś wykres do UI. W dowolnym
	 *            momencie. Może być na początku i uaktualniamy w trakcie, może
	 *            być na końcu, jak już się wszystko policzy.
	 */
	public abstract void simulate(double duration, Vector<T> people,
			World.ProgressCallback progressCallback,
			Statistics.AddCallback addCallback);
	
	public final MovableState getMovableState(AbstractMovable movable, double t) {
		return interpolator.getState(movable, t);
	}

	public static class MovableState {
		public final Point2D.Double position;
	
		public MovableState(AbstractMovable movable) {
			position = movable.getPosition();
		}
		
		public MovableState(Point2D.Double position) {
			this.position = position;
		}
	}

	/** tego nie ruszamy :] tłumaczy Vector<AbstractPerson> -> Vector<T> */
	@SuppressWarnings("unchecked")
	public final void simulateWrapper(double duration,
			Vector<AbstractPerson> people,
			World.ProgressCallback progressCallback,
			Statistics.AddCallback addCallback) {
		Vector<T> castedPeople = new Vector<T>();
		for (AbstractPerson p : people)
			castedPeople.add((T) p);
		simulate(duration, castedPeople, progressCallback, addCallback);
	}
}
