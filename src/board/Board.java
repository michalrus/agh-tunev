package board;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import agent.Agent;
import agent.Neighborhood;

public class Board {

	private int width, length;

	private ArrayList<Agent> agents;

	public Board(String path) throws FileNotFoundException {
		agents = new ArrayList<Agent>();

		parseFDSInputFile(path);
	}

	/**
	 * Wczytywanie danych z pliku wejœciowego FDS-a.
	 * 
	 * Pamiêtaj: x to d³ugoœæ tunelu, y to szerokoœæ. Podajemy czêsto
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
						width = Integer.parseInt(matcher.group(1));
						length = Integer.parseInt(matcher.group(2));
						gotDimensions = true;
						continue;
					}
				}

				// obstacles
				matcher = patternObstacle.matcher(line);
				if (matcher.find()) {
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

	private void addObstacle(int x1, int x2, int y1, int y2) {
		for (int x = x1; x < x2; x++)
			for (int y = y1; y < y2; y++)
				break;
		System.out.println("addObstacle():  L:" + x1 + "-" + x2 + "  W:" + y1 + "-" + y2);
	}

	public void initAgents() {
		// TODO: sk¹d braæ inicjalne pozycje ludzi? random?
	}

	/**
	 * Jedna iteracja symulacji.
	 */
	public void update() {
		try {
			Thread.sleep(1000); // na razie, póki nic tu nie ma do liczenia
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

}
