package board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import agent.Agent;
import agent.Neighborhood;

public class Board {

	private ArrayList<Agent> agents;

	public Board(String path) {
		agents = new ArrayList<Agent>();
	}

	public void initAgents() {
		// TODO: sk¹d braæ inicjalne pozycje ludzi? random?
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

		return map;
	}

}
