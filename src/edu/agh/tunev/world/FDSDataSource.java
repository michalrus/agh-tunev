package edu.agh.tunev.world;

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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.agh.tunev.world.World.ProgressCallback;

final class FDSDataSource extends AbstractDataSource {

	@Override
	double getDuration() {
		return duration;
	}

	@Override
	double getXDimension() {
		return dimensionX;
	}

	@Override
	double getYDimension() {
		return dimensionY;
	}

	@Override
	Vector<Exit> getExits() {
		return exits;
	}

	@Override
	Vector<Obstacle> getObstacles() {
		return obstacles;
	}

	@Override
	Physics getPhysicsAt(double t, double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private int progressDone = 0, progressTotal = Integer.MAX_VALUE;

	@Override
	void readData(File from, ProgressCallback callback) {
		if (dataFolder != null)
			throw new IllegalArgumentException("FDSDataSource.readData already called on this FDSDataSource");
		
		dataFolder = from;
		
		callback.update(progressDone, progressTotal, "Scanning file names...");
		parseFilenames();
		progressDone++;
		progressTotal = dataFiles.size() + 2;
		
		callback.update(progressDone, progressTotal, "Parsing tunnel.fds file...");
		try {
			parseInputFile();
		} catch (FileNotFoundException | ParseException e) {
			callback.update(-1, -1, "Error: " + e.getMessage());
			return;
		}
		progressDone++;
		
		// TODO: parseDataFiles(callback);
	}
	
	private double duration = 0;
	private double dimensionX = 0, dimensionY = 0;
	private double offsetX = 0, offsetY = 0; // [m]
	private long numCellsX = 0, numCellsY = 0;
	private File dataFolder, inputFile;
	private SortedSet<DataFile> dataFiles;
	private Vector<Obstacle> obstacles = new Vector<Obstacle>();
	private Vector<Exit> exits = new Vector<Exit>();

	/**
	 * Czyta folder {@link #dataFolder} i jeœli znajdzie pliki o nazwach
	 * pasuj¹cych do konwencji ustalonej z Kaœk¹, zapisuje je sobie odpowiednio
	 * w pamiêci (w {@link #inputFile} i {@link #dataFiles}).
	 */
	private void parseFilenames() {
		Map<Physics.Type, Pattern> patterns = new HashMap<Physics.Type, Pattern>();

		patterns.put(Physics.Type.TEMPERATURE, Pattern.compile(
				"^temp_(\\d+)-(\\d+)\\.csv$", Pattern.CASE_INSENSITIVE));

		patterns.put(Physics.Type.CO, Pattern.compile("^co_(\\d+)-(\\d+)\\.csv$",
				Pattern.CASE_INSENSITIVE));

		Pattern patternInput = Pattern.compile("^tunnel\\.fds$",
				Pattern.CASE_INSENSITIVE);

		Matcher matcher;

		dataFiles = new TreeSet<DataFile>();

		FILES_LOOP: for (File file : dataFolder.listFiles())
			if (file.isFile()) {
				for (Physics.Type key : patterns.keySet()) {
					matcher = patterns.get(key).matcher(file.getName());
					if (matcher.find()) {
						dataFiles.add(new DataFile(key, file, 1000 * Long
								.parseLong(matcher.group(1)), 1000 * Long
								.parseLong(matcher.group(2))));
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
	private void parseInputFile() throws FileNotFoundException, ParseException {
		if (inputFile == null)
			throw new FileNotFoundException(dataFolder.getPath()
					+ ": no .fds input file found inside");

		// regular expression of a Double
		String d = "[^,\\s/]+";

		boolean gotDimensions = false;
		Pattern patternDimensions = Pattern.compile("^&MESH\\s+IJK\\s*=\\s*("
				+ d + ")\\s*,\\s*(" + d + ")\\s*,\\s*" + d
				+ ",\\s*XB\\s*=\\s*(" + d + ")\\s*,\\s*(" + d + ")\\s*,\\s*("
				+ d + ")\\s*,\\s*(" + d + ")\\s*,\\s*" + d + "\\s*,\\s*" + d);

		Pattern patternObstacle = Pattern.compile("^&OBST\\s+XB\\s*=\\s*(" + d
				+ ")\\s*,\\s*(" + d + ")\\s*,\\s*(" + d + ")\\s*,\\s*(" + d
				+ ")\\s*,\\s*" + d + "\\s*,\\s*" + d);

		Pattern patternExit = Pattern.compile("^&HOLE\\s+XB\\s*=\\s*(" + d
				+ ")\\s*,\\s*(" + d + ")\\s*,\\s*(" + d + ")\\s*,\\s*(" + d
				+ ")\\s*,\\s*" + d + "\\s*,\\s*" + d);

		boolean gotDuration = false;
		Pattern patternDuration = Pattern
				.compile("^&TIME\\s+T_END=(" + d + ")");
		Matcher matcher;

		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		try {
			while ((line = br.readLine()) != null) {
				// dimensions
				if (!gotDimensions) {
					matcher = patternDimensions.matcher(line);
					if (matcher.find()) {
						numCellsX = Long.parseLong(matcher.group(1));
						numCellsY = Long.parseLong(matcher.group(2));

						offsetX = Double.parseDouble(matcher.group(3));
						offsetY = Double.parseDouble(matcher.group(5));
						dimensionX = Double.parseDouble(matcher.group(4))
								- offsetX;
						dimensionY = Double.parseDouble(matcher.group(6))
								- offsetY;

						gotDimensions = true;
						continue;
					}
				}

				// duration
				if (!gotDuration) {
					matcher = patternDuration.matcher(line);
					if (matcher.find()) {
						duration = Double.parseDouble(matcher.group(1));
						gotDuration = true;
						continue;
					}
				}

				// obstacle
				matcher = patternObstacle.matcher(line);
				if (matcher.find()) {
					if (!gotDimensions)
						throw new RuntimeException("&OBST before &MESH!");

					if (line.contains("PERMIT_HOLE=.TRUE."))
						continue;

					obstacles.add(new Obstacle(
							Double.parseDouble(matcher.group(1)) - offsetX,
							Double.parseDouble(matcher.group(3)) - offsetY,
							Double.parseDouble(matcher.group(2)) - offsetX,
							Double.parseDouble(matcher.group(4)) - offsetY));

					continue;
				}

				// exit
				matcher = patternExit.matcher(line);
				if (matcher.find()) {
					if (!gotDimensions)
						throw new RuntimeException("&HOLE before &MESH!");

					exits.add(new Exit(
						Double.parseDouble(matcher.group(1)) - offsetX,
						Double.parseDouble(matcher.group(3)) - offsetY,
						Double.parseDouble(matcher.group(2)) - offsetX,
						Double.parseDouble(matcher.group(4)) - offsetY));
					continue;
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
			if (!gotDimensions)
				throw new ParseException(inputFile.getPath()
						+ ": no mesh dimensions declared", 0);
			if (!gotDuration)
				throw new ParseException(inputFile.getPath()
						+ ": no simulation duration declared", 0);
		}

		// TODO: Kasiu, jakoœ oznaczamy g³ówne wyjœcia w .fds?
		if (dimensionX > dimensionY) { // poziomy tunel
			exits.add(new Exit(0, 0, 0, dimensionY));
			exits.add(new Exit(dimensionX, 0, dimensionX, dimensionY));
		} else { // pionowy tunel
			exits.add(new Exit(0, 0, dimensionX, 0));
			exits.add(new Exit(0, dimensionY, dimensionX, dimensionY));
		}
	}

	private static final class DataFile implements Comparable<DataFile> {
		public File file;
		public long start, end; // [ms]
		public boolean alreadyRead = false;
		public Physics.Type type;

		public DataFile(Physics.Type type, File file, long start, long end) {
			this.type = type;
			this.file = file;
			this.start = start;
			this.end = end;
		}

		@Override
		public int compareTo(DataFile o) {
			if (start == o.start)
				if (end == o.end)
					return file.compareTo(o.file);
				else
					return (int) (end - o.end);
			else
				return (int) (start - o.start);
		}
	}

	/**
	 * Nie czyta pliku ju¿ raz wczytanego!!! (Optymalizacja IO). Wszystko dzia³a
	 * OK i te dane s¹ wci¹¿ pamiêtane w komórkach {@link Cell}, jeœli
	 * wywo³ujemy {@link readData} z zawsze WIÊKSZYM argumentem ni¿ w poprzednim
	 * wywo³aniu!
	 * 
	 * @param simulationTime
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
/*	private void OLDreadData(double simulationTime) throws FileNotFoundException,
			ParseException {
		for (DataFile f : dataFiles) {
			if (simulationTime >= f.start && simulationTime < f.end) {
				if (f.alreadyRead)
					continue;

				String line;
				long lineNum = 0;
				BufferedReader br = new BufferedReader(new FileReader(f.file));
				try {
					// read header
					br.readLine();
					br.readLine();
					lineNum += 2;

					// read data
					while ((line = br.readLine()) != null) {
						lineNum++;
						String[] v = line.trim().split("\\s*,\\s*");

						if (v.length != 3)
							throw new ParseException(f.file.getPath() + ":"
									+ lineNum + ": invalid format",
									(int) lineNum);

						try {
							board.setPhysics(
									new Point(Double.parseDouble(v[0]), Double
											.parseDouble(v[1])), f.type, Double
											.parseDouble(v[2]));
						} catch (IndexOutOfBoundsException e) {
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
	}*/

}