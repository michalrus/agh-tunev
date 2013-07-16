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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class LifeStatistics implements Statistics {

	private JFreeChart chart;
	private XYSeries killed, alive, rescued;

	public LifeStatistics() {
		killed = new XYSeries("killed");
		alive = new XYSeries("alive");
		rescued = new XYSeries("rescued");

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(killed);
		dataset.addSeries(alive);
		dataset.addSeries(rescued);

		chart = ChartFactory.createXYLineChart(getTitle(), "Time [s]",
				"Num. people", dataset, PlotOrientation.VERTICAL, true, true,
				false);
	}

	@Override
	public String getTitle() {
		return "Life statistics";
	}

	@Override
	public JFreeChart getChart() {
		return chart;
	}

	public void add(double time, int numAlive, int numRescued, int numDead) {
		killed.add(time, numDead);
		alive.add(time, numAlive);
		rescued.add(time, numRescued);
	}

}
