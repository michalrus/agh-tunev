package edu.agh.tunev.statistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class HCBOStatistics implements Statistics {

	private JFreeChart chart;
	private XYSeries average;

	public HCBOStatistics() {
		average = new XYSeries("average");

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(average);

		chart = ChartFactory.createXYLineChart(getTitle(), "Time [s]",
				"Concentration", dataset, PlotOrientation.VERTICAL, true, true,
				false);
	}

	@Override
	public String getTitle() {
		return "HCBO statistics";
	}

	@Override
	public JFreeChart getChart() {
		return chart;
	}

	public void add(double time, double averageHCBO) {
		average.add(time, averageHCBO);
	}

}
