package agent;

import java.util.ArrayList;
import java.util.Map;

import agent.Neighborhood.Direction;
import board.Board;
import board.Cell;

public class Agent {

	/**Mo¿liwa orientacja agenta */
	public enum Orientation {
		SOUTH, EAST, NORTH, WEST;
		
		/**Losuje orientacje*/
		public static Orientation getRandom(){
			return values()[(int)Math.random() * values().length];
		}
	}
	
	/** Wspolczynnik wagowy obliczonego zagro¿enia */
	private static final int THREAT_COEFF = 100;

	/** Wspolczynnik wagowy odleg³oœci od wyjœcia */
	private static final int EXIT_COEFF = 10;

	/** Wspolczynnik wagowy dla czynników spo³ecznych */
	private static final int SOCIAL_COEFF = 1;
	
	/**Stezenie CO w powietrzu powodujace natychmiastowy zgon [ppm]*/
	private static final int LETHAL_CO_CONCN = 30000;
	
	/**Stezenie karboksyhemoglobiny we krwi powodujace natychmiastowy zgon [%]*/
	private static final int LETHAL_HbCO_CONCN = 75;

	/**Prêdkoœæ z jak¹ usuwane s¹ karboksyhemoglobiny z organizmu*/
	private static final int CLEANSING_VELOCITY = 6;
	

	/** Flaga informuj¹ca o statusie jednostki - zywa lub martwa */
	private boolean alive;
	
	/** Referencja do planszy */
	private Board board;
	
	/**Komórka, w której aktualnie znajduje siê agent*/
	private Cell position;
	
	/** Kierunek, w którym zwrócony jest agent */
	private Orientation orientation;

	/** Otoczenie agenta pobierane przy ka¿dym update()'cie */
	private Map<Direction, Neighborhood> neighborhood;
	
	/**Aktualne stezenie karboksyhemoglobiny we krwii*/
	private double hbco;

	
	/**
	 * Konstruktor agenta. Inicjuje wszystkie pola niezbêdne do jego egzystencji na planszy. 
	 * Pozycja jest z góry narzucona z poziomu Board. Orientacja zostaje wylosowana.
	 * 
	 * @param board
	 *            referencja do planszy
	 * @param position
	 * 			  referencja to komórki bêd¹cej pierwotn¹ pozycj¹ agenta
	 */
	// TODO: Tworzenie cech osobniczych
	public Agent(Board _board, Cell _position) {
		alive = true;
		this.board = _board;
		this.position = _position;
		orientation = Orientation.getRandom();
		neighborhood = board.getNeighborhoods(this);
		hbco = 0;
	}

	/** Akcje agenta w danej iteracji */
	public void update() {
		if (!alive)
			return;
		
		// kuba, wybacz, ¿e Ci tu smarujê, ale chcia³em pokazaæ jak tego u¿ywaæ :}
		neighborhood = board.getNeighborhoods(this);
		if (neighborhood.get(Neighborhood.Direction.BOTTOM).getTemperature() < 70) {
			// jest dobrze
		}
		// koniec smarowania -- micha³

		checkIfIWillLive();

		checkCollisions();

		computePotentialComponentByThreat();
		computePotentialComponentByExit();
		computePotentialComponentBySocialDistances();

		decideWhereToGo();

		updateMotorSkills();
	}

	/**Zwraca kierunek, w którym zwrócony jest agent*/
	public Orientation getOrientation(){
		return orientation;
	}

	/**Funkcja oblicza aktualne stezenie karboksyhemoglobiny, 
	 * uwzgledniajac zdolnosci organizmu do usuwania toksyn*/
	private void evaluateHbCO(){
		if(hbco > CLEANSING_VELOCITY)
			hbco -= CLEANSING_VELOCITY;
		
		hbco += LETHAL_HbCO_CONCN * (position.getCOConcentration() / LETHAL_CO_CONCN);
	}
	
	/**Okresla, czy agent przezyje, sprawdzajac temperature otoczenia i 
	 * stezenie toksyn we krwii 
	 *
	 * @return  
	 * 			zwraca status agenta, zeby nie wykonywac potem niepotrzebnie obliczen w update(),
	 * 			skoro i tak jest martwy ;)
	 */
	private boolean checkIfIWillLive() {
		evaluateHbCO();
		
		if(hbco > LETHAL_HbCO_CONCN ||
		   position.getTemperature() > 80)
			alive = false;
		
		return alive;
	}

	private void checkCollisions() {
		// TODO: sprawdzenie kolizji
	}

	private void computePotentialComponentByThreat() {
		// TODO: sk³adowa potencja³u od zagro¿enia po¿arowego
	}

	private void computePotentialComponentByExit() {
		// TODO: sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne)
	}

	private void computePotentialComponentBySocialDistances() {
		// TODO: sk³adowa potencja³u od Social Distances
	}

	private void decideWhereToGo() {
		// TODO: jeœli czekamy, ¿eby symulowaæ zmianê prêdkoœci, przechowaj
		// decyzjê na potem?
	}

	private void updateMotorSkills() {
		// TODO: ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	}

}