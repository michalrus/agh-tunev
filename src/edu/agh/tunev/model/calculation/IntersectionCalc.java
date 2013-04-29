package edu.agh.tunev.model.calculation;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public abstract class IntersectionCalc {

	public static Double calculateIntersection(Shape shape1, Shape shape2){
		Area area1 = new Area(shape1);
		Area area2 = new Area(shape2);
		area1.intersect(area2);
		Rectangle2D rect = area1.getBounds2D();
		
		return rect.getWidth() * rect.getHeight();	
	}
}
