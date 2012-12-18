import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import board.Board;

public class UI extends JFrame {
	private static final long serialVersionUID = 1L;

	public UI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
				setVisible(true);
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
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel paMain = new JPanel(new BorderLayout());
		paMain.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
		add(paMain, BorderLayout.CENTER);
	}

	public void draw(final Board board) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				drawBoard(board);
			}
		});
	}

	private void drawBoard(Board board) {

	}

}
