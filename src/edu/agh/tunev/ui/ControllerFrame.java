package edu.agh.tunev.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.Person;
import edu.agh.tunev.world.World;

class ControllerFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private AbstractModel model;
	
	JLabel simulationMsg, simulationIter, simulationTime;
	JProgressBar simulationProgress;
	Vector<Person> people;
	World world;

	ControllerFrame(int number, String name, Class<?> clazz, final World world) {
		setTitle(number + ": " + name + " - controller");
		setSize(new Dimension(400, 100));
		setFrameIcon(null);
		setVisible(true);
		
		this.world = world;

		try {
			this.model = (AbstractModel) clazz.getDeclaredConstructor(
					World.class).newInstance(world);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error during instantiation of "
					+ clazz.getName() + ".");
		}

		JPanel p = new JPanel();
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		
		add(p, BorderLayout.CENTER);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1, 3));
		p2.setAlignmentX(0);
		p.add(p2);
		
		simulationMsg = new JLabel();
		p2.add(simulationMsg);
		
		simulationIter = new JLabel();
		p2.add(simulationIter);
		
		simulationTime = new JLabel();
		p2.add(simulationTime);
		
		simulationProgress = new JProgressBar();
		simulationProgress.setAlignmentX(0);
		p.add(simulationProgress);
		
		people = PeopleFactory.random(world, 50);
		
		new Thread(new Runnable(){
			public void run() {
				model.simulate(world.getDuration(), people, new World.ProgressCallback() {
					public void update(final int done, final int total, final String msg) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								simulationProgress.setMaximum(total);
								simulationProgress.setValue(done);
								simulationMsg.setText(msg);
								simulationIter.setText(done + "/" + total);
								simulationTime.setText("t = " + world.getDuration() * total / done + " [s]");
							}
						});
					}
				});				
			}
		}).start();
	}

}
