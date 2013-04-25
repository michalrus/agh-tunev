package edu.agh.tunev.ui;

import java.awt.Dimension;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.agh.tunev.world.World;

interface Callback {}

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFileChooser dirChooser;
	private World world;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
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
		
        setTitle("TunEv");
        setSize(new Dimension(950, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        URL icon = this.getClass().getResource("/icon/icon.png");
        if (icon != null)
        	setIconImage(new ImageIcon(icon).getImage());
		
		dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Select FDS data directory...");
		
		if (dirChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			this.dispose();
			return;
		}
		
		File dir = dirChooser.getSelectedFile();
		
		world = new World();
		world.readData(dir, new World.ProgressCallback(){
			@Override
			public void update(int done, int total, String msg) {
				
			}
		});
		
		setVisible(true);
	}
}
