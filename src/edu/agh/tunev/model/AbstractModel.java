/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package edu.agh.tunev.model;

import java.util.Vector;

import edu.agh.tunev.world.World;
import edu.agh.tunev.statistics.Statistics;

/**
 * Po tym dziedziczy klasa główna każdego modelu.
 * 
 * Żeby "zarejestrować" nowy model, żeby był widoczny w UI, trzeba dodać linijkę
 * z nazwą jego klasy do funkcji wejścia <code>edu.agh.tunev.Main.main()</code>.
 * 
 */
public abstract class AbstractModel {

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
	 * @param profiles
	 *            Lista profili osób stworzona przez użytkownika.
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
	public abstract void simulate(double duration,
			Vector<PersonProfile> profiles,
			World.ProgressCallback progressCallback,
			Statistics.AddCallback addCallback);

	public final PersonState getPersonState(PersonProfile person, double t) {
		return interpolator.getState(person, t);
	}

}
