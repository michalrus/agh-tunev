package edu.agh.tunev.statistics;

import org.jfree.chart.JFreeChart;

/**
 * Interfejs, który implementuj¹ obiekty reprezentuj¹ce jakieœ zmierzone dane w
 * modelu.
 * 
 * Dla danych maj¹cych sens dla ka¿dego modelu (np. liczba zabitych w czasie),
 * umieszczajmy ich klasê w tym pakiecie.
 * 
 * Dla danych maj¹cych sens tylko dla konkretnego modelu, umieszczajmy ich klasê
 * gdzieœ w pakiecie tego modelu. Mo¿e edu.agh.tunev.model._nazwa_.statistics?
 * 
 */
public interface Statistics {

	public abstract String getTitle();

	public abstract JFreeChart getChart();

	/**
	 * Kiedy chcemy, ¿eby konkretne Statistics by³o dostêpne w UI, musimy wywo³aæ
	 * Statistics.AddCallback.add(konkretne_statistics). AddCallback jest
	 * przekazywany w parametrze do AbstractModel.simulate(), podobnie jak
	 * ProgressCallback.
	 */
	public interface AddCallback {
		public void add(Statistics statistics);
	}

}
