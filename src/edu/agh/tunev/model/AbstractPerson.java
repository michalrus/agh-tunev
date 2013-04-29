package edu.agh.tunev.model;

/**
 * Ta klasa reprezentuje osobê: jej cechy niezale¿ne od modelu, cechy które (w
 * przysz³oœci) mo¿emy wybraæ w UI dodaj¹c osoby.
 * 
 * Wszystkie cechy "osobnicze", które maj¹ sens poza konkretnym modelem, powinny
 * byæ dodane do AbstractPerson. Przyk³ad:
 * 
 * Do <code>AbstractPerson</code>: wiek, rasa, orientacja, religious views
 * 
 * Do <code>Person extends AbstractPerson</code>: informacja o tym co osoba
 * s¹dzi nt. konkretnych pól automatu komórkowego
 */
public abstract class AbstractPerson extends AbstractMovable {

	/** Szerokoœæ osoby (OX) [m] */
	protected double width = 0.5;

	/** Gruboœæ osoby (OY) [m] */
	protected double girth = 0.3;

	/** Wysokoœæ osoby (OZ) [m] */
	protected double height = 1.7;

	public AbstractPerson(double x, double y) {
		super(x, y);
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
