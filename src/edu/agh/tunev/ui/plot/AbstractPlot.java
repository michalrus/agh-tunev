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

	public AbstractPlot(int modelNumber, String modelName, int plotNumber,
			Class<? extends AbstractPlot> subclass) {
		String name = null;
		try {
			name = (String) subclass.getDeclaredField("PLOT_NAME").get(this);
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e1) {
		}

		setTitle(modelNumber + ": " + modelName + " - " + plotNumber + ": "
				+ name);
		setSize(400, 300);
		setLocation(modelNumber * 20 + 400 + 20 + plotNumber * 20, modelNumber
				* 20 + 20 + plotNumber * 20);
		setFrameIcon(null);
		setResizable(true);
		setClosable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
