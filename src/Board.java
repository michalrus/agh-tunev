import java.util.ArrayList;

public class Board {

	private ArrayList<Agent> agents;
	
	public Board () {
		agents = new ArrayList<Agent>();
	}
	
	public void initCells () {
		
	}
	
	public void start () {
		for (;;) {
			setFdsData();
			updateAgentsPosition();
			addNewAgents();
			
			for (Agent agent : agents)
				agent.update();
			
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
