import java.io.FileNotFoundException;
import java.text.ParseException;

import board.Board;

public class Main {

	private static final int ITERATION_DURATION = 500; // [ms]
	private static final float SIMULATION_SPEEDUP = 1.0f; // ... times

	/**
	 * Tymczasowo wyrzuca FileNotFoundException, póki nie bêdzie (o ile bêdzie!)
	 * kiedyœ wybierania pliku wejœciowego FDS-a z poziomu UI.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws FileNotFoundException, ParseException {
		Board board;

		board = new Board();

		FDSParser parser = new FDSParser(board, "data/");

		int duration = parser.getDuration();
		int currentTime = 0; // [ms]

		board.initAgentsRandomly(10);

		UI ui = new UI();

		while (currentTime < duration) {
			try {
				parser.readData(currentTime);
			} catch (FileNotFoundException | ParseException e1) {
				/*
				 * if user deleted a needed data file *during* simulation, then
				 * they won't have that data -,- ignore, keep simulating
				 */
				e1.printStackTrace();
			}

			board.update();
			ui.draw(board);

			try {
				Thread.sleep(Math
						.round(ITERATION_DURATION / SIMULATION_SPEEDUP));
			} catch (InterruptedException e) {
			}
			currentTime += ITERATION_DURATION;
		}
	}

}
