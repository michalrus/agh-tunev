package edu.agh.tunev.model;

import java.awt.geom.Point2D;

/**
 * Reprezentuje stan przypisany do danego PersonProfile w jakiejś chwili czasu,
 * niezależny od wybranego konkretnego modelu.
 * 
 * Obiekty tej klasy używane są głównie do rysowania przebiegu symulacji.
 * 
 * Obiekty tej klasy w postaci przekazanej interpolatorowi są w nim pamiętane,
 * więc klasa ta musi być niezmienna (wszystkie jej pola). Gdyby zmienić jedno
 * pole już zapisanego obiektu, to zmieniłby się też w pamięci Interpolatora, a
 * tego nie chcemy.
 * 
 */
public final class PersonState {

	public static enum Movement {
		STANDING, SQUATTING, CRAWLING, DEAD, HIDDEN
	}

	public final Point2D.Double position;
	public final double orientation;
	public final Movement movement;

	public PersonState(Point2D.Double position, double orientation,
			Movement movement) {
		this.position = position;
		this.orientation = orientation;
		this.movement = movement;
	}

}