package agent;

import board.Board;
import board.Board.Barrier;
import board.Board.Exit;
import board.Board.NoPhysicsDataException;
import board.Board.Obstacle;
import board.Board.Physics;
import board.Point;

public final class Agent {

	enum Stance {
		STAND, CROUCH, CRAWL
	}

	/** Szerokoœæ elipsy Agenta w [m]. */
	public static final double BROADNESS = 0.33;

	/** Ten drugi wymiar (gruboœæ?) elipsy Agenta w [m]. */
	public static final double THICKNESS = 0.2;

	/**
	 * D³ugoœæ wektora orientacji Agenta w [m]. Nic nie robi, tylko do
	 * rysowania.
	 */
	public static final double ORIENTATION_VECTOR = 1.0;

	/** Kat miedzy promieniami wyznaczajacymi wycinek kola bedacy sasiedztwem */
	private static final double CIRCLE_SECTOR = 45; // 360/8

	/**
	 * Wartosc podstawy w f. wykladniczej wykorzystywana do obliczania promienia
	 * sasiedztwa za pomoca kata
	 */
	private static final double BASE_RADIUS_CALC = 1.2;

	/**
	 * Wartosc podstawy w f. wykladniczej wykorzystywana do obliczania
	 * wspolczynnika atrakcyjnosci danego kierunku. Wspolczynniki dla kierunkow
	 * o mniejszych katach, czyli takich, ktore pozwola mniej wiecej zachowac
	 * kierunek ucieczki, maja odpowiednio wieksza wartosc.
	 */
	private static final double BASE_ATTR_CALC = 1.01;

	/**
	 * Wspó³czynnik do skalowania funkcji wyk³adniczej wykorzystywanej do
	 * obliczania promienia s¹siedztwa
	 */
	private static final double POW_RADIUS_COEFF = 2;

	/**
	 * Wspó³czynnik do skalowania funkcji wyk³adniczej wykorzystywanej do
	 * obliczania wspolczynnika atrakcyjnosci.
	 */
	private static final double POW_ATTR_COEFF = 1;

	/** Wspolczynnik wagowy obliczonego zagro¿enia */
	private static final double THREAT_COEFF = 10;

	/**
	 * Minimalna wartoœæ wspó³czynnika zagro¿enia powoduj¹ca zmianê kierunku.
	 * Agent zawsze kierujê siê w stronê wyjœcia, chyba ¿e czynniki œrodowiskowe
	 * mu na to nie pozwalaj¹. Z regu³y bêdzie to wartoœæ ujemna.
	 */
	private static final double MIN_THREAT_VAL = THREAT_COEFF * 50;

	/**
	 * Odleglosc od wyjscia, dla ktorej agent przestaje zwracac uwage na
	 * czynniki zewnetrzne i rzuca sie do drzwi/portalu
	 */
	private static final double EXIT_RUSH_DIST = 3;

	/** Wspolczynnik wagowy odleg³oœci od wyjœcia */
	// private static final double EXIT_COEFF = 5;

	/** Wspolczynnik wagowy dla czynników spo³ecznych */
	// private static final double SOCIAL_COEFF = 0.01;

	/** Minimalna temp. przy której agent widzi ogieñ */
	private static final double MIN_FLAME_TEMP = 100;

	/** Smiertelna wartosc temp. na wysokosci 1,5m */
	private static final double LETHAL_TEMP = 80;

	/** Stezenie CO w powietrzu powodujace natychmiastowy zgon [ppm] */
	private static final double LETHAL_CO_CONCN = 30000.0;

	/** Stezenie karboksyhemoglobiny we krwi powodujace natychmiastowy zgon [%] */
	private static final double LETHAL_HbCO_CONCN = 75.0;

	/** Prêdkoœæ z jak¹ usuwane s¹ karboksyhemoglobiny z organizmu */
	private static final double CLEANSING_VELOCITY = 0.08;
	
	/** Wspó³czynnik funkcji przekszta³caj¹cej odleg³oœæ na czas reakcji*/
	private static final double REACTION_COEFF = 0.3;

	/** Pozycja Agenta na planszy w rzeczywistych [m]. */
	Point position;

	/** Aktualnie wybrane wyjœcie ewakuacyjne */
	Exit exit;

	/** Referencja do planszy. */
	Board board;
	
	/**
	 * Orientacja: k¹t miêdzy wektorem "wzroku" i osi¹ OX w [deg]. Kiedy wynosi
	 * 0.0 deg, to Agent "patrzy" jak oœ OX (jak na geometrii analitycznej).
	 * Wtedy te¿ sin() i cos() dzia³aj¹ ~intuicyjne, tak samo jak analityczne
	 * wzory. :] -- m.
	 */
	double phi;

	/** Flaga informuj¹ca o statusie jednostki - zywa lub martwa */
	private boolean alive;

	/** Flaga mówi¹ca o tym, czy Agentowi uda³o siê ju¿ uciec. */
	boolean exited;
	
	/**Czas, który up³ynie, nim agent podejmie decyzje o ruchu*/
	private double pre_movement_t;

	/** Aktualne stezenie karboksyhemoglobiny we krwii */
	private double hbco;

	/** Czas ruchu agenta */
	double dt; // TODO: do boarda

	/** 'Modul' ruchu agenta */
	private Motion motion;
	
	/** Charakterystyka psychiki agenta*/
	private Psyche psyche;

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
		motion = new Motion(this);
		psyche = new Psyche(this);

		phi = Math.random() * 360 - 180;

		alive = true;
		exited = false;
		hbco = 0;
		dt = 0;
		
		pre_movement_t = REACTION_COEFF * position.evalDist(board.getFireSrc()) + psyche.reaction_t;

		// TODO: Tworzenie cech osobniczych.
	}

	/**
	 * Akcje agenta w danej iteracji.
	 * 
	 * 1. Sprawdza, czy agent zyje - jesli nie, to wychodzi z funkcji.
	 * 
	 * 2. Sprawdza, czy agent nie powinien zginac w tej turze.
	 * 
	 * 3. Wybiera wyjœcie.
	 * 
	 * 4. Aktualizuje liste checkpointow.
	 * 
	 * 5. Na podstawie danych otrzymanych w poprzednim punkcie podejmuje decyzje
	 * i wykonuje ruch
	 * 
	 * @param dt
	 *            Czas w [ms] jaki up³yn¹³ od ostatniego update()'u. Mo¿na
	 *            wykorzystaæ go do policzenia przesuniêcia w tej iteracji z
	 *            zadan¹ wartoœci¹ prêdkoœci:
	 *            {@code dx = dt * v * cos(phi); dy = dt * v * sin(phi);}
	 * @throws NoPhysicsDataException
	 */
	public void update(double _dt) throws NoPhysicsDataException {
		if (!alive || exited)
			return;

		this.dt = _dt;
		checkIfIWillLive();

		if (alive) { // ten sam koszt, a czytelniej, przemieni³em -- m.
			chooseExit();
			motion.updateCheckpoints();
			makeDecision();
			motion.move();
		}

		// jak wyszliœmy poza planszê, to wyszliœmy z tunelu? exited = true
		// spowoduje zaprzestanie wyœwietlania agenta i podbicie statystyk
		// uratowanych w ka¿dym razie :]
		// TODO: zmieniaæ na true dopiero gdy doszliœmy do wyjœcia
		exited = (position.x < 0 || position.y < 0
				|| position.x > board.getDimension().x || position.y > board
				.getDimension().y);
	}

	/**
	 * 
	 * @return aktualna pozycja
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * 
	 * @return obrot wzg OX
	 */
	public double getOrientation() {
		return phi;
	}

	/**
	 * 
	 * @return stan zdrowia
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Okresla, czy agent przezyje, sprawdzajac temperature otoczenia i stezenie
	 * toksyn we krwii
	 */
	private void checkIfIWillLive() {
		evaluateHbCO();

		if (hbco > LETHAL_HbCO_CONCN
				|| getMeanPhysics(0, 360, BROADNESS, Physics.TEMPERATURE) > LETHAL_TEMP)
			alive = false;
	}

	/**
	 * Funkcja oblicza aktualne stezenie karboksyhemoglobiny, uwzgledniajac
	 * zdolnosci organizmu do usuwania toksyn
	 */
	private void evaluateHbCO() {
		// TODO: Dobrac odpowiednie parametry
		if (hbco > dt * CLEANSING_VELOCITY)
			hbco -= dt * CLEANSING_VELOCITY;

		try {
			// TODO: Zastanowiæ siê, czy to faktycznie jest funkcja liniowa.
			hbco += dt
					* LETHAL_HbCO_CONCN
					* (board.getPhysics(position, Physics.CO) / LETHAL_CO_CONCN);
		} catch (NoPhysicsDataException e) {
			// TODO: Mo¿e po prostu nic nie rób z hbco, jeœli nie mamy danych o
			// tlenku wêgla (II)? KASIU?!...
		}
	}

	/**
	 * Podejmuje decyzje, co do kierunku ruchu lub ustala nowy checkpoint.
	 */
	private void makeDecision() {
		phi = calculateNewPhi();
		double attractivness_ahead = THREAT_COEFF * computeThreatComponent(0);
		Barrier barrier = motion.isCollision(0);

		if (distToExit(exit) > EXIT_RUSH_DIST
				&& attractivness_ahead > MIN_THREAT_VAL && barrier == null) {

			double attractivness = Double.POSITIVE_INFINITY;
			for (double angle = -180; angle < 180; angle += CIRCLE_SECTOR) {
				if (angle == 0)
					continue;

				double attr_coeff = 1 / computeMagnitudeByAngle(POW_ATTR_COEFF,
						BASE_ATTR_CALC, angle);
				double curr_attractivness = THREAT_COEFF * attr_coeff
						* computeThreatComponent(angle);

				if (curr_attractivness < attractivness
						&& motion.isCollision(angle) == null) {

					attractivness = curr_attractivness;
					phi += angle;
				}
			}
		}

		if (barrier instanceof Obstacle)
			motion.addCheckpoint(motion.avoidCollision((Obstacle) barrier));
	}

	/**
	 * Metoda obliczaj¹ca k¹t, który agent musi obraæ, by skierowaæ siê do
	 * wybranego checkpoint. K¹t jest wyznaczony przez oœ X i odcinek ³¹cz¹cy
	 * najblizszy checkpoint z aktualn¹ pozycj¹ agenta. Korzysta z funkcji
	 * atan2(), która w przeciwieñstwie do atan() uwzglêdnia orientacjê na
	 * p³aszczyŸnie.
	 * 
	 * @return k¹t zawart w przedziale [-180, 180)
	 */
	private double calculateNewPhi() {
		if (motion.checkpoints.isEmpty()) // TODO: chyba tak ma byæ, nie by³o
											// tego sprawdzenia i wywala³o
											// ArrayIndexOutOfBoundsException --
											// m.
			return phi;

		Point checkpoint = motion.checkpoints
				.get(motion.checkpoints.size() - 1);
		double deltaY = checkpoint.y - position.y;
		double deltaX = checkpoint.x - position.x;

		double angle = Math.atan2(deltaY, deltaX);
		if (angle < -Math.PI) // TODO: to chyba mozna usunac
			angle = (angle % Math.PI) + Math.PI;

		return Math.toDegrees(angle);
	}

	/**
	 * Wybór jednego z dwóch najbli¿szych wyjœæ w zale¿noœci od odleg³oœci i
	 * mo¿liwoœci przejœcia
	 * 
	 * @throws NoPhysicsDataException
	 */
	private void chooseExit() throws NoPhysicsDataException {
		Exit chosen_exit1 = getNearestExit(-1);
		Exit chosen_exit2 = getNearestExit(distToExit(chosen_exit1));

		//TODO: doda³em jeszcze check na null, wywala³o NullPointerException
		if ((chosen_exit1 != null && checkForBlockage(chosen_exit1) > 0)
				&& chosen_exit2 != null)
			exit = chosen_exit2;
		else
			exit = chosen_exit1;

	}

	/**
	 * Bierze pod uwage odleg³oœci na tylko jednej osi. Szuka najbli¿szego
	 * wyjœcia w odleg³oœci nie mniejszej ni¿ dist. Pozwala to na szukanie wyjœæ
	 * bêd¹cych alternatywami. Dla min_dist mniejszego od 0 szuka po prostu
	 * najbli¿szego wyjœcia
	 * 
	 * @param min_dist
	 *            zadana minimalna odleg³oœæ
	 * @return najbli¿sze wyjœcie spe³niaj¹ce warunki
	 */
	// TODO: priv
	public Exit getNearestExit(double min_dist) {
		double shortest_dist = board.getDimension().x + board.getDimension().y;
		Exit nearest_exit = null;

		for (Exit e : board.getExits()) {
			double dist = Math.abs(distToExit(e));
			if (dist < shortest_dist && dist > min_dist) {
				shortest_dist = dist;
				nearest_exit = e;
			}
		}
		return nearest_exit;
	}

	/**
	 * Algorytm dzia³a, poruszaj¹c sie po dwóch osiach: Y - zawsze, X - jeœli
	 * znajdzie blokadê. Zaczyna od wspolrzêdnej Y agenta i porszuamy siê po tej
	 * osi w stronê potencjalnego wyjœcia. Jeœli natrafi na przeszkodê, to
	 * sprawdza, czy ca³a szerokoœæ tunelu dla tej wartoœci Y jest zablokowana.
	 * Porszuaj¹c siê po osi X o szerokoœæ agenta, sprawdza, czy na ca³ym
	 * odcinku o d³. równej szerokoœci tunelu znajduj¹ siê blokady. Jeœli
	 * znajdzie siê choæ jeden przesmyk - przejœcie istnieje -> sprawdzamy
	 * kolejne punkty na osi Y. Jeœli nie istnieje, metoda zwraca wspolrzedna Y
	 * blokady.
	 * 
	 * TODO: W bardziej rzeczywistym modelu agent wybierze kierunek przeciwny do
	 * Ÿród³a ognia.
	 * 
	 * @param _exit
	 *            wyjœcie, w kierunku którego agent chce uciekaæ
	 * @return -1 jeœli drgoa do wyjœcia _exit nie jest zablokowana wspolrzedna
	 *         y blokady, jesli nie ma przejscia
	 * @throws NoPhysicsDataException
	 */
	// TODO: rework, uwaga na (....XXX__XX...)
	private double checkForBlockage(Exit _exit) {
		boolean viable_route = true;
		double exit_y = _exit.getExitY();
		double dist = Math.abs(position.y - exit_y);
		double ds = board.getDataCellDimension();

		if (position.y > exit_y)
			ds = -ds;

		// poruszamy siê po osi Y w kierunku wyjœcia
		double y_coord = position.y + ds;
		while (Math.abs(y_coord - position.y) < dist) {
			double x_coord = 0 + BROADNESS;
			double checkpoint_y_temp = 0;
			try {
				checkpoint_y_temp = board.getPhysics(
						new Point(x_coord, y_coord), Physics.TEMPERATURE);
			} catch (NoPhysicsDataException ex) {
				// nic sie nie dzieje
			}

			// poruszamy siê po osi X, jeœli natrafiliœmy na blokadê
			if (checkpoint_y_temp > MIN_FLAME_TEMP) {
				viable_route = false;
				while (x_coord < board.getDimension().x) {
					double checkpoint_x_temp = MIN_FLAME_TEMP;
					try {
						checkpoint_x_temp = board.getPhysics(new Point(x_coord,
								y_coord), Physics.TEMPERATURE);
					} catch (NoPhysicsDataException ex) {
						// nic sie nie dzieje
					}

					if (checkpoint_x_temp < MIN_FLAME_TEMP)
						viable_route = true;

					x_coord += BROADNESS;
				}
			}
			// jeœli nie ma przejœcia zwracamy wsp. Y blokady
			if (!viable_route)
				return y_coord;

			y_coord += ds;
		}
		return -1;
	}

	/**
	 * Oblicza odleglosc miedzy aktualna pozycja a wyjsciem
	 * 
	 * @param _exit
	 *            wybrane wyjscie
	 * @return odleglosc
	 */
	private double distToExit(Exit _exit) {
		if (_exit == null) // TODO: logiczne? -- m. :] (Wywala³o mi
							// NullPointerException, nie wiem ocb!)
			return Double.POSITIVE_INFINITY;
		return position.evalDist(_exit.getCentrePoint());
	}

	/**
	 * Zwraca œredni¹ wartoœæ parametru fizycznego na wybranej powierzchni --
	 * wycinka ko³a o œrodku w œrodku danego Agenta.
	 * 
	 * Koncept: 1) jedziemy ze sta³ym {@code dalpha} po ca³ym {@code alpha}; 2)
	 * dla ka¿dego z tych k¹tów jedziemy ze sta³ym {@code dr} po {@code r}. 3)
	 * Bierzemy wartoœæ parametru w punkcie okreœlonym przez {@code dalpha} i
	 * {@code dr}, dodajemy do sumy, a na koñcu 4) zwracamy sumê podzielon¹
	 * przez liczbê wybranych w ten sposób punktów.
	 * 
	 * Taki sposób ma 2 zalety: 1) jest ultraprosty, 2) punkty bli¿ej pozycji
	 * Agenta s¹ gêœciej rozmieszczone na wycinku, dlatego wiêksze znaczenie ma
	 * temperatura przy nim. ^_^ (Jeszcze kwestia dobrego dobrania
	 * {@code dalpha} i {@code dr}).
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
	 * 
	 *            Nic nie stoi na przeszkodzie, ¿eby wywo³aæ tê funkcjê z
	 *            {@code alpha == 0.0} i zdj¹æ œredni¹ tylko z linii.
	 * 
	 *            Mo¿na tak¿e przyj¹æ {@code alpha == 360.0} i policzyæ œredni¹
	 *            z ca³ego otoczenia, np. do wyznaczenia warunków œmierci
	 *            (zamiast punktowo, tylko na pozycji Agenta). ^_^
	 * @param r
	 *            Promieñ ko³a, na powierzchni wycinka którego obliczamy
	 *            œredni¹. (Ale konstrukt jêzykowy ;b).
	 * @param what
	 *            O któr¹ wielkoœæ fizyczn¹ nam chodzi.
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
		// dlatego jest potrzebna konstrukcja do-while, ¿eby to wykona³o siê
		// przynajmniej raz (nie jestem pewien czy przy k¹cie zerowym by
		// zadzia³a³o z u¿yciem for-a -- b³êdy numeryczne: nie mo¿na porównywaæ
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
					// nie ma danych tego typu w tym punkcie -- nie uwzglêniaj
					// go do œredniej
				}
				r += dr;
			} while (r <= rB);
			alpha += dalpha;
		} while (alpha <= alphaB);

		return sum / num;
	}

	/**
	 * Oblicza wspolczynnik zagrozenia dla danego kierunku.
	 * 
	 * @param angle
	 *            potencjalnie obrany kierunek
	 * @return wspolczynnik atrakcyjnosci dla zadanego kierunku, im wyzszy tym
	 *         GORZEJ
	 */
	private double computeThreatComponent(double angle) {
		double attractivness_comp = 0.0;
		double r_ahead = computeMagnitudeByAngle(POW_RADIUS_COEFF,
				BASE_RADIUS_CALC, angle);

		attractivness_comp += getMeanPhysics(angle, CIRCLE_SECTOR, r_ahead, // TODO:
																			// -=
				Physics.TEMPERATURE);
		return attractivness_comp;
	}

	/**
	 * Dzieki tej funkcji mozemy latwo otrzymac odpowiednia dlugosc promienia
	 * sasiedztwa, zaleznie od tego, pod jakim katem jest ono obrocone.
	 * 
	 * @param base
	 *            podstawa potegowania, ma duzy wplyw na zroznicowanie dlugosci
	 *            promienia, jako ze zmienia sie ona wykladniczo
	 * @param angle
	 * @return dlugosc promienia
	 */
	// TODO: Dobrac odpowiednie wspolczynniki
	private double computeMagnitudeByAngle(double pow_coeff, double base,
			double angle) {
		return pow_coeff
				* Math.pow(base, (180 - Math.abs(angle)) / CIRCLE_SECTOR);
	}

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
