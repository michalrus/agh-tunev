package edu.agh.tunev.model.kkm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.agh.tunev.model.kkm.Board.Barrier;
import edu.agh.tunev.model.kkm.Board.Obstacle;


class Motion {
	/** TODO:Bedzie okreslac predkosc */
	enum Stance {
		ERECT, BENT, CRAWL
	}

	/** Wspolczynnik predkosci dla pozycji zgiêtej */
	private final static double BENT_COEFF = 0.75;

	/** Wspolczynnik predkosci dla czo³gania */
	private final static double CRAWL_COEFF = 0.1;

	/** Gêstoœæ dymu przy której agent musi siê zgi¹æ */
	private final static double SMOKE_BENT_DENSITY = 5200;

	/** Gêstoœæ dymu, przy której agent musi siê czo³gaæ */
	private final static double SMOKE_CRAWL_DENSITY = 20800;

	/** Standardowa, poczatkowa predkosc ruchu */
	private final static double AVG_MOVING_SPEED = 1.6 / 1000;

	/** Aktualna postawa agenta */
	Stance stance;

	/**
	 * Lista punktow, ktore zamierzamy odwiedzic. Wybrane wyjscie jest
	 * checkpointem o indeksie 0
	 */
	List<Point> checkpoints;

	/** Aktualna predkosc */
	double velocity;

	/** Referencja do szefa */
	private Agent agent;

	/** Wsp. predkosci, cecha osobnicza agenta */
	private double velocity_coeff;

	Motion(Agent _agent) {
		this.agent = _agent;
		checkpoints = new ArrayList<Point>();
		velocity_coeff = (Math.random() / 2) + 0.75; // range [0.75, 1.25]
		velocity = velocity_coeff * AVG_MOVING_SPEED;
		stance = Stance.ERECT;
	}

	/** Ruch w danym kierunki z aktualna predkoscia */
	void move() {
		double x = agent.position.x + velocity * agent.dt
				* Math.cos(Math.toRadians(agent.phi));
		double y = agent.position.y + velocity * agent.dt
				* Math.sin(Math.toRadians(agent.phi));

		Point dest = new Point(x, y);

		if (!isDynamicCollision(dest))
			agent.position = dest;
	}

	/**
	 * Dostosowuje predkosc agenta do warunkow srodowiskowych i uwzglednia
	 * poziom jego przerazenia
	 * 
	 * @param smoke_density
	 *            gêstoœæ dymu na aktualnej pozycji
	 * @param anxiety
	 *            poziom przera¿enia
	 */
	void adjustVelocity(double smoke_density, double anxiety) {
		changeStance(smoke_density);
		velocity = velocity_coeff * anxiety * AVG_MOVING_SPEED;

		if (stance == Stance.BENT)
			velocity *= BENT_COEFF;
		else if (stance == Stance.CRAWL)
			velocity *= CRAWL_COEFF;

	}

	/**
	 * Sprawdza, czy w punkcie, do ktorego sie chcemy przemiescic nie ma
	 * przeszkody. Punkt jest wyliczany na podstawie kata i predkosci ruchu.
	 * 
	 * @param angle
	 *            kat wyznaczajacy kierunek ruchu
	 * @return przeszkoda(Obstacle lub Wall) albo null, jesli jest miejsce do
	 *         ruchu
	 */
	Barrier isStaticCollision(double angle) {
		double path_length = velocity * agent.dt + Agent.BROADNESS;
		double alpha = angle + agent.phi;
		double sin = Math.sin(Math.toRadians(alpha));
		double cos = Math.cos(Math.toRadians(alpha));

		Point p = new Point(agent.position.x + path_length * cos,
				agent.position.y + path_length * sin);

		if (agent.board.isOutOfBounds(p))
			return agent.board.new Wall();

		return isObstacleInPos(p);
	}

	/**
	 * Sprawdza czy w danym punkcie znajduje siê przeszkoda.
	 * 
	 * @param p
	 *            punkt
	 * @return referencja do przeszkody
	 */
	Obstacle isObstacleInPos(Point p) {
		for (Obstacle ob : agent.board.getObstacles()) {
			if (ob.isInside(p, 2 * Agent.BROADNESS))
				return ob;
		}

		return null;
	}

	/**
	 * Funkcja oblicza punkt, w ktory agent musi sie udac, by obejsc przeszkode.
	 * Najpierw sprawdza, z której strony Obstacle sie znajduje, a nastepnie
	 * wybiera, w strone ktorego wierzcholka sie poruszyc (wybiera ten, ktory
	 * znajduje sie blizej wyjscia);
	 * 
	 * @param ob
	 *            przeszkoda, ktora agent chce ominac
	 * @return wspolrzedne checkpointa
	 */
	// TODO: Motion
	Point avoidCollision(Obstacle ob) {
		Point start_point = ob.getStartPoint();
		Point end_point = ob.getEndPoint();

		// obliczamy wspolrzedne wierzcholkow przeszkody (za malym zapasem)
		Point left_bot = new Point(start_point.x - Agent.BROADNESS,
				start_point.y - Agent.BROADNESS);
		Point left_top = new Point(start_point.x - Agent.BROADNESS, end_point.y
				+ Agent.BROADNESS);
		Point right_bot = new Point(end_point.x + Agent.BROADNESS,
				start_point.y - Agent.BROADNESS);
		Point right_top = new Point(end_point.x + Agent.BROADNESS, end_point.y
				+ Agent.BROADNESS);

		// obliczamy odleglosci agenta od poszczegolnych bokow przeszkody (z
		// zapasem)
		List<Double> dist_list = new ArrayList<Double>();
		dist_list.add(Math.abs(agent.position.x
				- (start_point.x - 2 * Agent.BROADNESS))); // left
		dist_list.add(Math.abs(agent.position.y
				- (end_point.y + 2 * Agent.BROADNESS))); // top
		dist_list.add(Math.abs(agent.position.x
				- (end_point.x + 2 * Agent.BROADNESS))); // right
		dist_list.add(Math.abs(agent.position.y
				- (start_point.y - 2 * Agent.BROADNESS))); // bottom

		// wybieramy najmniejsza odleglosc
		double min_dist = Collections.min(dist_list);
		Point[] selected_points = new Point[2];

		// wybieramy odpowiednie wierzcholki
		// left
		if (min_dist == dist_list.get(0)) {
			selected_points[0] = left_bot;
			selected_points[1] = left_top;
			// top
		} else if (min_dist == dist_list.get(1)) {
			selected_points[0] = left_top;
			selected_points[1] = right_top;
			// right
		} else if (min_dist == dist_list.get(2)) {
			selected_points[0] = right_bot;
			selected_points[1] = right_top;
			// bottom
		} else if (min_dist == dist_list.get(3)) {
			selected_points[0] = left_bot;
			selected_points[1] = right_bot;
		}

		Point exit_pos = agent.exit.getClosestPoint(agent.position);
		double[] p_dists = new double[2];
		p_dists[0] = exit_pos.evalDist(selected_points[0]);
		p_dists[1] = exit_pos.evalDist(selected_points[1]);

		// wybieramy wierzcholek blizszy wyjsciu
		if (p_dists[0] < p_dists[1])
			return selected_points[0];
		else
			return selected_points[1];
	}

	/**
	 * Dodaje checkpoint do listy. Jesli jego odleglosc od wyjscia jest mniejsza
	 * niz innych, to odpowiednio przycina liste.
	 * 
	 * @param new_checkpoint
	 */
	void addCheckpoint(Point new_checkpoint) {
		trimCheckpoints(new_checkpoint);
		checkpoints.add(new_checkpoint);
	}

	/**
	 * Aktualizuje liste checkpointow, uwzgledniajac aktualna pozycje agenta.
	 * Jesli jest blizej wyjscia niz, ktorys z punktow na liscie, to ow punkt
	 * jest usuwany. Koordynaty wybranego wyjscia(exit) sa zawsze pierwsze na
	 * liscie.
	 */
	void updateCheckpoints() {
		if (agent.exit == null) // TODO:doda³em, bo wywala³o
								// NullPointerException --
								// m.
			return;
		Point exit_pos = agent.exit.getClosestPoint(agent.position);
		if (!checkpoints.isEmpty())
			checkpoints.set(0, exit_pos);
		else
			checkpoints.add(exit_pos);
		trimCheckpoints(agent.position);
	}

	/**
	 * Usuwa te checkpointy z listy, ktore sa dalej od wyjscia niz punkt p
	 * 
	 * @param p
	 */
	private void trimCheckpoints(Point p) {
		if (!checkpoints.isEmpty()) {
			Point exit_pos = agent.exit.getClosestPoint(agent.position);
			double new_dist = exit_pos.evalDist(p);

			int index = 0;
			while (index <= checkpoints.size() - 1
					&& new_dist > exit_pos.evalDist(checkpoints.get(index)))
				++index;

			checkpoints.subList(index, checkpoints.size()).clear();
		}
	}

	/**
	 * Okreœla postawê agenta w zale¿nosci od gêstoœci dymu.
	 * 
	 * @param smoke_density
	 */
	private void changeStance(double smoke_density) {
		if (smoke_density < SMOKE_BENT_DENSITY)
			stance = Stance.ERECT;
		else if (smoke_density >= SMOKE_BENT_DENSITY
				&& smoke_density < SMOKE_CRAWL_DENSITY)
			stance = Stance.BENT;
		else
			stance = Stance.CRAWL;
	}

	/**
	 * Sprawdza, czy w punkcie, do ktorego agent sie chce przemiescic, nie
	 * znajduje siê inny ewakuowany
	 * 
	 * @param dest
	 *            punkt do ktorego agent chce sie przemiescic
	 * @return
	 */
	private boolean isDynamicCollision(Point dest) {
		for (Agent a : agent.board.getAgents()) {
			if (!a.isAlive() || a.isExited() || a.equals(agent))
				continue;
			
			double dest_dist = a.getPosition().evalDist(dest);
			if (dest_dist < Agent.THICKNESS)
				return true;
		}

		return false;
	}
}
