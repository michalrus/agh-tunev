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

package edu.agh.tunev.model.cellular;

import java.util.Vector;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState.Movement;
import edu.agh.tunev.model.cellular.agent.NotANeighbourException;
import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.model.cellular.agent.WrongOrientationException;
import edu.agh.tunev.model.cellular.grid.Board;
import edu.agh.tunev.model.cellular.grid.Cell;
import edu.agh.tunev.statistics.LifeStatistics;
import edu.agh.tunev.statistics.Statistics.AddCallback;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public final class Model extends AbstractModel {

	public final static String MODEL_NAME = "Social Distances Cellular Automata";
	private final static double INTERSECTION_TOLERANCE = 0.001;

	public Model(World world) {
		super(world);
	}

	public static final double DT = 0.05;

	private Board board;
	private AllowedConfigs allowedConfigs;

	@Override
	public void simulate(double duration, Vector<PersonProfile> profiles,
			ProgressCallback progressCallback, AddCallback addCallback) {
		// show some initialization message in UI; it takes some time before
		// this
		// start to iterate
		int num = (int) Math.round(Math.ceil(world.getDuration() / DT));
		progressCallback.update(0, num, "Initializing...");

		// TODO: add some plots to the UI related to this model
		//
		// sidenote: see javadoc for Statistics interface
		LifeStatistics lifeStatistics = new LifeStatistics();
		addCallback.add(lifeStatistics);

		// create the automaton (board of cells)
		board = new Board(world);

		// TODO: exception handling
		try {
			allowedConfigs = new AllowedConfigs(PersonProfile.WIDTH,
					PersonProfile.GIRTH, Cell.CELL_SIZE, INTERSECTION_TOLERANCE);
		} catch (NeighbourIndexException | WrongOrientationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// create our own person representations
		Vector<Person> people = new Vector<Person>();
		for (PersonProfile profile : profiles)
			try {
				people.add(new Person(profile, board.getCellAt(Cell
						.c2d(profile.initialPosition)), allowedConfigs));
			} catch (WrongOrientationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// iterations of automaton -- caution: no waiting (like Thread.sleep)
		// here -- this has to be calculated *as fast as possible*! --
		// simulation playback is independent of calculation
		double t = 0;
		for (int iteration = 1; iteration <= num; iteration++) {
			// update the real time of our simulation
			t += DT;

			board.update(t);

			// take "shots" of people in their current states
			for (Person p : people) {
				try {
					try {
						p.update();
					} catch (NotANeighbourException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NeighbourIndexException | WrongOrientationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				interpolator.saveState(p.profile, t, p.getCurrentState());

			}

			int alive = 0;
			int dead = 0;
			int rescued = 0;
			for (Person p : people) {
				if (p.isAlive())
					++alive;
				else
					++dead;

				if (p.getCurrentState().movement == Movement.HIDDEN) // ;) what
																		// does
																		// .getMovement()
																		// do?
					++rescued;
			}

			lifeStatistics.add(t, alive, rescued, dead);

			// courtesy: increase ProgressBar value in UI
			progressCallback.update(iteration, num,
					(iteration < num ? "Simulating..." : "Done."));
		}

		// TODO: finally, add datasets to plots that can be filled only
		// after simulation ends

		// that's it ^_^
	}

}
