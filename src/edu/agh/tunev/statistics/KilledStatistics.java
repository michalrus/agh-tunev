package edu.agh.tunev.statistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class KilledStatistics implements Statistics {

	private JFreeChart chart;
	private XYSeries series;

	public KilledStatistics() {
		series = new XYSeries("people");

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		chart = ChartFactory.createXYLineChart(getTitle(), "Time [t]",
				"Num. killed", dataset, PlotOrientation.VERTICAL, true, true,
				false);
	}

	@Override
	public String getTitle() {
		return "Statystyka zabitych w po¿arze";
	}

	@Override
	public JFreeChart getChart() {
		return chart;
	}

	public void add(double time, int numDead) {
		series.add(time, numDead);
	}

}
