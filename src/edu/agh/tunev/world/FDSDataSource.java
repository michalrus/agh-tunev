package edu.agh.tunev.world;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.agh.tunev.world.World.ProgressCallback;

final class FDSDataSource extends AbstractDataSource {

	@Override
	double getDuration() {
		return duration;
	}

	@Override
	Point2D.Double getDimension() {
		return dimension;
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
	Physics getPhysicsAt(double t, Point2D.Double p) {
		Entry<Double, Vector<Vector<Physics>>> entry;

		// get entry with its t greatest but <= @param t
		entry = physics.floorEntry(t);

		// if there's no such entry, return the first entry
		if (entry == null) {
			entry = physics.firstEntry();
			if (entry == null) // or throw empty-map exception
				throw new IllegalArgumentException("physics map is empty");
		}

		return getPhysicsInGrid(entry.getValue(), p);
	}

	@Override
	void readData(File from, ProgressCallback callback) {
		if (dataFolder != null)
			throw new IllegalArgumentException(
					"FDSDataSource.readData already called on this FDSDataSource");

		dataFolder = from;

		callback.update(progressDone, progressTotal, "Scanning file names...");
		parseFilenames();
		progressDone++;

		callback.update(progressDone, progressTotal,
				"Parsing file: tunnel.fds...");
		try {
			parseInputFile();
		} catch (FileNotFoundException | ParseException e) {
			callback.update(-1, -1, "Error: " + e.getMessage());
			return;
		}
		progressDone++;

		parseDataFiles(callback);
	}

	private NavigableMap<Double, Vector<Vector<Physics>>> physics;
	private int progressDone = 0, progressTotal = 2;
	private double duration = 0;
	private Point2D.Double dimension;
	private Point2D.Double offset;
	private Point2D.Double d;
	private File dataFolder, inputFile;
	private SortedMap<Integer, Map<Physics.Type, File>> dataFiles;
	private Vector<Obstacle> obstacles = new Vector<Obstacle>();
	private Vector<Exit> exits = new Vector<Exit>();

	/**
	 * Czyta folder {@link #dataFolder} i jeśli znajdzie pliki o nazwach
	 * pasujących do konwencji ustalonej z Kaśką, zapisuje je sobie odpowiednio
	 * w pamięci (w {@link #inputFile} i {@link #dataFiles}).
	 */
	private void parseFilenames() {
		Map<Physics.Type, Pattern> patterns = new EnumMap<Physics.Type, Pattern>(
				Physics.Type.class);

		patterns.put(Physics.Type.TEMPERATURE, Pattern.compile(
				"^temp_(\\d+)-(\\d+)\\.csv$", Pattern.CASE_INSENSITIVE));

		patterns.put(Physics.Type.CO, Pattern.compile(
				"^co_(\\d+)-(\\d+)\\.csv$", Pattern.CASE_INSENSITIVE));

		Pattern patternInput = Pattern.compile("^tunnel\\.fds$",
				Pattern.CASE_INSENSITIVE);

		Matcher matcher;

		dataFiles = new TreeMap<Integer, Map<Physics.Type, File>>();

		FILES_LOOP: for (File file : dataFolder.listFiles())
			if (file.isFile()) {
				for (Physics.Type key : patterns.keySet()) {
					matcher = patterns.get(key).matcher(file.getName());
					if (matcher.find()) {
						int t = Integer.parseInt(matcher.group(1));

						Map<Physics.Type, File> files = dataFiles.get(t);
						if (files == null) {
							files = new EnumMap<Physics.Type, File>(
									Physics.Type.class);
							dataFiles.put(t, files);
						}

						files.put(key, file);
						progressTotal++;

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
	 * Wczytywanie danych z pliku wejściowego FDS-a.
	 * 
	 * Pamiętaj: x to szerokość tunelu, y to długość. Podajemy często
	 * współrzędne x,y,z albo x,x1,y,y1,z,z1.
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
						long numCellsX = Long.parseLong(matcher.group(1));
						long numCellsY = Long.parseLong(matcher.group(2));

						offset = new Point2D.Double(Double.parseDouble(matcher
								.group(3)),
								Double.parseDouble(matcher.group(5)));
						dimension = new Point2D.Double(
								Double.parseDouble(matcher.group(4)) - offset.x,
								Double.parseDouble(matcher.group(6)) - offset.y);

						this.d = new Point2D.Double(dimension.x / numCellsX,
								dimension.y / numCellsY);

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

					obstacles
							.add(new Obstacle(new Point2D.Double(Double
									.parseDouble(matcher.group(1)) - offset.x,
									Double.parseDouble(matcher.group(3))
											- offset.y), new Point2D.Double(
									Double.parseDouble(matcher.group(2))
											- offset.x, Double
											.parseDouble(matcher.group(4))
											- offset.y)));

					continue;
				}

				// exit
				matcher = patternExit.matcher(line);
				if (matcher.find()) {
					if (!gotDimensions)
						throw new RuntimeException("&HOLE before &MESH!");

					exits.add(new Exit(new Point2D.Double(Double
							.parseDouble(matcher.group(1)) - offset.x, Double
							.parseDouble(matcher.group(3)) - offset.y),
							new Point2D.Double(Double.parseDouble(matcher
									.group(2)) - offset.x, Double
									.parseDouble(matcher.group(4)) - offset.y)));
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

		// TODO: Kasiu, jakoś oznaczamy główne wyjścia w .fds?
		if (dimension.x > dimension.y) { // poziomy tunel
			exits.add(new Exit(new Point2D.Double(0, 0), new Point2D.Double(0,
					dimension.y)));
			exits.add(new Exit(new Point2D.Double(dimension.x, 0),
					new Point2D.Double(dimension.y, dimension.y)));
		} else { // pionowy tunel
			exits.add(new Exit(new Point2D.Double(0, 0), new Point2D.Double(
					dimension.x, 0)));
			exits.add(new Exit(new Point2D.Double(0, dimension.y),
					new Point2D.Double(dimension.x, dimension.y)));
		}
	}

	private Physics getPhysicsInGrid(Vector<Vector<Physics>> grid,
			Point2D.Double p) {
		// always return *some* value, for all (x,y):

		int row = (int) Math.round(Math.floor(p.y / d.y));
		row = Math.max(row, 0);
		row = Math.min(row, grid.size() - 1);

		int col = (int) Math.round(Math.floor(p.x / d.x));
		col = Math.max(col, 0);
		col = Math.min(col, grid.get(0).size() - 1);

		return grid.get(row).get(col);
	}

	private void parseDataFiles(World.ProgressCallback callback) {
		physics = new ConcurrentSkipListMap<Double, Vector<Vector<Physics>>>();

		long numRows = Math.round(Math.floor(dimension.y / d.y)) + 1;
		long numCols = Math.round(Math.floor(dimension.x / d.x)) + 1;

		for (Entry<Integer, Map<Physics.Type, File>> e1 : dataFiles.entrySet()) {
			double t = e1.getKey();
			Vector<Vector<Physics>> grid = new Vector<Vector<Physics>>();

			for (long i = 0; i < numRows; i++) {
				Vector<Physics> row = new Vector<Physics>();
				for (long j = 0; j < numCols; j++)
					row.add(new Physics());
				grid.add(row);
			}

			for (Entry<Physics.Type, File> e2 : e1.getValue().entrySet()) {
				callback.update(progressDone, progressTotal,
						"Parsing data file: " + e2.getValue().getName() + "...");

				String line;
				long lineNum = 0;
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(e2.getValue()));
				} catch (FileNotFoundException e) {
					callback.update(-1, -1, e.toString());
					return;
				}
				try {
					// read header
					br.readLine();
					br.readLine();
					lineNum += 2;

					// read data
					while ((line = br.readLine()) != null) {
						lineNum++;
						String[] v = line.trim().split("\\s*,\\s*");

						if (v.length != 3) {
							callback.update(-1, -1, e2.getValue().getPath()
									+ ":" + lineNum + ": invalid format");
							return;
						}

						getPhysicsInGrid(
								grid,
								new Point2D.Double(Double.parseDouble(v[0]),
										Double.parseDouble(v[1]))).set(
								e2.getKey(), Double.parseDouble(v[2]));
					}

					progressDone++;
				} catch (IOException e) {
					callback.update(-1, -1, e.toString());
					return;
				} finally {
					if (br != null)
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}

			physics.put(t, grid);
		}

		callback.update(progressDone, progressTotal, "Ready.");
	}

}