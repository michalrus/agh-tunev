package edu.agh.tunev.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import edu.agh.tunev.world.World;

class DataDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JFileChooser dirChooser;
	
	public DataDialog(final MainFrame mainFrame) {
		dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser
				.setCurrentDirectory(new File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Select FDS data directory...");

		if (dirChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
			this.dispose();
			return;
		}
		
		final JProgressBar progress = new JProgressBar();
		final JLabel label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progress, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		add(label, BorderLayout.NORTH);
		add(panel, BorderLayout.SOUTH);
	    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    setSize(300, 90);
	    setResizable(false);
		setVisible(true);
		setLocationRelativeTo(mainFrame);

		File dir = dirChooser.getSelectedFile();

		mainFrame.world.readData(dir, new World.ProgressCallback() {
			public void update(final int done, final int total, final String msg) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						label.setText(msg);
						if (total < 0 || done < 0) {
							mainFrame.dispose();
							DataDialog.this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						}
						else {
							progress.setValue(done);
							progress.setMaximum(total);
							if (done >= total) {
								DataDialog.this.dispose();
								mainFrame.onDataLoaded();
							}
						}
					}
				});
			}
		});
	}

}
