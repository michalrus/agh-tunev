package agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import board.Board.Barrier;
import board.Board.Obstacle;
import board.Point;

public class Motion {
	/** TODO:Bedzie okreslac predkosc */
	enum Stance {
		STAND, CROUCH, CRAWL
	}

	/** Standardowa, poczatkowa predkosc ruchu */
	private static double AVG_MOVING_SPEED = 1.6 / 1000;

	/** Referencja do szefa */
	private Agent agent;

	/**
	 * Lista punktow, ktore zamierzamy odwiedzic. Wybrane wyjscie jest
	 * checkpointem o indeksie 0
	 */
	List<Point> checkpoints;

	/** Aktualna predkosc */
	double velocity;

	public Motion(Agent _agent) {
		this.agent = _agent;
		checkpoints = new ArrayList<Point>();
		velocity = AVG_MOVING_SPEED;
	}

	/** Ruch w danym kierunki z aktualna predkoscia */
	void move() {
		agent.position.x += velocity * agent.dt
				* Math.cos(Math.toRadians(agent.phi));
		agent.position.y += velocity * agent.dt
				* Math.sin(Math.toRadians(agent.phi));
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
	Barrier isCollision(double angle) {
		double path_length = velocity * agent.dt + Agent.BROADNESS;
		double alpha = angle + agent.phi;
		double sin = Math.sin(Math.toRadians(alpha));
		double cos = Math.cos(Math.toRadians(alpha));

		Point p = new Point(agent.position.x + path_length * cos,
				agent.position.y + path_length * sin);

		if (agent.board.isOutOfBounds(p))
			return agent.board.new Wall();

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

		Point exit_pos = agent.exit.getCentrePoint();
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
		if (agent.exit == null) // doda³em, bo wywala³o NullPointerException --
								// m.
			return;
		Point exit_pos = agent.exit.getCentrePoint();
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
			Point exit_pos = agent.exit.getCentrePoint();
			double new_dist = exit_pos.evalDist(p);

			int index = 0;
			while (index <= checkpoints.size() - 1
					&& new_dist > exit_pos.evalDist(checkpoints.get(index)))
				++index;

			checkpoints.subList(index, checkpoints.size()).clear();
		}
	}

}
