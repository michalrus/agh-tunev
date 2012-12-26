package board;

public final class Point {
	/**
	 * Position.
	 * 
	 * Leave public for ease of use, e.g.
	 * 
	 * {@code Point a = new Point(1.13, 2.0);
	 * if (a.x > 1.0)
	 *     a.y = 13.5;} -- m.
	 */
	public double x, y;

	public Point() {
		init(0.0, 0.0);
	}

	public Point(double x, double y) {
		init(x, y);
	}

	private void init(double x, double y) {
		this.x = x;
		this.y = y;
	}

}
