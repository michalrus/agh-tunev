package edu.agh.tunev.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class Obstacle {
	
	public final Point2D.Double p1, p2;
	
	public final double height = 1.0; // [m]
	
	public final boolean isFireSource;
	
	private final Rectangle2D.Double rectangle;

	public Obstacle(Point2D.Double p1, Point2D.Double p2, boolean isFireSource) {
		this.p1 = p1;
		this.p2 = p2;
		this.isFireSource = isFireSource;
		
		final double x = Math.min(p1.x, p2.x);
		final double y = Math.min(p1.y, p2.y);
		final double w = Math.abs(p2.x - p1.x);
		final double h = Math.abs(p2.y - p1.y);
		rectangle = new Rectangle2D.Double(x, y, w, h);
	}
	
	public boolean contains(Point2D.Double point) {
		return rectangle.contains(point);
	}

	public boolean contains(Point2D.Double point, double margin) {
		return contains(new Point2D.Double(point.x - margin, point.y - margin))
				|| contains(new Point2D.Double(point.x - margin, point.y + margin))
				|| contains(new Point2D.Double(point.x + margin, point.y + margin))
				|| contains(new Point2D.Double(point.x + margin, point.y - margin))
				;
	}

}
