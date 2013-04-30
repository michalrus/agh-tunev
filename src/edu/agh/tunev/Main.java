package edu.agh.tunev;

import edu.agh.tunev.ui.MainFrame;

public class Main {

	/**
	 * Tutaj rejestrujemy wszystkie nasze modele, które mają być widoczne w UI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// modele
		MainFrame.register(edu.agh.tunev.model.cellular.Model.class);

		new MainFrame();
	}
}
