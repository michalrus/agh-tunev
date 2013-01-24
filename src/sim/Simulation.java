package sim;

import java.io.FileNotFoundException;
import java.text.ParseException;

import stats.Statistics;

import board.Board;
import board.Board.NoPhysicsDataException;

public final class Simulation {

	/** Referencja do planszy */
	private Board board;

	/** Referencja do klasy obs³uguj¹cej mierzenie czasu symulacji */
	private Timer timer;

	/** Referencja do UI, w którym wyœwietli siê symulacja */
	private UI ui;

	/** Referencja do klasy klasy przechowujacej staty */
	private Statistics stats;

	/**
	 * TODO: Dalej tymczasowo wyrzuca FileNotFound. dataFile powinno byæ
	 * pobierane z pola tekstowego UI, a nie podawane w konstruktorze.
	 * 
	 * @param dataFile
	 *            tymczasowo
	 * @param _agents_num
	 *            liczba losowo wygenerowanych agentów
	 * @param _ui
	 *            referencja do GUI
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public Simulation(String dataFile, UI _ui) throws FileNotFoundException,
			ParseException {
		board = new Board(dataFile, this);
		timer = new Timer();
		stats = new Statistics(this);
		this.ui = _ui;
	}

	/**
	 * Start symulacji - inicjalizuje agentów, uruchamia timer, odpala
	 * simulate()
	 * 
	 * @param _agents_num
	 *            liczba losowo wygenerowanych agentów
	 * @throws InterruptedException
	 * @throws NoPhysicsDataException
	 */
	public void start(long _agents_num) throws NoPhysicsDataException,
			InterruptedException {
		board.initAgentsRandomly(_agents_num);
		timer.init();
		// TODO: tutaj trzeba ogarnac jakies lepsze generowanie agentow
		simulate();

	}

	public long getSimTime() {
		return timer.getCurrentTime();
	}

	/**
	 * Odpowiada za uaktualnianie stanu symulacji zgodznie z danymi z pliku i
	 * up³ywaj¹cym czasem.
	 * 
	 * @throws NoPhysicsDataException
	 * @throws InterruptedException
	 */
	private void simulate() throws NoPhysicsDataException, InterruptedException {
		double simulation_duration = board.getDuration();
		long curr_time;
		double dt;

		while ((curr_time = timer.getCurrentTime()) < simulation_duration) {
			try {
				board.updateData(curr_time);
			} catch (FileNotFoundException | ParseException e) {
				/*
				 * if user deleted a needed data file *during* simulation, then
				 * they *won't have* that data -,- ignore, keep simulating
				 */
			}

			dt = timer.getCurrentDt();
			board.update(dt);
			stats.update();
			ui.draw(board);
			ui.draw(stats);
			timer.updateTime();
		}
	}

	public Board getBoard() {
		return board;
	}

	public Timer getTimer() {
		return timer;
	}
}
