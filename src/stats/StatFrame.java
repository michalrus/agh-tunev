package stats;

import agent.Agent;
import board.Board;
import sim.Simulation;

/** Klasa reprezentuje porcjê danych dotycz¹cych symulacji w danej sekundzie */
class StatFrame {
	/** Czas w ktorym zosta³y zebrane dane */
	private double time; // [ms]

	private long agents_exited;
	private long agents_alive;
	private long agents_dead;

	/** Œrednia stê¿enia karboksyhemoglobiny we krwi agentów */
	private double hbco_avg;

	/** Œrednia prêdkoœæ agentów */
	private double velo_avg;

	/**
	 * W momencie utworzenia zbiera dane z symulacji i zapisuje do pól
	 * 
	 * @param sim
	 *            referencja do symulacji
	 */
	public StatFrame(Simulation sim) {
		time = sim.getSimTime();
		hbco_avg = 0;
		velo_avg = 0;
		int cnt = 0;
		Board board = sim.getBoard();

		for (Agent agent : board.getAgents()) {
			if (agent.isExited())
				++agents_exited;

			if (agent.isAlive())
				++agents_alive;
			else
				++agents_dead;

			hbco_avg += agent.getHBCO();
			velo_avg += agent.getVelocity();
			++cnt;
		}
		hbco_avg /= cnt;
		velo_avg /= cnt;
	}

	public double getTime() {
		return time;
	}

	public long getAgentsExited() {
		return agents_exited;
	}

	public long getAgentsAlive() {
		return agents_alive;
	}

	public long getAgentsDead() {
		return agents_dead;
	}

	public double getHbcoAvg() {
		return hbco_avg;
	}

	public double getVeloAvg() {
		return velo_avg;
	}
}
