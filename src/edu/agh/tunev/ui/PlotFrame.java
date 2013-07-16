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
	 * Name of the plot in UI.
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
