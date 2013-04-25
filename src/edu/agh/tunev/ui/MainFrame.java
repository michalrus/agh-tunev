package edu.agh.tunev.ui;

import java.awt.Dimension;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.world.World;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	World world;
	static Map<String, Class<?>> models = new HashMap<String, Class<?>>();

	public static void register(Class<?> model) {
		if (!AbstractModel.class.isAssignableFrom(model))
			throw new IllegalArgumentException(model.getName()
					+ " is not a subclass of " + AbstractModel.class.getName());

		try {
			models.put((String) model.getDeclaredField("MODEL_NAME").get(null),
					model);
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			System.exit(ERROR);
		}
	}

	public MainFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
			}
		});
	}

	private void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (InstantiationException e) {
		} catch (ClassNotFoundException e) {
		} catch (UnsupportedLookAndFeelException e) {
		} catch (IllegalAccessException e) {
		}

		URL icon = this.getClass().getResource("/icon/icon.png");
		if (icon != null)
			setIconImage(new ImageIcon(icon).getImage());

		setTitle("TunEv");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		world = new World();

		new DataDialog(this);
	}

	void onDataLoaded(String dataName) {
		setTitle(dataName + " - " + getTitle());
		setSize(new Dimension(950, 700));
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
