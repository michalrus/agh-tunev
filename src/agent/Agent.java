package agent;

import java.util.Random;

import board.Board;
import board.Board.NoPhysicsDataException;
import board.Board.Physics;
import board.Point;

public final class Agent {

	/**
	 * Orientacja: k�t mi�dzy wektorem "wzroku" i osi� OX w [deg]. Kiedy wynosi
	 * 0.0 deg, to Agent "patrzy" jak o� OX (jak na geometrii analitycznej).
	 * Wtedy te� sin() i cos() dzia�aj� ~intuicyjne, tak samo jak analityczne
	 * wzory. :] -- m.
	 */
	private double phi;

	/** Pozycja Agenta na planszy w rzeczywistych [m]. */
	private Point position;

	/** Szeroko�� elipsy Agenta w [m]. */
	public static final double BROADNESS = 0.33;

	/** Ten drugi wymiar (grubo��?) elipsy Agenta w [m]. */
	public static final double THICKNESS = 0.2;

	/**
	 * D�ugo�� wektora orientacji Agenta w [m]. Nic nie robi, tylko do
	 * rysowania.
	 */
	public static final double ORIENTATION_VECTOR = 1.0;

	/** Flaga m�wi�ca o tym, czy Agentowi uda�o si� ju� ucie�. */
	boolean exited;

	/** Referencja do planszy. */
	private Board board;

	/** Random number generator. */
	private Random rng;

	/** Wspolczynnik wagowy obliczonego zagro�enia */
	// private static final double THREAT_COEFF = 10;

	/** Wspolczynnik wagowy odleg�o�ci od wyj�cia */
	// private static final double EXIT_COEFF = 5;

	/** Wspolczynnik wagowy dla czynnik�w spo�ecznych */
	// private static final double SOCIAL_COEFF = 0.01;

	/** Smiertelna wartosc temp. na wysokosci 1,5m */
	private static final double LETHAL_TEMP = 80;

	/** Stezenie CO w powietrzu powodujace natychmiastowy zgon [ppm] */
	private static final double LETHAL_CO_CONCN = 30000.0;

	/** Stezenie karboksyhemoglobiny we krwi powodujace natychmiastowy zgon [%] */
	private static final double LETHAL_HbCO_CONCN = 75.0;

	/** Pr�dko�� z jak� usuwane s� karboksyhemoglobiny z organizmu */
	private static final double CLEANSING_VELOCITY = 0.08;

	/** Wspolczynnik wagowy dla kierunku przeciwnego do potencjalnego ruchu */
	// private static double THREAT_COMP_BEHIND = 0.5;

	/** Wspolczynnik wagowy dla potencjalnego kierunku ruchu */
	// private static double THREAT_COMP_AHEAD = 1;

	/** Flaga informuj�ca o statusie jednostki - zywa lub martwa */
	private boolean alive;

	/** Aktualne stezenie karboksyhemoglobiny we krwii */
	private double hbco;

	/**
	 * Konstruktor agenta. Inicjuje wszystkie pola niezb�dne do jego egzystencji
	 * na planszy. Pozycja jest z g�ry narzucona z poziomu Board. Orientacja
	 * zostaje wylosowana.
	 * 
	 * @param board
	 *            referencja do planszy
	 * @param position
	 *            referencja to kom�rki b�d�cej pierwotn� pozycj� agenta
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
	 * Zwraca �redni� warto�� parametru fizycznego na wybranej powierzchni --
	 * wycinka ko�a o �rodku w �rodku danego Agenta.
	 * 
	 * Koncept: 1) jedziemy ze sta�ym {@code dalpha} po ca�ym {@code alpha}; 2)
	 * dla ka�dego z tych k�t�w jedziemy ze sta�ym {@code dr} po {@code r}. 3)
	 * Bierzemy warto�� parametru w punkcie okre�lonym przez {@code dalpha} i
	 * {@code dr}, dodajemy do sumy, a na ko�cu 4) zwracamy sum� podzielon�
	 * przez liczb� wybranych w ten spos�b punkt�w.
	 * 
	 * Taki spos�b ma 2 zalety: 1) jest ultraprosty, 2) punkty bli�ej pozycji
	 * Agenta s� g�ciej rozmieszczone na wycinku, dlatego wi�ksze znaczenie ma
	 * temperatura przy nim. ^_^ (Jeszcze kwestia dobrego dobrania
	 * {@code dalpha} i {@code dr}).
	 * 
	 * @param orientation
	 *            K�t mi�dzy wektorem orientacji Agenta a osi� symetrii wycinka
	 *            ko�a. Innymi s�owy, jak chcemy wycinek po lewej r�ce danego
	 *            Agenta, to dajemy tu 90.0 [deg], jak po prawej to -90.0 [deg].
	 *            (Dlatego, �e k�ty w geometrii analitycznej rosn� przeciwnie do
	 *            ruchu wskaz�wek zegara!).
	 * @param alpha
	 *            Rozstaw "ramion" wycinka ko�a w [deg]. Jak chcemy np. 1/8
	 *            ko�a, to dajemy 45.0 [deg], w miar� oczywiste chyba. By� mo�e
	 *            warto zmieni� nazw� tego parametru.
	 * 
	 *            Nic nie stoi na przeszkodzie, �eby wywo�a� t� funkcj� z
	 *            {@code alpha == 0.0} i zdj�� �redni� tylko z linii.
	 * 
	 *            Mo�na tak�e przyj�� {@code alpha == 360.0} i policzy� �redni�
	 *            z ca�ego otoczenia, np. do wyznaczenia warunk�w �mierci
	 *            (zamiast punktowo, tylko na pozycji Agenta). ^_^
	 * @param r
	 *            Promie� ko�a, na powierzchni wycinka kt�rego obliczamy
	 *            �redni�. (Ale konstrukt j�zykowy ;b).
	 * @param what
	 *            O kt�r� wielko�� fizyczn� nam chodzi.
	 * @return
	 */
	private double getMeanPhysics(double orientation, double alpha, double r,
			Physics what) {
		if (alpha < 0)
			throw new IllegalArgumentException("alpha < 0");
		if (r < 0)
			throw new IllegalArgumentException("r < 0");

		double dalpha = 10; // [deg]
		double dr = 0.5; // [m]

		double alphaA = phi + orientation - alpha / 2;
		double alphaB = phi + orientation + alpha / 2;
		double rA = 0;
		double rB = r;

		double sum = 0.0;
		long num = 0;

		alpha = alphaA;
		// dlatego jest porzebna konstrukcja do-while, �eby to wykona�o si�
		// przynajmniej raz (nie jestem pewien czy przy k�cie zerowym by
		// zadzia�a�o z u�yciem for-a -- b��dy numeryczne: nie mo�na por�wnywa�
		// zmiennoprzecinkowych)
		do {
			double sin = Math.sin(Math.toRadians(alpha));
			double cos = Math.cos(Math.toRadians(alpha));
			r = rA;
			do {
				try {
					sum += board.getPhysics(new Point(position.x + cos * r,
							position.y + sin * r), what);
					num++;
				} catch (NoPhysicsDataException e) {
				} catch (IndexOutOfBoundsException e) {
				}
				r += dr;
			} while (r <= rB);
			alpha += dalpha;
		} while (alpha <= alphaB);

		return sum / num;
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
	 *            Czas w [ms] jaki up�yn�� od ostatniego update()'u. Mo�na
	 *            wykorzysta� go do policzenia przesuni�cia w tej iteracji z
	 *            zadan� warto�ci� pr�dko�ci:
	 *            {@code dx = dt * v * cos(phi); dy = dt * v * sin(phi);}
	 */
	public void update(double dt) {
		if (!alive || exited)
			return;

		checkIfIWillLive();

		if (alive) { // ten sam koszt, a czytelniej, przemieni�em -- m.
			lookAround();
			move(dt);
		}

		// jak wyszli�my poza plansz�, to wyszli�my z tunelu? exited = true
		// spowoduje zaprzestanie wy�wietlania agenta i podbicie statystyk
		// uratowanych w ka�dym razie :]
		exited = (position.x < 0 || position.y < 0
				|| position.x > board.getDimension().x || position.y > board
				.getDimension().y);
	}

	/**
	 * Sprawdza czy Agent na swojej planszy aktualnie koliduje z *czymkolwiek*
	 * (innym Agentem, przeszkod�).
	 * 
	 * U�ywanie: najpierw ustawiamy nowe {@link #position} i {@link #phi},
	 * sprawdzamy czy {@link #hasCollision()}, je�li tak, to wracamy do starych.
	 * 
	 * Koncept prawdopodobnie do modyfikacji, na razie tak zapisa�em. -- m.
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

	/** Zwraca kierunek, w kt�rym zwr�cony jest agent */
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
			// nie zmieniaj flagi �ycia, je�li nie mamy danych o temperaturze w
			// aktualnym punkcie przestrzeni i czasu (ale ofc. tylko gdy
			// st�enie CO pozwala prze�y�)
		} catch (IndexOutOfBoundsException e) {
			// pro�ba o dane spoza planszy
		}
	}

	/**
	 * Funkcja oblicza aktualne stezenie karboksyhemoglobiny, uwzgledniajac
	 * zdolnosci organizmu do usuwania toksyn
	 */
	private void evaluateHbCO() {
		// TODO: Trzeba t� pr�dko�� teraz uzale�ni� od dt; je�li to by�o
		// 0.08/500 ms, to jakby ustawi� to w�a�nie na 0.08/500 i zawsze tutaj
		// mno�y� przez dt t� sta�� pr�dko��, b�dzie dzia�a�o tak samo.

		if (hbco > CLEANSING_VELOCITY)
			hbco -= CLEANSING_VELOCITY;

		try {
			// TODO: Zastanowi� si�, czy to faktycznie jest funkcja liniowa.
			hbco += LETHAL_HbCO_CONCN
					* (board.getPhysics(position, Physics.CO) / LETHAL_CO_CONCN);
		} catch (NoPhysicsDataException e) {
			// TODO: Mo�e po prostu nic nie r�b z hbco, je�li nie mamy danych o
			// tlenku w�gla (II)? KASIU?!...
		} catch (IndexOutOfBoundsException e) {
			// pro�ba o dane spoza planszy
		}
	}

	/**
	 * Rozejrzenie si�, wst�pna(?) decyzja dok�d chcia�oby si� i��. (Darujmy
	 * sobie na razie te �cie�ki).
	 * 
	 * Nazwa�em to bardziej po ludzku, a mniej komputerowo (w por. do
	 * createMoveOptions). ^_^ Ofc. je�li Ci si� nie podoba, to zmie�,
	 * Alt+Shift+R po najechaniu na nazw�, szalenie wygodne.
	 */
	private void lookAround() {
		double tempInFrontOfMe5m = getMeanPhysics(0, 120, 5,
				Physics.TEMPERATURE);
		double tempOnMyLeft3m = getMeanPhysics(-90, 120, 3, Physics.TEMPERATURE);
	}

	private double angleVelocity = 0.0;
	double velocity = 0.0;

	/**
	 * Ruszanie na podstawie podj�tej decyzji.
	 * 
	 * Na razie kompletny random (test rysowania), chodz� jak pijani troch�. ^.-
	 * 
	 * @param dt
	 */
	private void move(double dt) {
		// change velocities
		// randomowe zmiany pr�dko�ci do +-0.1 [m/s^2]
		velocity += .1 / 1000.0 * (rng.nextDouble() * 2 - 1.0);
		// randomowe zmiany pr�dko�ci k�towej do +-5.0 [deg/s]
		angleVelocity += 5.0 / 1000.0 * (rng.nextDouble() * 2 - 1.0);

		// rotate
		phi += angleVelocity * dt;

		// move
		position.x += velocity * dt * Math.cos(Math.toRadians(phi));
		position.y += velocity * dt * Math.sin(Math.toRadians(phi));
	}
	/**
	 * Sprawdza jakie s� dost�pne opcje ruchu, a nast�pnie szacuje, na ile sa
	 * atrakcyjne dla agenta Najpierw przeszukuje s�siednie kom�rki w
	 * poszukiwaniu przeszk�d i wybieram tylko te, kt�re s� puste. Nast�pnie
	 * szacuje wsp�czynnik atrakcyjno�ci dla ka�dej z mo�liwych opcji ruchu na
	 * podstawie zagro�enia, odleg�o�ci od wyj�cia, itd.
	 * 
	 * @return HashMapa kierunk�w wraz ze wsp�czynnikami atrakcyjno�ci
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
	 * 2. Sprawdza, czy op�aca jej sie ruch, jesli nie to pomija kolejne
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
	 * return attractivness_comp; TODO: Rozwin��. }
	 */

	// private void computeAttractivnessComponentByExit() {
	// sk�adowa potencja�u od ew. wyj�cia (je�li widoczne)
	// }

	// private void computeAttractivnessComponentBySocialDistances() {
	// sk�adowa potencja�u od Social Distances
	// }

	// private void updateMotorSkills() {
	// ograniczenie zdolno�ci poruszania si� w wyniku zatrucia?
	// }

}
