import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;

import board.Board;

public final class Main {

	private static final double SIMULATION_SPEEDUP = 1.0f; // ... times

	/**
	 * Tymczasowo wyrzuca FileNotFoundException, póki nie bêdzie (o ile bêdzie!)
	 * kiedyœ wybierania pliku wejœciowego FDS-a z poziomu UI.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			ParseException {
		Board board;
		board = new Board();

		FDSParser parser = new FDSParser(board, "data/");

		board.initAgentsRandomly(100);

		UI ui = new UI();

		// all time values in [ms]
		double simulationDuration = parser.getDuration();
		double simulationTime = 0;
		double dt;
		long currentCPUTime = new Date().getTime();
		long previousCPUTime;

		while (simulationTime < simulationDuration) {
			try {
				parser.readData(simulationTime);
			} catch (FileNotFoundException | ParseException e) {
				/*
				 * if user deleted a needed data file *during* simulation, then
				 * they *won't have* that data -,- ignore, keep simulating
				 */
			}

			previousCPUTime = currentCPUTime;
			currentCPUTime = new Date().getTime();
			dt = (currentCPUTime - previousCPUTime) * SIMULATION_SPEEDUP;
			simulationTime += dt;

			board.update(dt);
			ui.draw(board);

			// sztuczne opóŸnienie, tylko na razie -- m.
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

}
