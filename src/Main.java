import java.io.FileNotFoundException;

import board.Board;

public class Main {

	/**
	 * Tymczasowo wyrzuca FileNotFoundException, póki nie bêdzie (o ile bêdzie!)
	 * kiedyœ wybierania pliku wejœciowego FDS-a z poziomu UI.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Board board;

		board = new Board("firedata/tunnel.fds");

		board.initAgents(); // w jaki sposób? gdzie inicjalni agenci? random?

		UI ui = new UI();

		for (;;) {
			board.update();
			ui.draw(board);

			// TODO: check end conditions
		}
	}

}
