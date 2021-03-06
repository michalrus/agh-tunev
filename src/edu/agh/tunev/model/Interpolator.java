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

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class Interpolator {

	/**
	 * Saves discreet state in Interpolator. -- m.
	 * 
	 * @param t
	 *            Time of the state.
	 */
	public void saveState(PersonProfile profile, double t, PersonState state) {
		if (profile == null || state == null)
			return;

		NavigableMap<Double, PersonState> states = data.get(profile);
		if (states == null) {
			states = new ConcurrentSkipListMap<Double, PersonState>();
			data.put(profile, states);
		}

		states.put(t, state);
	}

	public PersonState getState(PersonProfile person, double t) {
		NavigableMap<Double, PersonState> states = data.get(person);
		if (states == null)
			return new PersonState(person.initialPosition,
					person.initialOrientation, person.initialMovement);

		final Entry<Double, PersonState> prevEntry = states.floorEntry(t);
		final Entry<Double, PersonState> nextEntry = states.ceilingEntry(t);

		if (prevEntry == null && nextEntry == null)
			return new PersonState(person.initialPosition,
					person.initialOrientation, person.initialMovement);
		else if (prevEntry == null)
			return nextEntry.getValue();
		else if (nextEntry == null)
			return prevEntry.getValue();
		
		final PersonState prev = prevEntry.getValue();
		final PersonState next = nextEntry.getValue();
		final double prevT = prevEntry.getKey();
		final double nextT = nextEntry.getKey();
		
		if (Common.equal(nextT, prevT))
			return next;

		// 1st order splines? should work
		
		final double ratio = (t - prevT) / (nextT - prevT);

		final Point2D.Double p = prev.position;
		final Point2D.Double n = next.position;

		final double px = p.getX();
		final double py = p.getY();
		final double nx = n.getX();
		final double ny = n.getY();

		final double x = px + ratio * (nx - px);
		final double y = py + ratio * (ny - py);

		return new PersonState(new Point2D.Double(x, y), Common.sectDeg(
				prev.orientation, next.orientation, ratio), prev.movement);
	}

	public Interpolator() {
		data = new ConcurrentHashMap<PersonProfile, NavigableMap<Double, PersonState>>();
	}

	private final Map<PersonProfile, NavigableMap<Double, PersonState>> data;

}
