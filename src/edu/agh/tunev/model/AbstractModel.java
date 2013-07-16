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
 * Main class of every model inherits this.
 * 
 * To register your model in UI, add it in <code>edu.agh.tunev.Main.main</code>. 
 */
public abstract class AbstractModel {

	final protected World world;
	final protected Interpolator interpolator;

	/**
	 * Name of the model in UI.
	 * 
	 * *Do* set it in derived class or register() in main() will throw.
	 */
	public static String MODEL_NAME;

	public AbstractModel(World world) {
		this.world = world;
		interpolator = new Interpolator();
	}

	/**
	 * Starts the simulation.
	 * 
	 * @param duration
	 *            Duration of the sim.
	 * @param profiles
	 *            List of PersonProfiles created by user.
	 * @param progressCallback
	 *            We call <code>callback.update(done, total,
	 *            msg</code> after each iteration. <code>done</code>
	 *            is a number of current iteration and <code>total</code>
	 *            is a number of all planned iteratons and <code>msg</code>
	 *            is some text message to display to user, it can be
	 *            <code>""</code>/<code>null</code>. Used to draw simulation's
	 *            progress.
	 * @param addCallback
	 *            Called when we want to add some plot to UI. At any moment.
	 *            We might add a plot at the beginning a then provide new
	 *            data samples in every iteration. Or add it at the end.
	 */
	public abstract void simulate(double duration,
			Vector<PersonProfile> profiles,
			World.ProgressCallback progressCallback,
			Statistics.AddCallback addCallback);

	public final PersonState getPersonState(PersonProfile person, double t) {
		return interpolator.getState(person, t);
	}

}
