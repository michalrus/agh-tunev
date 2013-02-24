package sim;

import java.io.FileNotFoundException;
import java.text.ParseException;

import board.Board.NoPhysicsDataException;

public class MainPane {
	// TODO: na razie mamy tutaj lekki burdel z wyj¹tkami ;P
	
	// test-commit

	public static void main(String[] args) {
		UI ui = new UI();
		Simulation sim = null;
		try {
			sim = new Simulation("data/", ui);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			sim.start(300);
		} catch (NoPhysicsDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
