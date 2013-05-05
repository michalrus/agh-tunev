package edu.agh.tunev;

import edu.agh.tunev.ui.MainFrame;

public class Main {

	/**
	 * Tutaj rejestrujemy wszystkie nasze modele, które mają być widoczne w UI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame.register(edu.agh.tunev.model.cellular.Model.class);
		MainFrame.register(edu.agh.tunev.model.kkm.Model.class);

		new MainFrame();
	}
}
