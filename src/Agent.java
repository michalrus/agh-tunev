
public class Agent {
	
	private boolean alive = true;

	public Agent () {
		// TODO: losowanie moich cech/charakterystyki
	}
	
	public void update () {
		if (!alive)
			return;
		
		checkIfIWillLive();
		
		checkCollisions();

		computePotentialComponentByThreat();
		computePotentialComponentByExit();
		computePotentialComponentBySocialDistances();

		decideWhereToGo();
		
		updateMotorSkills();
	}

	private void checkIfIWillLive () {
		// TODO: sprawdzenie czy prze¿yjê nastêpn¹ iteracjê
		// if (...)
		//	alive = false;
	}

	private void checkCollisions () {
		// TODO: sprawdzenie kolizji
	}

	private void computePotentialComponentByThreat () {
		// TODO: sk³adowa potencja³u od zagro¿enia po¿arowego 
	}

	private void computePotentialComponentByExit () {
		// TODO: sk³adowa potencja³u od ew. wyjœcia (jeœli widoczne) 
	}

	private void computePotentialComponentBySocialDistances () {
		// TODO: sk³adowa potencja³u od Social Distances
	}

	private void decideWhereToGo () {
		// TODO: jeœli czekamy, ¿eby symulowaæ zmianê prêdkoœci, przechowaj decyzjê na potem?
	}

	private void updateMotorSkills () {
		// TODO: ograniczenie zdolnoœci poruszania siê w wyniku zatrucia?
	}

}
