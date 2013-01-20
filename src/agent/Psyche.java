package agent;

import java.util.Random;

class Psyche {

	private final static double MIN_REACTION_T = 0.1 * 1000; //[ms]
	private final static double MAX_REACTION_T = 1 * 1000;	//[ms]

	/** Czas reakcji na zagro¿enie = detekcja + decyzja */
	double reaction_t;

	/** Referencja do agenta */
	private Agent agent;

	/** Random number generator */
	private Random rand;

	Psyche(Agent _agent) {
		rand = new Random();
		this.agent = _agent;
		reaction_t = (MAX_REACTION_T - MIN_REACTION_T) * rand.nextDouble()
				+ MIN_REACTION_T;
	}

}
