import java.util.ArrayList;

public class Board {

	private ArrayList<Agent> agents;
	
	public Board () {
		agents = new ArrayList<Agent>();
		
		for (;;) {
			setFdsData();
			updateAgentsPosition();
			addNewAgents();
			
			for (int i = 0; i < agents.size(); i++)
				agents.get(i).update();
			
			// TODO: check end conditions
			// if (...)
			//	break;
		}
	}
	
	private void setFdsData () {
		// TODO: 
	}

	private void updateAgentsPosition () {
		// TODO: 
	}

	private void addNewAgents () {
		// TODO: 
	}

}
