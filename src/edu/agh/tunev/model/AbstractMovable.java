package edu.agh.tunev.model;

import java.awt.geom.Point2D;

/**
 * Po tej klasie musi dziedziczyć wszystko co się rusza -- m.
 * 
 * Ale póki co chyba tylko model.Person. -- m.
 *
 */
public abstract class AbstractMovable {

	protected Point2D.Double position;

	public AbstractMovable(Point2D.Double position) {
		setPosition(position);
	}

	public Point2D.Double getPosition() {
		return position;
	}
	
	public void setPosition(Point2D.Double position) {
		this.position = position;
	}
	
}
