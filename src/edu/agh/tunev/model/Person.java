package edu.agh.tunev.model;

import edu.agh.tunev.world.World;

/**
 * Ta klasa reprezentuje osobê. Cechy osoby nie zale¿¹ od modelu, dlatego ka¿dy
 * model u¿ywa takiej samej osoby. Dlatego final. Nie dziedziczymy. -- m.
 * 
 */
public final class Person extends AbstractMovable {
	
	/** Szerokoœæ osoby (OX) [m] */
	private double width = 0.5;

	/** Gruboœæ osoby (OY) [m] */
	private double girth = 0.3;

	/** Wysokoœæ osoby (OZ) [m] */
	private double height = 1.7;

	public Person(World world, double x, double y) {
		super(world);
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
