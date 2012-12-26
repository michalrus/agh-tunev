package agent;

import java.util.Random;

import board.Board;
import board.Board.NoPhysicsDataException;
import board.Board.Physics;
import board.Point;

public final class Agent {

	/**
	 * Orientacja: k¹t miêdzy wektorem "wzroku" i osi¹ OX w [deg]. Kiedy wynosi
	 * 0.0 deg, to Agent "patrzy" jak oœ OX (jak na geometrii analitycznej).
	 * Wtedy te¿ sin() i cos() dzia³aj¹ ~intuicyjne, tak samo jak analityczne
	 * wzory. :] -- m.
	 */
	private double phi;

	/** Pozycja Agenta na planszy w rzeczywistych [m]. */
	private Point position;

	/** Szerokoœæ elipsy Agenta w [m]. */
	public static final double BROADNESS = 0.33;

	/** Ten drugi wymiar (gruboœæ?) elipsy Agenta w [m]. */
	public static final double THICKNESS = 0.2;

	/**
	 * D³ugoœæ wektora orientacji Agenta w [m]. Nic nie robi, tylko do
	 * rysowania.
	 */
	public static final double ORIENTATION_VECTOR = 1.0;

	/** Flaga mówi¹ca o tym, czy Agentowi uda³o siê ju¿ ucieæ. */
	boolean exited;

	/** Referencja do planszy. */
	private Board board;

	/** Random number generator. */
	private Random rng;

	/** Wspolczynnik wagowy obliczonego zagro¿enia */
	// private static final double THREAT_COEFF = 10;

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
	// private static double THREAT_COMP_BEHIND = 0.5;

	/** Wspolczynnik wagowy dla potencjalnego kierunku ruchu */
	// private static double THREAT_COMP_AHEAD = 1;

	/** Flaga informuj¹ca o statusie jednostki - zywa lub martwa */
	private boolean alive;

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
	public Agent(Board board, Point position) {
		this.board = board;
		this.position = position;

		rng = new Random();
		phi = 0;// rng.nextDouble() * 360;

		alive = true;
		exited = false;
		hbco = 0;

		// TODO: Tworzenie cech osobniczych.
	}

	/**
	 * Zwraca œredni¹ wartoœæ parametru fizycznego na wybranej powierzchni --
	 * wycinka ko³a o œrodku w œrodku danego Agenta.
	 * 
	 * @param orientation
	 *            K¹t miêdzy wektorem orientacji Agenta a osi¹ symetrii wycinka
	 *            ko³a. Innymi s³owy, jak chcemy wycinek po lewej rêce danego
	 *            Agenta, to dajemy tu 90.0 [deg], jak po prawej to -90.0 [deg].
	 *            (Dlatego, ¿e k¹ty w geometrii analitycznej rosn¹ przeciwnie do
	 *            ruchu wskazówek zegara!).
	 * @param alpha
	 *            Rozstaw "ramion" wycinka ko³a w [deg]. Jak chcemy np. 1/8
	 *            ko³a, to dajemy 45.0 [deg], w miarê oczywiste chyba. Byæ mo¿e
	 *            warto zmieniæ nazwê tego parametru.
	 * @param what
	 *            O któr¹ wielkoœæ fizyczn¹ nam chodzi.
	 * @return
	 */
	private double getMeanPhysics(double orientation, double alpha, Physics what) {
		return 0.0;
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
	 * i wykonuje ruch
	 * 
	 * @param dt
	 *            Czas w [ms] jaki up³yn¹³ od ostatniego update()'u. Mo¿na
	 *            wykorzystaæ go do policzenia przesuniêcia w tej iteracji z
	 *            zadan¹ wartoœci¹ prêdkoœci:
	 *            {@code dx = dt * v * cos(phi); dy = dt * v * sin(phi);}
	 */
	public void update(double dt) {
		if (!alive || exited)
			return;

		checkIfIWillLive();

		if (alive) { // ten sam koszt, a czytelniej, przemieni³em -- m.
			lookAround();
			move(dt);
		}

		// jak wyszliœmy poza planszê, to wyszliœmy z tunelu? exited = true
		// spowoduje zaprzestanie wyœwietlania agenta i podbicie statystyk
		// uratowanych w ka¿dym razie :]
		exited = (position.x < 0 || position.y < 0
				|| position.x > board.getDimension().x || position.y > board
				.getDimension().y);
	}

	/**
	 * Sprawdza czy Agent na swojej planszy aktualnie koliduje z *czymkolwiek*
	 * (innym Agentem, przeszkod¹).
	 * 
	 * U¿ywanie: najpierw ustawiamy nowe {@link #position} i {@link #phi},
	 * sprawdzamy czy {@link #hasCollision()}, jeœli tak, to wracamy do starych.
	 * 
	 * Koncept prawdopodobnie do modyfikacji, na razie tak zapisa³em. -- m.
	 * 
	 * @return
	 */
	public boolean hasCollision() {
		// TODO: Sprawdzanie kolizji.
		return false;
	}

	public Point getPosition() {
		return position;
	}

	/** Zwraca kierunek, w którym zwrócony jest agent */
	public double getOrientation() {
		return phi;
	}

	public boolean isAlive() {
		return alive;
	}

	/**
	 * Okresla, czy agent przezyje, sprawdzajac temperature otoczenia i stezenie
	 * toksyn we krwii
	 */
	private void checkIfIWillLive() {
		evaluateHbCO();

		try {
			if (hbco > LETHAL_HbCO_CONCN
					|| board.getPhysics(position, Physics.TEMPERATURE) > LETHAL_TEMP)
				alive = false;
		} catch (NoPhysicsDataException e) {
			// nie zmieniaj flagi ¿ycia, jeœli nie mamy danych o temperaturze w
			// aktualnym punkcie przestrzeni i czasu (ale ofc. tylko gdy
			// stê¿enie CO pozwala prze¿yæ)
		} catch (IndexOutOfBoundsException e) {
			// proœba o dane spoza planszy
		}
	}

	/**
	 * Funkcja oblicza aktualne stezenie karboksyhemoglobiny, uwzgledniajac
	 * zdolnosci organizmu do usuwania toksyn
	 */
	private void evaluateHbCO() {
		// TODO: Trzeba tê prêdkoœæ teraz uzale¿niæ od dt; jeœli to by³o
		// 0.08/500 ms, to jakby ustawiæ to w³aœnie na 0.08/500 i zawsze tutaj
		// mno¿yæ przez dt tê sta³¹ prêdkoœæ, bêdzie dzia³a³o tak samo.

		if (hbco > CLEANSING_VELOCITY)
			hbco -= CLEANSING_VELOCITY;

		try {
			// TODO: Zastanowiæ siê, czy to faktycznie jest funkcja liniowa.
			hbco += LETHAL_HbCO_CONCN
					* (board.getPhysics(position, Physics.CO) / LETHAL_CO_CONCN);
		} catch (NoPhysicsDataException e) {
			// TODO: Mo¿e po prostu nic nie rób z hbco, jeœli nie mamy danych o
			// tlenku wêgla (II)? KASIU?!...
		} catch (IndexOutOfBoundsException e) {
			// proœba o dane spoza planszy
		}
	}

	/**
	 * Rozejrzenie siê, wstêpna(?) decyzja dok¹d chcia³oby siê iœæ. (Darujmy
	 * sobie na razie te œcie¿ki).
	 * 
	 * Nazwa³em to bardziej po ludzku, a mniej komputerowo (w por. do
	 * createMoveOptions). ^_^ Ofc. jeœli Ci siê nie podoba, to zmieñ,
	 * Alt+Shift+R po najechaniu na nazwê, szalenie wygodne.
	 */
	private void lookAround() {
	}

	private double angleVelocity = 0.0;
	double velocity = 0.0;

	/**
	 * Ruszanie na podstawie podjêtej decyzji.
	 * 
	 * Na razie kompletny random (test rysowania), chodz¹ jak pijani trochê. ^.-
	 * 
	 * @param dt
	 */
	private void move(double dt) {
		// change velocities
		// randomowe zmiany prêdkoœci do +-0.1 [m/s^2]
		velocity += .1 / 1000.0 * (rng.nextDouble() * 2 - 1.0);
		// randomowe zmiany prêdkoœci k¹towej do +-5.0 [deg/s]
		angleVelocity += 5.0 / 1000.0 * (rng.nextDouble() * 2 - 1.0);

		// rotate
		phi += angleVelocity * dt;

		// move
		position.x += velocity * dt * Math.cos(Math.toRadians(phi));
		position.y += velocity * dt * Math.sin(Math.toRadians(phi));
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
	/*
	 * HashMap<Direction, Double> createMoveOptions() { HashMap<Direction,
	 * Double> move_options = new HashMap<Direction, Double>();
	 * 
	 * for (Map.Entry<Direction, Neighborhood> entry : neighborhood.entrySet())
	 * { Cell first = entry.getValue().getFirstCell(); if (first != null &&
	 * !first.isOccupied()) move_options.put(entry.getKey(), 0.0); }
	 * 
	 * for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
	 * Direction key = entry.getKey(); Double attractivness = 0.0; attractivness
	 * += THREAT_COEFF computeAttractivnessComponentByThreat(key);
	 * move_options.put(key, attractivness); }
	 * 
	 * // prowizorka for (Map.Entry<Direction, Double> entry :
	 * move_options.entrySet()) { Direction key = entry.getKey(); double val =
	 * entry.getValue(); switch (orientation) { case NORTH: if (key ==
	 * Neighborhood.Direction.LEFT) val += RIGHT_EXIT_COEFF; break; case SOUTH:
	 * if (key == Neighborhood.Direction.RIGHT) val += RIGHT_EXIT_COEFF; break;
	 * case EAST: if (key == Neighborhood.Direction.BOTTOM) val +=
	 * RIGHT_EXIT_COEFF; break; case WEST: if (key ==
	 * Neighborhood.Direction.TOP) val += RIGHT_EXIT_COEFF; break; }
	 * move_options.put(key, val); }
	 * 
	 * return move_options; }
	 */

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
	/*
	 * private void move(HashMap<Direction, Double> move_options) { Direction
	 * dir = null; Double top_attractivness = null;
	 * 
	 * for (Map.Entry<Direction, Double> entry : move_options.entrySet()) {
	 * Double curr_attractivness = entry.getValue(); if (top_attractivness ==
	 * null || curr_attractivness > top_attractivness) { top_attractivness =
	 * curr_attractivness; dir = entry.getKey(); } }
	 * 
	 * if (top_attractivness > -THREAT_COEFF * position.getTemperature()) {
	 * rotate(dir); setPosition(neighborhood.get(dir).getFirstCell());
	 * neighborhood = board.getNeighborhoods(this); } }
	 */

	/**
	 * Oblicza chec wyboru danego kierunku, biorac pod uwage zarowno chec ruchu
	 * w dana strone, jak i chec ucieczki od zrodla zagrozenia.
	 * 
	 * @param dir
	 *            potencjalnie obrany kierunek
	 * @return wspolczynnik atrakcyjnosci dla zadanego kierunku, im wyzszy tym
	 *         LEPIEJ
	 */
	/*
	 * private double computeAttractivnessComponentByThreat(Direction dir) {
	 * double attractivness_comp = 0.0; attractivness_comp -= THREAT_COMP_AHEAD
	 * neighborhood.get(dir).getTemperature(); attractivness_comp +=
	 * THREAT_COMP_BEHIND neighborhood.get(Direction.getOppositeDir(dir))
	 * .getTemperature();
	 * 
	 * return attractivness_comp; TODO: Rozwin¹æ. }
	 */

	// private void computeAttractivnessComponentByExit() {
	// sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne)
	// }

	// private void computeAttractivnessComponentBySocialDistances() {
	// sk³adowa potencja³u od Social Distances
	// }

	// private void updateMotorSkills() {
	// ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	// }

}
