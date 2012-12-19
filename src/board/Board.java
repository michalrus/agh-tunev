package board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import agent.Agent;
import agent.Neighborhood;

public class Board {

	private List<Agent> agents;

	private List<List<Cell>> cells;

	private static final int MAX_RANDOM_FAILURES = 10;
	private Random rng;

	public Board() {
		agents = new ArrayList<Agent>();
		rng = new Random();
	}

	public void initCells(int width, int length) {
		cells = new ArrayList<List<Cell>>();
		for (int x = 0; x < width; x++) {
			List<Cell> row = new ArrayList<Cell>();
			for (int y = 0; y < length; y++)
				row.add(new Cell(x, y));
			cells.add(row);
		}
	}

	public int getWidth() {
		return cells.size();
	}

	public int getLength() {
		return cells.get(0).size();
	}

	public Cell getCellAt(int x, int y) {
		if (x >= 0 && x < cells.size())
			if (y >= 0 && y < cells.get(x).size())
				return cells.get(x).get(y);
		return null;
	}

	public void addObstacle(int x1, int x2, int y1, int y2) {
		for (int x = x1; x < x2; x++)
			for (int y = y1; y < y2; y++) {
				Cell c = getCellAt(x, y);
				if (c != null)
					c.setType(Cell.Type.BLOCKED);
			}
	}

	public void initAgentsRandomly(int num) {
		if (num <= 0)
			return;

		int width = getWidth();
		int length = getLength();
		Set<Cell> usedCells = new HashSet<Cell>();

		for (int i = 0; i < num; i++) {
			int numFails = 0;

			while (numFails < MAX_RANDOM_FAILURES) {
				int x = rng.nextInt(width);
				int y = rng.nextInt(length);

				Cell cell = getCellAt(x, y);

				if (cell.getType() != Cell.Type.BLOCKED
						&& !usedCells.contains(cell)) {
					Agent agent = new Agent(this, cell);
					agents.add(agent);
					usedCells.add(cell);
					break;
				}

				numFails++;
			}
		}
	}

	/**
	 * Jedna iteracja symulacji.
	 */
	public void update() {
		setFdsData();

		for (Agent agent : agents)
			agent.update();
	}

	private void setFdsData() {
		// TODO:
	}

	public Map<Neighborhood.Direction, Neighborhood> getNeighborhoods(
			Agent agent) {
		Map<Neighborhood.Direction, Neighborhood> map = new HashMap<Neighborhood.Direction, Neighborhood>();
		
		for (Neighborhood.Direction dir : Neighborhood.Direction.values()) {
			map.put(dir, new Neighborhood(this, agent, dir));
		}

		return map;
	}

	public List<Agent> getAgents() {
		return agents;
	}

}
