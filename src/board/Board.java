package board;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import agent.Agent;
import agent.Neighborhood;

public class Board {

	private List<Agent> agents;

	private List<List<Cell>> cells;

	private static final int MAX_RANDOM_FAILURES = 10;
	private Random rng;

	public Board(String path) throws FileNotFoundException {
		agents = new ArrayList<Agent>();
		rng = new Random();

		parseFDSInputFile(path);
	}

	/**
	 * Wczytywanie danych z pliku wejœciowego FDS-a.
	 * 
	 * Pamiêtaj: x to szerokoœæ tunelu, y to d³ugoœæ. Podajemy czêsto
	 * wspó³rzêdne x,y,z albo x,x1,y,y1,z,z1.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	private void parseFDSInputFile(String path) throws FileNotFoundException {
		boolean gotDimensions = false;
		Pattern patternDimensions = Pattern
				.compile("^&MESH\\s+IJK=(\\d+),(\\d+),\\d+");
		Pattern patternObstacle = Pattern
				.compile("^&OBST\\s+XB=(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+");
		Matcher matcher;

		String line;
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			while ((line = br.readLine()) != null) {
				// dimensions
				if (!gotDimensions) {
					matcher = patternDimensions.matcher(line);
					if (matcher.find()) {
						int width = Integer.parseInt(matcher.group(1));
						int length = Integer.parseInt(matcher.group(2));
						if (width <= 0 || length <= 0)
							throw new RuntimeException(
									"width <= 0 or length <= 0");
						initCells(width, length);
						gotDimensions = true;
						continue;
					}
				}

				// obstacles
				matcher = patternObstacle.matcher(line);
				if (matcher.find()) {
					if (!gotDimensions)
						throw new RuntimeException("&OBST before &MESH!");
					int x1 = Integer.parseInt(matcher.group(1));
					int x2 = Integer.parseInt(matcher.group(2));
					int y1 = Integer.parseInt(matcher.group(3));
					int y2 = Integer.parseInt(matcher.group(4));
					addObstacle(x1, x2, y1, y2);
					continue;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (!gotDimensions)
				throw new RuntimeException(path
						+ ": no mesh dimensions declared");
		}
	}

	private void initCells(int width, int length) {
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
		if (x < cells.size())
			if (y < cells.get(x).size())
				return cells.get(x).get(y);
		return null;
	}

	private void addObstacle(int x1, int x2, int y1, int y2) {
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
		try {
			Thread.sleep(500); // na razie, bo póki nic tu nie ma do liczenia
		} catch (InterruptedException e) {
		}

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

		return map;
	}

	public List<Agent> getAgents() {
		return agents;
	}

}
