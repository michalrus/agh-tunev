import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import board.Board;
import board.Cell;

public class FDSParser {

	private Board board;
	private float dx, dy, offsetX, offsetY; // [m]
	private File dataFolder, inputFile;
	private SortedSet<DataFile> dataFiles;
	private int duration;

	public FDSParser(Board board, String dataFolder)
			throws FileNotFoundException, ParseException {
		this.board = board;
		this.dataFolder = new File(dataFolder);

		parseFilenames();
		parseInputFile();
	}

	/**
	 * Czyta folder {@link #dataFolder} i jeœli znajdzie pliki o nazwach
	 * pasuj¹ch do konwencji ustalonej z Kaœk¹, zapisuje je sobie odpowiednio w
	 * pamiêci (w {@link #inputFile} i {@link #dataFiles}).
	 */
	public void parseFilenames() {
		Map<DataFile.Type, Pattern> patterns = new HashMap<DataFile.Type, Pattern>();

		patterns.put(DataFile.Type.TEMPERATURE, Pattern.compile(
				"^temp_(\\d+)-(\\d+)s\\.csv$", Pattern.CASE_INSENSITIVE));

		patterns.put(DataFile.Type.CO, Pattern.compile(
				"^co_(\\d+)-(\\d+)s\\.csv$", Pattern.CASE_INSENSITIVE));

		Pattern patternInput = Pattern.compile("^tunnel\\.fds$",
				Pattern.CASE_INSENSITIVE);

		Matcher matcher;

		dataFiles = new TreeSet<DataFile>();

		FILES_LOOP: for (File file : dataFolder.listFiles())
			if (file.isFile()) {
				for (DataFile.Type key : patterns.keySet()) {
					matcher = patterns.get(key).matcher(file.getName());
					if (matcher.find()) {
						dataFiles.add(new DataFile(key, file, 1000 * Integer
								.parseInt(matcher.group(1)), 1000 * Integer
								.parseInt(matcher.group(2))));
						continue FILES_LOOP;
					}
				}

				if (inputFile == null
						&& patternInput.matcher(file.getName()).matches()) {
					inputFile = file;
					continue FILES_LOOP;
				}
			}
	}

	/**
	 * Wczytywanie danych z pliku wejœciowego FDS-a.
	 * 
	 * Pamiêtaj: x to szerokoœæ tunelu, y to d³ugoœæ. Podajemy czêsto
	 * wspó³rzêdne x,y,z albo x,x1,y,y1,z,z1.
	 * 
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public void parseInputFile() throws FileNotFoundException, ParseException {
		if (inputFile == null)
			throw new FileNotFoundException(dataFolder.getPath()
					+ ": no .fds input file found inside");

		boolean gotDimensions = false;
		Pattern patternDimensions = Pattern
				.compile("^&MESH\\s+IJK=(\\d+),(\\d+),\\d+,\\s*XB=(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+");

		Pattern patternObstacle = Pattern
				.compile("^&OBST\\s+XB=(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+");

		boolean gotDuration = false;
		Pattern patternDuration = Pattern.compile("^&TIME\\s+T_END=(\\d+)");
		Matcher matcher;

		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		try {
			while ((line = br.readLine()) != null) {
				// dimensions
				if (!gotDimensions) {
					matcher = patternDimensions.matcher(line);
					if (matcher.find()) {
						int width = Integer.parseInt(matcher.group(1));
						int length = Integer.parseInt(matcher.group(2));

						offsetX = Integer.parseInt(matcher.group(3));
						dx = (Integer.parseInt(matcher.group(4)) - offsetX)
								/ width;
						offsetY = Integer.parseInt(matcher.group(5));
						dy = (Integer.parseInt(matcher.group(6)) - offsetY)
								/ length;

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
				throw new ParseException(inputFile.getPath()
						+ ": no mesh dimensions declared", 0);
			if (!gotDuration)
				throw new ParseException(inputFile.getPath()
						+ ": no simulation duration declared", 0);
		}
	}

	private static class DataFile implements Comparable<DataFile> {
		public File file;
		public int start, end; // [ms]
		public boolean alreadyRead = false;
		public Type type;

		public enum Type {
			TEMPERATURE, CO
		}

		public DataFile(Type type, File file, int start, int end) {
			this.type = type;
			this.file = file;
			this.start = start;
			this.end = end;
		}

		@Override
		public int compareTo(DataFile o) {
			if (start == o.start)
				return end - o.end;
			return start - o.start;
		}
	}

	/**
	 * Nie czyta pliku ju¿ raz wczytanego!!! (Optymalizacja IO). Wszystko dzia³a
	 * OK i te dane s¹ wci¹¿ pamiêtane w komórkach {@link Cell}, jeœli
	 * wywo³ujemy {@link readData} z zawsze WIÊKSZYM argumentem ni¿ w poprzednim
	 * wywo³aniu!
	 * 
	 * @param currentTime
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public void readData(int currentTime) throws FileNotFoundException,
			ParseException {
		for (DataFile f : dataFiles)
			if (currentTime >= f.start && currentTime < f.end) {
				if (f.alreadyRead) //
					continue;

				String line;
				int lineNum = 0;
				BufferedReader br = new BufferedReader(new FileReader(f.file));
				try {
					br.readLine();
					lineNum++;
					br.readLine();
					lineNum++;
					while ((line = br.readLine()) != null) {
						lineNum++;
						String[] v = line.trim().split("\\s*,\\s*");
						if (v.length != 3)
							throw new ParseException(f.file.getPath() + ":"
									+ lineNum + ": invalid format", lineNum);
						int x = (int) Math.round(Double.valueOf(v[0]) / dx);
						int y = (int) Math.round(Double.valueOf(v[1]) / dy);
						double value = Double.valueOf(v[2]);

						Cell cell = board.getCellAt(x, y);
						if (cell == null)
							continue;

						switch (f.type){
						case CO:
							// cell.setCO((float)value);
							break;
						case TEMPERATURE:
							cell.setTemperature((float)value);
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null)
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}

				f.alreadyRead = true;
			}
	}

	public int getDuration() {
		return duration;
	}
}
