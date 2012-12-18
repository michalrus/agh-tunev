import board.Board;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board();

		board.initCells();
		board.initAgents();
	
		UI ui = new UI();

		for (;;) {
			board.update();
			ui.draw(board);
			
			// TODO: check end conditions
		}
	}

}
