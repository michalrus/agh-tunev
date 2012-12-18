import board.Board;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board("firedata/tunnel.fds");

		board.initAgents(); // w jaki sposób? gdzie inicjalni agenci? random?

		UI ui = new UI();

		for (;;) {
			board.update();
			ui.draw(board);

			// TODO: check end conditions
		}
	}

}
