import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import board.Board;
import board.Cell;

public class UI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JScrollPane panel;
	private Grid grid;
	
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

		JPanel paMain = new JPanel(new BorderLayout());
		paMain.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
		add(paMain, BorderLayout.CENTER);

		grid = new Grid();
		panel = new JScrollPane(grid);
		paMain.add(panel);
		
		setVisible(true);
	}

	public void draw(final Board board) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				grid.setBoard(board);
				grid.repaint();
				panel.revalidate();
			}
		});
	}

	/**
	 * Visual representation of a Board.
	 * @author Michal
	 *
	 */
	private class Grid extends JComponent {
		private static final long serialVersionUID = 1L;
		private static final int CELL_SIZE = 20;
		
		private Board board = null;
		
		public void setBoard (Board board) {
			this.board = board;
		}
		
		@Override
		public Dimension getPreferredSize() {
			return getMySize();
		}
		
		@Override
		public Dimension getMinimumSize() {
			return getMySize();
		}
		
		@Override
		public Dimension getMaximumSize() {
			return getMySize();
		}
		
		private Dimension getMySize() {
			if (board == null)
				return new Dimension(0, 0);
		    return new Dimension(board.getLength() * CELL_SIZE, board.getWidth() * CELL_SIZE);
		}

		public void paintComponent (Graphics g) {
			if (board == null)
				return;
			
			int width = board.getWidth();
			int length = board.getLength();
			for (int x = 0; x < width; x++)
				for (int y = 0; y < length; y++) {
					Cell cell = board.cellAt(x, y);

					switch (cell.getType()) {
					case BLOCKED:
						g.setColor(Color.BLACK);
						break;
					default:
						g.setColor(Color.WHITE);
						break;
					}
					
					g.fillRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				}
		}
	}

}
