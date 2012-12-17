package agent;
import java.util.ArrayList;

import board.Board;
import board.Cell;


public class Agent {
	
	/**D³ugoœæ p³aszczyzny zajmowanej przez agenta - linia barkowa*/
	private static final int AGENT_LENGTH = 3;			// TODO: zastanowic sie, czy kazdy agent ma te sam¹ wielkosc
	
	/** Szerokoœæ p³aszczyzny zajmowane przez agenta - oœ prostopad³a do linii barkowej*/
	private static final int AGENT_WIDTH = 2;
	
	/**Wspolczynnik wagowy obliczonego zagro¿enia*/
	private static final int THREAT_COEFF = 100;
	
	/**Wspolczynnik wagowy odleg³oœci od wyjœcia*/
	private static final int EXIT_COEFF = 10;
	
	/**Wspolczynnik wagowy dla czynników spo³ecznych*/
	private static final int SOCIAL_COEFF = 1;
	
	/**Referencja do planszy*/
	private Board board;
	
	/**Flaga informuj¹ca o statusie jednostki - zywa lub martwa*/
	private boolean alive;
	
	/**Tablica 2D komórek zajmowanych przez i s¹siaduj¹cych z agentem */
	private ArrayList<ArrayList<Cell>> surroundings;        //TODO: niezbyt fortunne rozwi¹zanie
	
	
	
	/**
	 * Konstruktor agenta. Zmienia jego status na alive. 
	 * 
	 * @param _board			referencja do planszy
	 * @param _surroundings		referencja do komórek bêd¹cych pierwotnym otoczeniem agenta
	 */
	
	//TODO: Tworzenie cech osobniczych
	public Agent (Board _board, ArrayList<ArrayList<Cell>> _surroundings) {
		alive = true;
		this.surroundings = _surroundings;
		this.board = _board;
		// TODO: losowanie moich cech/charakterystyki
	}
	
	public void update () {
		if (!alive)
			return;
		
		checkIfIWillLive();
		
		checkCollisions();

		computePotentialComponentByThreat();
		computePotentialComponentByExit();
		computePotentialComponentBySocialDistances();

		decideWhereToGo();
		
		updateMotorSkills();
	}

	private void checkIfIWillLive () {
		// TODO: sprawdzenie czy prze¿yjê nastêpn¹ iteracjê
		// if (...)
		//	alive = false;
	}

	private void checkCollisions () {
		// TODO: sprawdzenie kolizji
	}

	private void computePotentialComponentByThreat () {
		// TODO: sk³adowa potencja³u od zagro¿enia po¿arowego 
	}

	private void computePotentialComponentByExit () {
		// TODO: sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne) 
	}

	private void computePotentialComponentBySocialDistances () {
		// TODO: sk³adowa potencja³u od Social Distances
	}

	private void decideWhereToGo () {
		// TODO: jeœli czekamy, ¿eby symulowaæ zmianê prêdkoœci, przechowaj decyzjê na potem?
	}

	private void updateMotorSkills () {
		// TODO: ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	}

}