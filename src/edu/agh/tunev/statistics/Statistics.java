package edu.agh.tunev.statistics;

import org.jfree.chart.JFreeChart;

/**
 * Interfejs, który implementują obiekty reprezentujące jakieś zmierzone dane w
 * modelu.
 * 
 * Dla danych mających sens dla każdego modelu (np. liczba zabitych w czasie),
 * umieszczajmy ich klasę w tym pakiecie.
 * 
 * Dla danych mających sens tylko dla konkretnego modelu, umieszczajmy ich klasę
 * gdzieś w pakiecie tego modelu. Może edu.agh.tunev.model._nazwa_.statistics?
 * 
 */
public interface Statistics {

	public abstract String getTitle();

	public abstract JFreeChart getChart();

	/**
	 * Kiedy chcemy, żeby konkretne Statistics było dostępne w UI, musimy wywołać
	 * Statistics.AddCallback.add(konkretne_statistics). AddCallback jest
	 * przekazywany w parametrze do AbstractModel.simulate(), podobnie jak
	 * ProgressCallback.
	 */
	public interface AddCallback {
		public void add(Statistics statistics);
	}

}
