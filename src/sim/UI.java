package sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import stats.Statistics;

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

		// desktop
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(desktopPane);

		// board
		boardView = new BoardView();
		boardScrollPane = new JScrollPane(boardView);
		desktopPane.add(new BoardFrame(boardScrollPane));

		// control
		desktopPane.add(new ControlFrame());

		// chart
		desktopPane.add(new ChartFrame());

		setVisible(true);
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

	public void draw(final Statistics stats) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
//				boardView.setBoard(board);
	//			boardView.repaint();
		//		boardScrollPane.revalidate();
			}
		});
	}

	private class BoardFrame extends JInternalFrame {
		public BoardFrame(JComponent inside) {
			super("Board", true, false, true, true);
			
			setLocation(0, 0);
			setSize(600, 300);
			setVisible(true);
			
			add(inside);
		}
	}

	private class ControlFrame extends JInternalFrame {
		public ControlFrame() {
			super("Control", true, false, true, true);
			
			setLocation(0, 300);
			setSize(300, 300);
			setVisible(true);

			JLabel control = new JLabel("(kontrola)");
			control.setHorizontalAlignment(JLabel.CENTER);
			add(control);
		}
	}

	private class ChartFrame extends JInternalFrame {
		public ChartFrame() {
			super("Chart", true, false, true, true);
			
			setLocation(300, 300);
			setSize(300, 300);
			setVisible(true);

			JLabel control = new JLabel("(wykres)");
			control.setHorizontalAlignment(JLabel.CENTER);
			add(control);
		}
	}

}
