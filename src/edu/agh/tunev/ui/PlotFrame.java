package edu.agh.tunev.ui;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;

import edu.agh.tunev.statistics.Statistics;

public final class PlotFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Nazwa wykresu w UI.
	 */
	public static String PLOT_NAME;

	public PlotFrame(int modelNumber, String modelName, Statistics statistics) {
		setTitle(modelNumber + ": " + modelName + " - " + statistics.getTitle());
		setSize(400, 300);
		setLocation(modelNumber * 20 + 400 + 20, modelNumber * 20 + 20);
		setFrameIcon(null);
		setResizable(true);
		setClosable(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		add(new ChartPanel(statistics.getChart()), BorderLayout.CENTER);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							PlotFrame.this.setSelected(true);
						} catch (PropertyVetoException e) {
							PlotFrame.this.toFront();
						}
					}
				});
			}

		});
	}

}
