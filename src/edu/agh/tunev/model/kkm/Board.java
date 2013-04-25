package edu.agh.tunev.model.kkm;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Board {

	public enum Physics {
		TEMPERATURE, CO
	}

	/**
	 * Zwraca dane fizyczne zadanego typu. Jak nie ma dla tego punktu takich
	 * danych, rzuca wyj¹tek.
	 * 
	 * @param where
	 * @param what
	 * @return
	 * @throws NoPhysicsDataException
	 */
	public double getPhysics(Point where, Physics what)
			throws NoPhysicsDataException {
		try {
			return getDataCell(where).getPhysics(what);
		} catch (IndexOutOfBoundsException e) {
			// jeœli nie ma ¿adnej komórki na pozycji {@code where}
			throw new NoPhysicsDataException();
		}
	}

	public void setPhysics(Point where, Physics what, double value) {
		getDataCell(where).setPhysics(what, value);
	}

	public Point getDimension() {
		return dimension;
	}

	public Point getDataCellDimension() {
		return dataCellDimension;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public List<Exit> getExits() {
		return exits;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public double getDuration() {
		return data_duration;
	}

	public void setDuration(double _duration) {
		this.data_duration = _duration;
	}

	public void updateData(double simTime) throws FileNotFoundException,
			ParseException {
		// parser, ty nie czytaæ danych w tym miejscu! -- m.
		//parser.readData(simTime);
	}

	/** sprawdza, czy punkty znajduje siê na planszy */
	public boolean isOutOfBounds(Point p) {
		return p.x < 0 || p.x > getDimension().x || p.y < 0
				|| p.y > getDimension().y;
	}

	public Point getNearestFireSrc(Point p) {
		double min = Double.POSITIVE_INFINITY;
		Point nearest_src = null;

		for (Point src : fire_srcs) {
			double dist = src.evalDist(p);
			if (dist < min) {
				min = dist;
				nearest_src = src;
			}
		}

		return nearest_src;
	}

	public void addFireSrc(Point _src) {
		fire_srcs.add(_src);
	}

	public class NoPhysicsDataException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public class NoExitException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/** Referencja do symulacji */
	//private Object sim;
	// commented-out (unused), see below (wyszukaj "sim.")

	/** Parser dla danej planszy */
	// parser, ty nie czytaæ danych w tym miejscu! -- m.
	//private FDSParser parser;

	// ------------- internals start here, an Agent should not use those
	private Point dimension;

	/** Œrodkowy punkt Ÿród³a ognia */
	private List<Point> fire_srcs;

	/** Czas dla jakiego mamy okreœlone dane dla planszy */
	private double data_duration;

	// leave these package-private (without access modifier) -- BoardView
	// has to be able to read them
	Point dataCellDimension;

	List<Agent> agents;
	List<Obstacle> obstacles;
	List<Exit> exits;
	List<List<DataCell>> dataCells;

	private static final long MAX_RANDOM_FAILURES = 10;
	private Random rng;

	public Board(String dataFolder, Object _sim) throws FileNotFoundException,
			ParseException {
		agents = new ArrayList<Agent>();
		obstacles = new ArrayList<Obstacle>();
		exits = new ArrayList<Exit>();
		fire_srcs = new ArrayList<Point>();
		rng = new Random();
		// parser, ty nie czytaæ danych w tym miejscu! -- m.
		//parser = new FDSParser(this, dataFolder);
		//this.sim = _sim;
		// commented-out (unused), see below (wyszukaj "sim.")
	}

	/**
	 * Jedna iteracja symulacji. Agent uaktualnia swoj stan, tylko jesli zyje,
	 * jest na planszy i uplynal juz jego pre movement time
	 * 
	 * @param dt
	 *            czas w [ms] który up³yn¹³ od poprzedniej iteracji
	 * @throws NoPhysicsDataException
	 */
	public void update(double dt) throws NoPhysicsDataException {
		for (Agent agent : agents) {
			if (agent.isAlive() && !agent.isExited()
			// && sim.getSimTime() > agent.getPreMoveTime())
					&& Double.NaN > agent.getPreMoveTime())
				// sim.Simulation juz nie istnieje, mam nadzieje, ze ogarniesz
				// -- m. =)~
				agent.update(dt);
		}
	}

	/**
	 * Ustawia geometriê planszy.
	 * 
	 * @param dimension
	 *            Rozmiar planszy (najdalszy punkt od jej pocz¹tku).
	 * @param numCellsX
	 *            Rozdzielczoœæ w OX (na ile odcinków dzielimy OX).
	 * @param numCellsY
	 *            Rozdzielczoœæ w OY.
	 */
	public void setGeometry(Point dimension, long numCellsX, long numCellsY) {
		this.dimension = dimension;
		this.dataCellDimension = new Point(dimension.x / numCellsX, dimension.y
				/ numCellsY);

		dataCells = new ArrayList<List<DataCell>>();
		for (long y = 0; y < numCellsY; y++) {
			List<DataCell> row = new ArrayList<DataCell>();
			for (long x = 0; x < numCellsX; x++)
				row.add(new DataCell());
			dataCells.add(row);
		}
	}

	public void addObstacle(Obstacle ob) {
		obstacles.add(ob);
	}

	public void addExit(Point start, Point end) {
		exits.add(new Exit(start, end));
	}

	public void initAgentsRandomly(long num) {
		for (long i = 0; i < num; i++) {
			long numFails = 0;

			while (numFails++ < MAX_RANDOM_FAILURES) {
				Point position = new Point(rng.nextDouble() * dimension.x,
						rng.nextDouble() * dimension.y);

				Agent agent = new Agent(this, position);
				if (/* !agent.isCollision(0) */true) { // TODO: to tak nie moze
														// wygladac
					agents.add(agent);
					break;
				}
			}
		}
	}

	public void initAgents() {
		for (Obstacle ob : obstacles) {
			Point start = ob.getStartPoint();
			Point end = ob.getEndPoint();
			double veh_len = end.y - start.y;
			int passengers = rng.nextInt(3) + 1;

			for (int i = 0; i < passengers; ++i) {
				Point coord = (i % 2 == 0) ? new Point(start.x - 2
						* Agent.BROADNESS, start.y + (i / 2) * (veh_len / 2)
						+ 2 * Agent.BROADNESS) : new Point(end.x + 2
						* Agent.BROADNESS, start.y + (i / 2) * (veh_len / 2)
						+ 2 * Agent.BROADNESS);
				agents.add(new Agent(this, coord));
			}
		}
	}

	// TODO: hardcode do testow, nie chcia³o mi siê na razie robiæ parsowania
	/*
	 * public void initAgents(long vehicles_num) { for (long i = 0; i <
	 * vehicles_num; i += 2) { obstacles.add(new Obstacle(new Point(5, 100 - 3 *
	 * i - 3), new Point(6.5, 100 - 3 * i))); agents.add(new Agent(this, new
	 * Point(7, 100 - 3 * i - 1))); agents.add(new Agent(this, new Point(4.5,
	 * 100 - 3 * i - 1))); agents.add(new Agent(this, new Point(4.5, 100 - 3 * i
	 * - 2)));
	 * 
	 * obstacles.add(new Obstacle(new Point(1, 120 + 3 * i), new Point( 2.5, 120
	 * + 3 * i + 3))); agents.add(new Agent(this, new Point(3, 120 + 3 * i +
	 * 2))); agents.add(new Agent(this, new Point(0.5, 120 + 3 * i + 1)));
	 * agents.add(new Agent(this, new Point(3, 120 + 3 * i + 1))); } }
	 */

	DataCell getDataCell(Point where) {
		if (where.x < 0 || where.y < 0 || where.x > dimension.x
				|| where.y > dimension.y)
			throw new IndexOutOfBoundsException();

		long x = Math.round(Math.floor(where.x / dataCellDimension.x));
		long y = Math.round(Math.floor(where.y / dataCellDimension.y));

		// correct a rare situation when:
		//
		// a) where.y == dimensions.y and/or
		// b) where.x == dimensions.x

		if (y > dataCells.size() - 1)
			y = dataCells.size() - 1;
		if (x > dataCells.get(0).size() - 1)
			x = dataCells.get(0).size() - 1;

		return dataCells.get((int) y).get((int) x);
	}

	final class DataCell {
		private Map<Physics, Double> physics;

		public DataCell() {
			physics = new HashMap<Physics, Double>();
		}

		public double getPhysics(Physics what) throws NoPhysicsDataException {
			Double value = physics.get(what);
			if (value == null)
				throw new NoPhysicsDataException();
			return value;
		}

		public void setPhysics(Physics what, double value) {
			physics.put(what, value);
		}
	}

	public class TwoPointStructure {
		protected Point start, end;

		public TwoPointStructure(Point _start, Point _end) {
			this.start = new Point(Math.min(_start.x, _end.x), Math.min(
					_start.y, _end.y));
			this.end = new Point(Math.max(_start.x, _end.x), Math.max(_start.y,
					_end.y));
		}

		public Point getStartPoint() {
			return start;
		}

		public Point getEndPoint() {
			return end;
		}

		public Point getCentrePoint() {
			return new Point((start.x + end.x) / 2, (start.y + end.y) / 2);
		}

	}

	public final class Exit extends TwoPointStructure /*
													 * implements
													 * Comparable<Exit>
													 */{
		public Exit(Point start, Point end) {
			super(start, end);
		}

		public double getExitY() {
			return getCentrePoint().y;
		}

		public double getExitX() {
			return getCentrePoint().x;
		}

		/**
		 * Znajduje punkt le¿¹cy na odcinku reprezentuj¹cym wyjœcie, bêd¹cy w
		 * najmniejszej odleg³oœci do zadanego punktu
		 * 
		 * @param p
		 *            zadany punkt
		 * @return najbli¿ej le¿¹cy punkt
		 */
		public Point getClosestPoint(Point p) {
			Point closestPoint;

			double delta_x = end.x - start.x;
			double delta_y = end.y - start.y;

			if ((delta_x == 0) && (delta_y == 0)) {
				// throw sth
			}

			double u = ((p.x - start.x) * delta_x + (p.y - start.y) * delta_y)
					/ (delta_x * delta_x + delta_y * delta_y);

			if (u < 0) {
				closestPoint = new Point(start.x, start.y);
			} else if (u > 1) {
				closestPoint = new Point(end.x, end.y);
			} else {
				closestPoint = new Point(
						(int) Math.round(start.x + u * delta_x),
						(int) Math.round(start.y + u * delta_y));
			}

			return closestPoint;
		}
	}

	public final class Obstacle extends TwoPointStructure implements Barrier {
		public Obstacle(Point start, Point end) {
			super(start, end);
		}

		public boolean isInside(Point p, double reserve) {
			return p.x > getStartPoint().x - reserve
					&& p.x < getEndPoint().x + reserve
					&& p.y > getStartPoint().y - reserve
					&& p.y < getEndPoint().y + reserve;
		}
	}

	public final class Wall extends TwoPointStructure implements Barrier {
		public Wall(Point start, Point end) {
			super(start, end);
		}

		public Wall() { // TODO: tymczasowo dla wygody
			super(new Point(0, 0), new Point(0, 0));
		}
	}

	public interface Barrier {

	}
}
