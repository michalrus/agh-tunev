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

package edu.agh.tunev.world;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;

public class World {

	// -- world-data access methods

	public double getDuration() {
		return data.getDuration();
	}

	public Point2D.Double getDimension() {
		return data.getDimension();
	}

	public Vector<Exit> getExits() {
		return data.getExits();
	}

	public Vector<Obstacle> getObstacles() {
		return data.getObstacles();
	}

	public Vector<FireSource> getFireSources() {
		return data.getFireSources();
	}

	/**
	 * Zawsze zwróci jakąś wartość. Kiedy zapytasz o coś spoza granic świata,
	 * zwróci wartość z najbliższego punktu leżącego na brzegu.
	 * 
	 * Przykład użycia:
	 * 
	 * <pre>
	 * <code>
	 * Physics p = world.getPhysicsAt(13.33, new Point2D.Double(0.0, 1.0)); 
	 * double temp = p.get(Physics.Type.TEMPERATURE);
	 * double co = p.get(Physics.Type.CO);
	 * </code>
	 * </pre>
	 * 
	 * @param t
	 *            Rzeczywisty czas, o który pytasz.
	 * @param p
	 *            Punkt, o który pytasz.
	 * @return
	 */
	public Physics getPhysicsAt(double t, Point2D.Double p) {
		return data.getPhysicsAt(t, p);
	}
	
	/** zwraca dyskretyzację fizyki */
	public Point2D.Double getPhysicsGranularity() {
		return data.getPhysicsGranularity();
	}

	// -- end of world-data access methods

	public interface ProgressCallback {
		void update(int done, int total, String msg);
	}

	private AbstractDataSource data;

	public void readData(File dir, ProgressCallback callback) {
		if (data != null)
			throw new IllegalArgumentException(
					"World.readData() already called on this World");

		data = new FDSDataSource();
		data.readData(dir, callback);
	}

}
