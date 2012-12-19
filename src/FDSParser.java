import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import board.Board;

public class FDSParser {

	Board board;
	float dx, dy, offsetX, offsetY; // [m]

	public FDSParser(Board board) {
		this.board = board;
	}

	/**
	 * Wczytywanie danych z pliku wejœciowego FDS-a.
	 * 
	 * Pamiêtaj: x to szerokoœæ tunelu, y to d³ugoœæ. Podajemy czêsto
	 * wspó³rzêdne x,y,z albo x,x1,y,y1,z,z1.
	 * 
	 * @param board
	 * @param path
	 * @return Simulation time in [ms].
	 */
	public int inputFile(String path)
			throws FileNotFoundException {
		boolean gotDimensions = false;
		Pattern patternDimensions = Pattern
				.compile("^&MESH\\s+IJK=(\\d+),(\\d+),\\d+,\\s*XB=(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+");

		Pattern patternObstacle = Pattern
				.compile("^&OBST\\s+XB=(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+");

		boolean gotDuration = false;
		int duration = 0;
		Pattern patternDuration = Pattern.compile("^&TIME\\s+T_END=(\\d+)");
		Matcher matcher;

		String line;
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			while ((line = br.readLine()) != null) {
				// dimensions
				if (!gotDimensions) {
					matcher = patternDimensions.matcher(line);
					if (matcher.find()) {
						int width = Integer.parseInt(matcher.group(1));
						int length = Integer.parseInt(matcher.group(2));

						offsetX = Integer.parseInt(matcher.group(3));
						dx = (Integer.parseInt(matcher.group(4)) - offsetX) / width;
						offsetY = Integer.parseInt(matcher.group(5));
						dy = (Integer.parseInt(matcher.group(6)) - offsetY) / length;
						
						System.out.println("dx=" + dx + "  dy=" + dy + "  offsetX=" + offsetX + "  offsetY=" + offsetY);
						
						board.initCells(width, length);
						gotDimensions = true;
						continue;
					}
				}

				// duration
				if (!gotDuration) {
					matcher = patternDuration.matcher(line);
					if (matcher.find()) {
						duration = 1000 * Integer.parseInt(matcher.group(1));
						gotDuration = true;
						continue;
					}
				}

				// obstacles
				matcher = patternObstacle.matcher(line);
				if (matcher.find()) {
					if (!gotDimensions)
						throw new RuntimeException("&OBST before &MESH!");
					int x1 = Integer.parseInt(matcher.group(1));
					int x2 = Integer.parseInt(matcher.group(2));
					int y1 = Integer.parseInt(matcher.group(3));
					int y2 = Integer.parseInt(matcher.group(4));
					board.addObstacle(x1, x2, y1, y2);
					continue;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (!gotDimensions)
				throw new RuntimeException(path
						+ ": no mesh dimensions declared");
			if (!gotDuration)
				throw new RuntimeException(path
						+ ": no simulation duration declared");
		}

		return duration;
	}

	public void dataFile(String directory, int currentTime) {
	}

}
