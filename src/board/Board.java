package board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import agent.Agent;

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

	public double getDataCellDimension() {
		return dataCellDimension.y;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public List<Exit> getExits() {
		return exits;
	}

	// TODO: Tymczasowo, zmienie na private, kiedy przeniose parsowanie do
	// konstruktora
	/**
	 * Sortuje wyjœcia w kolejnoœci rosn¹cej, bior¹c pod uwagê wspó³rzêdn¹ Y*
	 * public void sortExits(){ Collections.sort(exits); }
	 */

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	/** sprawdza, czy punkty znajduje siê na planszy */
	public boolean isOutOfBounds(Point p) {
		return p.x < 0 || p.x > getDimension().x || p.y < 0
				|| p.y > getDimension().y;
	}

	public class NoPhysicsDataException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public class NoExitException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	// ------------- internals start here, an Agent should not use those

	private Point dimension;

	// leave these package-private (without access modifier) -- BoardView
	// has to be able to read them
	Point dataCellDimension;

	List<Agent> agents;
	List<Obstacle> obstacles;
	List<Exit> exits;
	List<List<DataCell>> dataCells;

	private static final long MAX_RANDOM_FAILURES = 10;
	private Random rng;

	public Board() {
		agents = new ArrayList<Agent>();
		obstacles = new ArrayList<Obstacle>();
		exits = new ArrayList<Exit>();
		rng = new Random();
	}

	/**
	 * Jedna iteracja symulacji.
	 * 
	 * @param dt
	 *            czas w [ms] który up³yn¹³ od poprzedniej iteracji
	 * @throws NoPhysicsDataException
	 */
	public void update(double dt) throws NoPhysicsDataException {
		for (Agent agent : agents)
			agent.update(dt);
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

	public void addObstacle(Point start, Point end) {
		Point newStart = new Point(Math.min(start.x, end.x), Math.min(start.y,
				end.y));
		Point newEnd = new Point(Math.max(start.x, end.x), Math.max(start.y,
				end.y));

		obstacles.add(new Obstacle(newStart, newEnd));
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

	// TODO: hardcode do testow, nie chcia³o mi siê na razie robiæ parsowania
	public void initAgents(long vehicles_num) {
		for (long i = 0; i < vehicles_num; i += 2) {
			obstacles.add(new Obstacle(new Point(5, 100 - 3 * i - 3),
					new Point(6.5, 100 - 3 * i)));
			agents.add(new Agent(this, new Point(7, 100 - 3 * i - 1)));
			agents.add(new Agent(this, new Point(4.5, 100 - 3 * i - 1)));
			agents.add(new Agent(this, new Point(4.5, 100 - 3 * i - 2)));

			obstacles.add(new Obstacle(new Point(1, 120 + 3 * i), new Point(
					2.5, 120 + 3 * i + 3)));
			agents.add(new Agent(this, new Point(3, 120 + 3 * i + 2)));
			agents.add(new Agent(this, new Point(0.5, 120 + 3 * i + 1)));
			agents.add(new Agent(this, new Point(3, 120 + 3 * i + 1)));
		}
	}

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
		private Point start, end;

		public TwoPointStructure(Point start, Point end) {
			this.start = start;
			this.end = end;
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

		/*
		 * @Override public int compareTo(Exit anotherExit) throws
		 * ClassCastException { if(!(anotherExit instanceof Exit)) throw new
		 * ClassCastException(); return (int) (this.getCentrePoint().y -
		 * anotherExit.getCentrePoint().y); }
		 */
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
