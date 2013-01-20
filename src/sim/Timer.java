package sim;

/** Klasa mierz¹ca czas symulacji, wszystkie wartoœci w [ms] */
public class Timer {
	/** Przyspieszenie symulacji wzg czasu w pliku z FDS */
	private static final double SIMULATION_SPEEDUP = 1.0f;

	/** Pocz¹tek symulacji mierzony BEZWZGLÊDNIE wg czasu systemu */
	private long start_time;

	/** Aktualny czas symulacji, wartoœæ WZGLÊDNA w odniesieniu do start_time */
	private long current_time;

	/** Ró¿nica czasu pomiêdzy aktualnym current_dt, a poprzednim */
	private double current_dt;

	/** Metoda zaczyna odmierzanie czasu */
	public void init() {
		start_time = System.currentTimeMillis();
		current_time = 0;
		current_dt = 0;
	}

	/** Aktualizujemy timer wzg czasu systemu */
	public void updateTime() {
		long new_time = System.currentTimeMillis() - start_time;
		current_dt = (new_time - current_time) * SIMULATION_SPEEDUP;
		current_time = new_time;
	}

	/**
	 * @return aktualny wzglêdny czas symulacji
	 */
	public long getCurrentTime() {
		return current_time;
	}

	/**
	 * @return aktualn¹ ró¿niczkê czasu
	 */

	public double getCurrentDt() {
		return current_dt;
	}
}
