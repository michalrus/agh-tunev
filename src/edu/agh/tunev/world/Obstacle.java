package edu.agh.tunev.world;

import java.awt.geom.Point2D;

public final class Obstacle {
	
	public final Point2D.Double p1, p2;
	
	public final double height = 1.0; // [m]

	public Obstacle(Point2D.Double p1, Point2D.Double p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

}
