package edu.agh.tunev.statistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class VelocityStatistics implements Statistics {

	private JFreeChart chart;
	private XYSeries average;

	public VelocityStatistics() {
		average = new XYSeries("average");

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(average);

		chart = ChartFactory.createXYLineChart(getTitle(), "Time [s]",
				"Velocity [m/s]", dataset, PlotOrientation.VERTICAL, true, true,
				false);
	}

	@Override
	public String getTitle() {
		return "Velocity statistics";
	}

	@Override
	public JFreeChart getChart() {
		return chart;
	}

	public void add(double time, double averageVelocity) {
		average.add(time, averageVelocity);
	}

}
