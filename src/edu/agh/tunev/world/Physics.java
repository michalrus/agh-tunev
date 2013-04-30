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
