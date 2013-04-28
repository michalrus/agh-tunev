package edu.agh.tunev;

import edu.agh.tunev.ui.MainFrame;

public class Main {

	/**
	 * Tutaj rejestrujemy wszystkie nasze modele, które maj¹ byæ widoczne w UI.
	 * 
	 * Tak¿e: wszystkie wykresy.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// modele
		MainFrame.register(edu.agh.tunev.model.cellular.Model.class);
		
		// wykresy
		MainFrame.register(edu.agh.tunev.ui.plot.Example.class);

		new MainFrame();
	}
}
