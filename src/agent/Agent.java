package agent;

import java.util.HashMap;
import java.util.Map;

import agent.Neighborhood.Direction;
import board.Board;
import board.Cell;

public class Agent {

	/** Mo¿liwa orientacja agenta */
	public enum Orientation {
		SOUTH, EAST, NORTH, WEST;

		/** Losuje orientacje */
		public static Orientation getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
	}

	/** Wspolczynnik wagowy obliczonego zagro¿enia */
	private static final int THREAT_COEFF = 100;

	/** Wspolczynnik wagowy odleg³oœci od wyjœcia */
	private static final int EXIT_COEFF = 10;

	/** Wspolczynnik wagowy dla czynników spo³ecznych */
	private static final int SOCIAL_COEFF = 1;

	/** Stezenie CO w powietrzu powodujace natychmiastowy zgon [ppm] */
	private static final int LETHAL_CO_CONCN = 30000;

	/** Stezenie karboksyhemoglobiny we krwi powodujace natychmiastowy zgon [%] */
	private static final int LETHAL_HbCO_CONCN = 75;

	/** Prêdkoœæ z jak¹ usuwane s¹ karboksyhemoglobiny z organizmu */
	private static final int CLEANSING_VELOCITY = 6;

	/** Flaga informuj¹ca o statusie jednostki - zywa lub martwa */
	private boolean alive;

	/** Referencja do planszy */
	private Board board;

	/**
	 * Komórka, w której aktualnie znajduje siê agent. Nie nadpisujemy jej
	 * rêcznie, tylko przez {@link #setPosition()}!
	 */
	private Cell position;

	/** Kierunek, w którym zwrócony jest agent */
	private Orientation orientation;

	/** Otoczenie agenta pobierane przy ka¿dym update()'cie */
	private Map<Direction, Neighborhood> neighborhood;

	/** Aktualne stezenie karboksyhemoglobiny we krwii */
	private double hbco;

	/**
	 * Konstruktor agenta. Inicjuje wszystkie pola niezbêdne do jego egzystencji
	 * na planszy. Pozycja jest z góry narzucona z poziomu Board. Orientacja
	 * zostaje wylosowana.
	 * 
	 * @param _board
	 *            referencja do planszy
	 * @param _position
	 *            referencja to komórki bêd¹cej pierwotn¹ pozycj¹ agenta
	 */
	// TODO: Tworzenie cech osobniczych
	public Agent(Board _board, Cell _position) {
		alive = true;
		this.board = _board;
		setPosition(_position);
		orientation = Orientation.getRandom();
		neighborhood = board.getNeighborhoods(this);
		hbco = 0;
	}

	/**
	 * Akcje agenta w danej iteracji. 1. Sprawdza, czy agent zyje - jesli nie,
	 * to wychodzi z funkcji. 2. Sprawdza, czy agent nie powinien zginac w tej
	 * turze. 3. Sprawdza jakie sa dostepne opcje ruchu. 4. Na podstawie danych
	 * otrzymanych w poprzednim punkcie podejmuje decyzje i wykouje ruch
	 */
	public void update() {
		if (!alive)
			return;

		if (checkIfIWillLive()) {
			move(createMoveOptions());
		}
	}

	/**
	 * Nie nadpisujmy {@link #position} rêcznie, tylko t¹ metod¹. Potrzebujê w
	 * komórce mieæ referencjê do agenta, jeœli na niej stoi (rysowanie).
	 * 
	 * @param newPosition
	 */
	public void setPosition(Cell newPosition) {
		if (position != null)
			position.removeAgent();
		position = newPosition;
		position.addAgent(this);
	}

	public boolean isAlive() {
		return alive;
	}

	/** Zwraca kierunek, w którym zwrócony jest agent */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Okresla, czy agent przezyje, sprawdzajac temperature otoczenia i stezenie
	 * toksyn we krwii
	 * 
	 * @return zwraca status agenta, zeby nie wykonywac potem niepotrzebnie
	 *         obliczen w update(), skoro i tak jest martwy ;)
	 */
	private boolean checkIfIWillLive() {
		evaluateHbCO();

		if (hbco > LETHAL_HbCO_CONCN || position.getTemperature() > 80)
			alive = false;

		return alive;
	}

	/**
	 * Funkcja oblicza aktualne stezenie karboksyhemoglobiny, uwzgledniajac
	 * zdolnosci organizmu do usuwania toksyn
	 */
	private void evaluateHbCO() {
		if (hbco > CLEANSING_VELOCITY)
			hbco -= CLEANSING_VELOCITY;

		hbco += LETHAL_HbCO_CONCN
				* (position.getCOConcentration() / LETHAL_CO_CONCN);
	}

	/**
	 * Sprawdza jakie s¹ dostêpne opcje ruchu, a nastêpnie szacuje, na ile sa
	 * atrakcyjne dla agenta Najpierw przeszukuje s¹siednie komórki w
	 * poszukiwaniu przeszkód i wybieram tylko te, które s¹ puste. Nastêpnie
	 * szacuje wspó³czynnik atrakcyjnoœci dla ka¿dej z mo¿liwych opcji ruchu na
	 * podstawie zagro¿enia, odleg³oœci od wyjœcia, itd.
	 * 
	 * @return HashMapa kierunków wraz ze wspó³czynnikami atrakcyjnoœci
	 * */
	// TODO: dodac wiecej
	private HashMap<Direction, Double> createMoveOptions() {
		HashMap<Direction, Double> move_options = new HashMap<Direction, Double>();

		for (Map.Entry<Direction, Neighborhood> entry : neighborhood.entrySet()) {
			if (!entry.getValue().getFirstCell().isOccupied())
				move_options.put(entry.getKey(), 0.0);
		}

		for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
			Direction key = entry.getKey();
			Double attractivness = 0.0;
			attractivness += computeAttractivnessComponentByThreat(neighborhood.get(key));
		}

		return move_options;
	}

	/**
	 * Przeszukuje dostêpne opcje, wybiera najbardziej atrakcyjna i wykonuje
	 * ruch
	 */
	private void move(HashMap<Direction, Double> move_options) {
		Direction dir = null;
		Double top_attractivness = null;

		for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
			Double curr_attractivness = entry.getValue();
			if (top_attractivness == null
					|| curr_attractivness < top_attractivness) {
				top_attractivness = curr_attractivness;
				dir = entry.getKey();
			}
		}

		//TODO: prototyp - rozwinac
		switch (dir) {
			case LEFT : 
				rotate();
				break;
			case RIGHT :
				rotate();
				break;
			case BOTTOMLEFT : case BOTTOMRIGHT : 
			case BOTTOM:
				rotate(); rotate();
				break;		
		}

		setPosition(neighborhood.get(dir).getFirstCell());
		// <Micha³> doda³em te¿ na razie jakiœ randomowy ruch, ¿eby zobaczyæ czy
		// dzia³a rysowanie

		// wywal to wszystko poni¿ej!

		/*if (Math.random() < 0.04) // 4% szans na prze¿ycie ; to wszystko jest
									// tylko mój test rysowania!
			alive = false;
		for (;;) {
			Cell cell = board.getCellAt(
					(int) Math.round(Math.floor(Math.random()
							* board.getWidth())),
					(int) Math.round(Math.floor(Math.random()
							* board.getLength())));
			if (!cell.isOccupied()) {
				setPosition(cell);
				break;
			}
		}*/
	}

	/**Funkcja obraca agenta do kierunku jego ruchu*/
	//TODO: stub
	private void rotate(){
		
	}
	
	
	private double computeAttractivnessComponentByThreat(Neighborhood neigh) {
		return neigh.getTemperature();
		// TODO: rozwinac
	}

	private void computeAttractivnessComponentByExit() {
		// TODO: sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne)
	}

	private void computeAttractivnessComponentBySocialDistances() {
		// TODO: sk³adowa potencja³u od Social Distances
	}
	
	private void updateMotorSkills() {
		// TODO: ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	}

}