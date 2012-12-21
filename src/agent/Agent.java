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

		/**
		 * Zaklada ze stoimy posrodku rozy wiatrow. Zwraca wartosc posiadajaca
		 * indeks tablicy values() wiekszy o 1
		 * 
		 * @return kierunek po obrocie w lewo
		 */
		public static Orientation turnLeft(Orientation currOrient) {
			if (values() == null)
				return null;

			int index = 0;
			int val_len = values().length;
			for (int i = 0; i < val_len; ++i) {
				if (values()[i] == currOrient)
					index = (i + 1) % (val_len);
			}
			return values()[index];

		}

		/**
		 * Analogicznie do turnLeft(), tylko ze tym razem obrot w prawo. Zwraca
		 * element o indeksie mniejszym o 1
		 */
		public static Orientation turnRight(Orientation currOrient) {
			if (values() == null)
				return null;

			int index = 0;
			int val_len = values().length;
			for (int i = 0; i < val_len; ++i) {
				if (values()[i] == currOrient) {
					index = i - 1;
					if (index < 0)
						index += val_len;
				}
			}
			return values()[index];
		}
	}

	/** Wspolczynnik wagowy obliczonego zagro¿enia */
	private static final double THREAT_COEFF = 10;

	/** Wspolczynnik wagowy odleg³oœci od wyjœcia */
	// private static final double EXIT_COEFF = 5;

	/** Wspolczynnik wagowy dla czynników spo³ecznych */
	// private static final double SOCIAL_COEFF = 0.01;

	/** Smiertelna wartosc temp. na wysokosci 1,5m */
	private static final double LETHAL_TEMP = 80;

	/** Stezenie CO w powietrzu powodujace natychmiastowy zgon [ppm] */
	private static final double LETHAL_CO_CONCN = 30000.0;

	/** Stezenie karboksyhemoglobiny we krwi powodujace natychmiastowy zgon [%] */
	private static final double LETHAL_HbCO_CONCN = 75.0;

	/** Prêdkoœæ z jak¹ usuwane s¹ karboksyhemoglobiny z organizmu */
	private static final double CLEANSING_VELOCITY = 0.08;

	/** Wspolczynnik wagowy dla kierunku przeciwnego do potencjalnego ruchu */
	private static double THREAT_COMP_BEHIND = 0.5;

	/** Wspolczynnik wagowy dla potencjalnego kierunku ruchu */
	private static double THREAT_COMP_AHEAD = 1;

	// TODO: do wywalenia jak najszybciej
	private static double RIGHT_EXIT_COEFF = 15;

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
	 * @param board
	 *            referencja do planszy
	 * @param position
	 *            referencja to komórki bêd¹cej pierwotn¹ pozycj¹ agenta
	 */
	// TODO: Tworzenie cech osobniczych
	public Agent(Board board, Cell position) {
		alive = true;
		this.board = board;
		setPosition(position);
		orientation = Orientation.getRandom();
		neighborhood = board.getNeighborhoods(this);
		hbco = 0;
	}

	/**
	 * Akcje agenta w danej iteracji.
	 * 
	 * 1. Sprawdza, czy agent zyje - jesli nie, to wychodzi z funkcji.
	 * 
	 * 2. Sprawdza, czy agent nie powinien zginac w tej turze.
	 * 
	 * 3. Sprawdza jakie sa dostepne opcje ruchu.
	 * 
	 * 4. Na podstawie danych otrzymanych w poprzednim punkcie podejmuje decyzje
	 * i wykouje ruch
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
	 * TODO: to wyleci w nowym podejœciu oczywiœcie. :>
	 * 
	 * @param newPosition
	 */
	public void setPosition(Cell newPosition) {
		if (position != null)
			position.removeAgent();
		position = newPosition;
		position.addAgent(this);
	}

	public Cell getPosition() {
		return position;
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
	 *         {@code // Micha³ Rus lubi
	 *         to. ^-^ Czat w komentarzach, jea!}
	 */
	private boolean checkIfIWillLive() {
		evaluateHbCO();

		if (hbco > LETHAL_HbCO_CONCN || position.getTemperature() > LETHAL_TEMP)
			alive = false;

		return alive;
	}

	/**
	 * Funkcja oblicza aktualne stezenie karboksyhemoglobiny, uwzgledniajac
	 * zdolnosci organizmu do usuwania toksyn
	 */
	// TODO: Zastanowic sie, czy to faktycznie jest funkcja liniowa
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
	// TODO: Zmodyfikowac mozliwosc pozostanie w miejscu
	private HashMap<Direction, Double> createMoveOptions() {
		HashMap<Direction, Double> move_options = new HashMap<Direction, Double>();

		for (Map.Entry<Direction, Neighborhood> entry : neighborhood.entrySet()) {
			Cell first = entry.getValue().getFirstCell();
			if (first != null && !first.isOccupied())
				move_options.put(entry.getKey(), 0.0);
		}

		for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
			Direction key = entry.getKey();
			Double attractivness = 0.0;
			attractivness += THREAT_COEFF
					* computeAttractivnessComponentByThreat(key);
			move_options.put(key, attractivness);
		}

		// prowizorka
		for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
			Direction key = entry.getKey();
			double val = entry.getValue();
			switch (orientation) {
			case NORTH:
				if (key == Neighborhood.Direction.LEFT)
					val += RIGHT_EXIT_COEFF;
				break;
			case SOUTH:
				if (key == Neighborhood.Direction.RIGHT)
					val += RIGHT_EXIT_COEFF;
				break;
			case EAST:
				if (key == Neighborhood.Direction.BOTTOM)
					val += RIGHT_EXIT_COEFF;
				break;
			case WEST:
				if (key == Neighborhood.Direction.TOP)
					val += RIGHT_EXIT_COEFF;
				break;
			}
			move_options.put(key, val);
		}

		return move_options;
	}

	/**
	 * 1. Analizuje wszystkie dostepne opcje ruchu pod katem atrakcyjnosci i
	 * dokonuje wyboru.
	 * 
	 * 2. Sprawdza, czy op³aca jej sie ruch, jesli nie to pomija kolejne
	 * instrukcje.
	 * 
	 * 2. Obraca sie w kierunku ruchu.
	 * 
	 * 3. Wykonuje ruch.
	 * 
	 * 4. Aktualizuje sasiedztwo.
	 */
	private void move(HashMap<Direction, Double> move_options) {
		Direction dir = null;
		Double top_attractivness = null;

		for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
			Double curr_attractivness = entry.getValue();
			if (top_attractivness == null
					|| curr_attractivness > top_attractivness) {
				top_attractivness = curr_attractivness;
				dir = entry.getKey();
			}
		}

		if (top_attractivness > -THREAT_COEFF * position.getTemperature()) {
			rotate(dir);
			setPosition(neighborhood.get(dir).getFirstCell());
			neighborhood = board.getNeighborhoods(this);
		}
	}

	/**
	 * Funkcja obraca agenta do kierunku jego ruchu
	 * 
	 * Ale¿ to jest zakrêcone, czas na zmiany! -- m.
	 */
	// TODO: Poprawic
	private void rotate(Direction dir) {
		switch (dir) {
		case LEFT:
			orientation = Orientation.turnLeft(orientation);
			break;
		case RIGHT:
			orientation = Orientation.turnRight(orientation);
			break;
		case BOTTOMLEFT:
		case BOTTOMRIGHT:
		case BOTTOM:
			orientation = Orientation.turnRight(orientation);
			orientation = Orientation.turnRight(orientation);
			break;
		default:
			break;
		}
	}

	/**
	 * Oblicza chec wyboru danego kierunku, biorac pod uwage zarowno chec ruchu
	 * w dana strone, jak i chec ucieczki od zrodla zagrozenia.
	 * 
	 * @param dir
	 *            potencjalnie obrany kierunek
	 * @return wspolczynnik atrakcyjnosci dla zadanego kierunku, im wyzszy tym
	 *         LEPIEJ
	 */
	private double computeAttractivnessComponentByThreat(Direction dir) {
		double attractivness_comp = 0.0;
		attractivness_comp -= THREAT_COMP_AHEAD
				* neighborhood.get(dir).getTemperature();
		attractivness_comp += THREAT_COMP_BEHIND
				* neighborhood.get(Direction.getOppositeDir(dir))
						.getTemperature();

		return attractivness_comp;
		// TODO: rozwinac
	}

	// private void computeAttractivnessComponentByExit() {
	// TODO: sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne)
	// }

	// private void computeAttractivnessComponentBySocialDistances() {
	// TODO: sk³adowa potencja³u od Social Distances
	// }

	// private void updateMotorSkills() {
	// TODO: ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	// }

}
