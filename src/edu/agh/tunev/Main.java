package edu.agh.tunev;

import edu.agh.tunev.model.cellular.AllowedConfigs;
import edu.agh.tunev.model.cellular.NeighbourIndexException;
import edu.agh.tunev.model.cellular.agent.Person;
import edu.agh.tunev.model.cellular.agent.WrongOrientationException;

public class Main {

	/**
	 * Tutaj rejestrujemy wszystkie nasze modele, które mają być widoczne w
	 * UI.
	 * 
	 * @param args
	 * @throws WrongOrientationException 
	 */
	public static void main(String[] args) throws WrongOrientationException {
		// MainFrame.register(edu.agh.tunev.model.cellular.Model.class);

		// new MainFrame();

		AllowedConfigs cfgs = null;
		try {
			cfgs = new AllowedConfigs(0.45, 0.27, 0.25);
		} catch (NeighbourIndexException | WrongOrientationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cfgs.printIntersectionMap();
		
		
	}
}
