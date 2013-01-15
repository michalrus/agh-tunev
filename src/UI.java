import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import board.Board;
import board.BoardView;

public final class UI extends JFrame {
	private static final long serialVersionUID = 1L;

	private JScrollPane boardScrollPane;
	private BoardView boardView;

	public UI() {
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
		setMinimumSize(new Dimension(640, 480));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/* real elements */
		
		// board
		boardView = new BoardView();
		boardScrollPane = new JScrollPane(boardView);
		
		// control panel
		JLabel control = new JLabel("(kontrola)");
		control.setHorizontalAlignment(JLabel.CENTER);
		
		// stats console
		JLabel stats = new JLabel("(statystyki)");
		stats.setHorizontalAlignment(JLabel.CENTER);

		// chart
		JLabel chart = new JLabel("(wykres)");
		chart.setHorizontalAlignment(JLabel.CENTER);
		
		/* layout */

		// south
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(control, BorderLayout.WEST);

		// stats
		JSplitPane statsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stats, chart);
		statsSplitPane.setResizeWeight(0.5);
		southPanel.add(statsSplitPane, BorderLayout.CENTER);

		// main panel
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, boardScrollPane, southPanel);
		mainSplitPane.setResizeWeight(0.5);
		mainSplitPane.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
		add(mainSplitPane);
		setVisible(true);
		mainSplitPane.setDividerLocation(0.75);
	}

	public void draw(final Board board) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boardView.setBoard(board);
				boardView.repaint();
				boardScrollPane.revalidate();
			}
		});
	}

}
