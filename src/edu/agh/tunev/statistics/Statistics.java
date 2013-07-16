/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
