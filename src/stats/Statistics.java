package stats;

import java.util.ArrayList;
import java.util.List;

import sim.Simulation;

public class Statistics {

	/** Lista porcji danych w konkretny momentach czasu */
	private List<StatFrame> stat_frames;

	/** Referencja do symulacji */
	private Simulation sim;

	public Statistics(Simulation _sim) {
		stat_frames = new ArrayList<StatFrame>();
		this.sim = _sim;
	}

	/** Dodaje nowa porcje danych do listy */
	public void update() {
		stat_frames.add(new StatFrame(sim));
	}

}
