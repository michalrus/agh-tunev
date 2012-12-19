package agent;

import board.Cell;

public class Neighborhood {

	public enum Direction {
		TOP, TOPRIGHT, RIGHT, BOTTOMRIGHT, BOTTOM, BOTTOMLEFT, LEFT, TOPLEFT;
		
		/**Potrzebne miedzy innymi do badania zagrozenia.
		 * @param dir
		 * 			zadany kierunek
		 * @return
		 * 			kierunek przeciwny do podanego
		 */			
		public static Direction getOppositeDir(Direction dir){
			Direction opposite = null;
			switch(dir){
				case TOP:
					opposite = BOTTOM;
					break;
				case TOPRIGHT:
					opposite = BOTTOMLEFT;
					break;
				case RIGHT:
					opposite = LEFT;
					break;
				case BOTTOMRIGHT:
					opposite = TOPLEFT;
					break;
				case BOTTOM:
					opposite = TOP;
					break;
				case BOTTOMLEFT:
					opposite = TOPRIGHT;
					break;
				case LEFT:
					opposite = RIGHT;
					break;
				case TOPLEFT:
					opposite = BOTTOMRIGHT;
					break;				
			}
			return opposite;
		}
	}

	private Cell first;

	public int getTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Cell getFirstCell() {
		return first;
	}

}
