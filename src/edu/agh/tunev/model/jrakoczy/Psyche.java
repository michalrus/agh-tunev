/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package edu.agh.tunev.model.jrakoczy;

import java.util.Random;

class Psyche {

	private final static double MIN_REACTION_T = 0.1; // [s]
	private final static double MAX_REACTION_T = 1; // [s]

	private final static double ANXIETY_COEFF_SIGNIF = 0.2;
	private final static double MIN_THREAT_COMP = 35 * Agent.TEMP_THREAT_COEFF;

	/** Time of reaction to a threat = detection + decision */
	double reaction_t;

	/**
	 * Anxiety of an agent. Default: 1 when there's no threat.
	 */
	double anxiety;

	/** Anxiety coefficient, individual characteristic */
	private double anxiety_coeff;

	/** Random number generator */
	private Random rand;

	Psyche(Agent _agent) {
		rand = new Random();
		reaction_t = (MAX_REACTION_T - MIN_REACTION_T) * rand.nextDouble()
				+ MIN_REACTION_T;
		anxiety = 1;
		anxiety_coeff = 1 + rand.nextDouble() * ANXIETY_COEFF_SIGNIF;
	}

	void expAnxiety(double threat_comp) {
		if (threat_comp > MIN_THREAT_COMP)
			anxiety = anxiety_coeff * threat_comp;
		else
			anxiety = 1;
	}
}
