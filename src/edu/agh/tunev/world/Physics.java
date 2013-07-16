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

import java.util.EnumMap;
import java.util.Map;

public final class Physics {
	public enum Type {
		TEMPERATURE, CO
	}
	
	/**
	 * Zwraca konkretne wartości fizyczne z danego punktu.
	 * 
	 * Przykład użycia:
	 * 
	 * <pre><code>
	 * Physics p = world.getPhysicsAt(13.33, 0.0, 1.0); 
	 * double temp = p.get(Physics.Type.TEMPERATURE);
	 * double co = p.get(Physics.Type.CO);
	 * </code></pre>
	 * 
	 * @param type  Typ wartości.
	 * @return
	 */
	public double get(Type type) {
		Double v = data.get(type);
		if (v == null)
			return Double.NaN;
		return v;
	}
	
	private Map<Type, Double> data = new EnumMap<Type, Double>(Type.class);
	
	void set(Type type, double value) {
		data.put(type, value);
	}
}
