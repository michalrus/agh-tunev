package edu.agh.tunev.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.world.World;

public final class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	World world;
	private static Map<String, Class<?>> models = new HashMap<String, Class<?>>();

	public static void register(Class<?> thing) {
		try {
			if (AbstractModel.class.isAssignableFrom(thing))
				models.put(
						(String) thing.getDeclaredField("MODEL_NAME").get(null),
						thing);
			else
				throw new IllegalArgumentException(thing.getName()
						+ " is not an AbstractModel");
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
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu menu = new JMenu("Run model");
		menuBar.add(menu);

		for (final Map.Entry<String, Class<?>> e : models.entrySet()) {
			final JMenuItem menuItem = new JMenuItem(e.getKey());
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					onRunModel(e.getKey());
				}
			});
			menu.add(menuItem);
		}

		desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(desktopPane);

		desktopPane.addContainerListener(new ContainerListener() {
			@Override
			public void componentAdded(ContainerEvent e) {
				Component c = e.getChild();
				if (c instanceof JInternalFrame)
					((JInternalFrame) c)
							.addComponentListener(internalFrameComponentListener);
				if (c instanceof ControllerFrame)
					controllerFrames.add((ControllerFrame) c);
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
			}
		});
	}

	private JDesktopPane desktopPane;
	private int modelCounter = 0;

	void onRunModel(String name) {
		add(new ControllerFrame(++modelCounter, name, models.get(name), world));
	}

	private final Vector<ControllerFrame> controllerFrames = new Vector<ControllerFrame>();

	private void refreshAll() {
		for (ControllerFrame f : controllerFrames)
			if (f.refresher != null)
				f.refresher.refresh();
	}

	private final ComponentListener internalFrameComponentListener = new ComponentListener() {

		@Override
		public void componentHidden(ComponentEvent arg0) {
			refreshAll();
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			refreshAll();
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			refreshAll();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			refreshAll();
		}

	};
}
