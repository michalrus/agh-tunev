package edu.agh.tunev.ui.plot;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

public abstract class AbstractPlot extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Nazwa wykresu w UI.
	 */
	public static String PLOT_NAME;
	public abstract String getName();

	public AbstractPlot(int modelNumber, String modelName, int plotNumber,
			Class<? extends AbstractPlot> clazz) {
		setTitle(modelNumber + ": " + modelName + " - " + plotNumber + ": "
				+ getName());
		setSize(300, 300);
		setLocation(modelNumber * 20 + 400 + plotNumber * 20, modelNumber * 20
				+ plotNumber * 20);
		setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					AbstractPlot.this.setSelected(true);
				} catch (PropertyVetoException e) {
					AbstractPlot.this.toFront();
				}
			}
		});
	}

}
