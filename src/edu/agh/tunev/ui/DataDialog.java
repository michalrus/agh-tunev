package edu.agh.tunev.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import edu.agh.tunev.world.World;

final class DataDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JFileChooser dirChooser;
	
	/** layout insets */
	private static final Insets INSETS = new Insets(5, 5, 5, 5);

	public DataDialog(final MainFrame mainFrame) {
		dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser
				.setCurrentDirectory(new File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Select FDS data directory...");

		if (dirChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
			mainFrame.dispose();
			this.dispose();
			return;
		}

		final JProgressBar progress = new JProgressBar();
		final JLabel label = new JLabel();
		label.setVerticalAlignment(JLabel.TOP);

		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new EmptyBorder(INSETS));
		add(p, BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = INSETS;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		p.add(label, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		p.add(progress, c);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(400, 120);
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(mainFrame);

		final File dir = dirChooser.getSelectedFile();
		
		// love java for that monstrosity below -,-
		new Thread(new Runnable() {
			public void run() {
				mainFrame.world.readData(dir, new World.ProgressCallback() {
					public void update(final int done, final int total,
							final String msg) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								label.setText("<html>" + msg + "</html>");
								if (total < 0 || done < 0) {
									mainFrame.dispose();
									DataDialog.this
											.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
								} else {
									progress.setMaximum(total);
									progress.setValue(done);
									if (done >= total) {
										DataDialog.this.dispose();
										mainFrame.onDataLoaded(dir
												.getAbsolutePath());
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}

}
