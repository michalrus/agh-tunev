package edu.agh.tunev.model;

import java.util.Vector;

import edu.agh.tunev.world.World;

/**
 * Po tym dziedziczy klasa g³ówna ka¿dego modelu. -- m.
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
	 *            Wywo³ujemy po ka¿dej iteracji callback.update(done, total,
	 *            msg), gdzie done to numer aktualnej iteracji, a total to
	 *            liczba wszystkich zaplanowanych, a msg to jakiœ komunikat
	 *            tekstowy, mo¿e byæ ""/null. Po to, ¿eby rysowaæ ProgressBar
	 *            ile ju¿ siê policzy³o z ca³oœci.
	 */
	public abstract void simulate(double duration, Vector<Person> people,
			World.ProgressCallback callback);

}
