package edu.agh.tunev.model;

import java.awt.geom.Point2D;

/**
 * Ta klasa reprezentuje osobę: jej cechy niezależne od modelu, cechy które (w
 * przyszłości) możemy wybrać w UI dodając osoby.
 * 
 * Wszystkie cechy "osobnicze", które mają sens poza konkretnym modelem, powinny
 * być dodane do AbstractPerson. Przykład:
 * 
 * Do <code>AbstractPerson</code>: wiek, rasa, orientacja, religious views
 * 
 * Do <code>Person extends AbstractPerson</code>: informacja o tym co osoba
 * sądzi nt. konkretnych pól automatu komórkowego
 */
public abstract class AbstractPerson {

	protected Point2D.Double position;

	/** Szerokość osoby (OX) [m] */
	protected double width = 0.5;

	/** Grubość osoby (OY) [m] */
	protected double girth = 0.3;

	/** Wysokość osoby (OZ) [m] */
	protected double height = 1.7;

	public AbstractPerson(Point2D.Double position) {
		this.position = position;
	}
	
	public Point2D.Double getPosition() {
		return position;
	}
	
	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	public double getWidth() {
		return width;
	}

	public double getGirth() {
		return girth;
	}

	public double getHeight() {
		return height;
	}

}
