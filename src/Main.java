
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board();
		
		board.initCells();
		
		UI.start(board);
		
		board.start();
	}

}
