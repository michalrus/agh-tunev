/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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