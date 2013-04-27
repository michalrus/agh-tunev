package edu.agh.tunev.model;

import java.util.Vector;

import edu.agh.tunev.world.World;

/**
 * Po tym dziedziczy klasa g³ówna ka¿dego modelu. -- m.
 * 
 * ¯eby "zarejestrowaæ" nowy model, ¿eby by³ widoczny w UI, trzeba dodaæ linijkê
 * z nazw¹ jego klasy do <code>edu.agh.tunev.Main.main()</code>.
 * 
 */
public abstract class AbstractModel {

	final protected World world;

	/**
	 * Nazwa modelu w UI.
	 */
	public static String MODEL_NAME;

	public AbstractModel(World world) {
		this.world = world;
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
	public abstract void simulate(double duration, Vector<Person> people,
			World.ProgressCallback callback);

}
