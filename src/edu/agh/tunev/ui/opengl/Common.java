package edu.agh.tunev.ui.opengl;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class Common {

	public static class Color {
		final double r, g, b;

		public Color(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public String toString() {
			return "(" + r + ", " + g + ", " + b + ")";
		}
	}
	
	public static Color temp2Color(double temp) {
		final Entry<Double,Double> prevEntry = heatMapScale.floorEntry(temp);
		final Entry<Double,Double> nextEntry = heatMapScale.ceilingEntry(temp);

		if (prevEntry == null && nextEntry == null)
			return null; // should not happen
		else if (prevEntry == null)
			return jetMap(nextEntry.getValue());
		else if (nextEntry == null)
			return jetMap(prevEntry.getValue());
		else if (prevEntry == nextEntry)
			return jetMap(prevEntry.getValue());

		final double prevI = prevEntry.getValue();
		final double nextI = nextEntry.getValue();

		final double prevT = prevEntry.getKey();
		final double nextT = nextEntry.getKey();

		final double ratio = (temp - prevT) / (nextT - prevT);
		
		return jetMap(prevI + ratio * (nextI - prevI));
	}

	// temp -> jetMap.x
	private final static NavigableMap<Double, Double> heatMapScale = new ConcurrentSkipListMap<Double, Double>();

	static {
		// maps temp [*C] -> jetMap.index [0;1)
		heatMapScale.put(23.0, 0.0);
		heatMapScale.put(100.0, 1.0);
	}

	private static Color jetMap(double x) {
		double r, g, b;
		if (x < -1.0 / 8.0)
			b = 0.0;
		else if (x < 1.0 / 8.0)
			b = (x - -1.0 / 8.0) * 4.0;
		else if (x < 3.0 / 8.0)
			b = 1.0;
		else if (x < 5.0 / 8.0)
			b = (x - 5.0 / 8.0) * -4.0;
		else
			b = 0.0;

		if (x < 1.0 / 8.0)
			g = 0.0;
		else if (x < 3.0 / 8.0)
			g = (x - 1.0 / 8.0) * 4.0;
		else if (x < 5.0 / 8.0)
			g = 1.0;
		else if (x < 7.0 / 8.0)
			g = (x - 7.0 / 8.0) * -4.0;
		else
			g = 0.0;

		if (x < 3.0 / 8.0)
			r = 0.0;
		else if (x < 5.0 / 8.0)
			r = (x - 3.0 / 8.0) * 4.0;
		else if (x < 7.0 / 8.0)
			r = 1.0;
		else if (x < 9.0 / 8.0)
			r = (x - 9.0 / 8.0) * -4.0;
		else
			r = 0.0;

		return new Color(r, g, b);
	}

}
