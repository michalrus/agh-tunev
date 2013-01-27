package sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import stats.StatFrame;
import stats.Statistics;

import board.Board;
import board.BoardView;

public final class UI extends JFrame {
	private static final long serialVersionUID = 1L;

	private JScrollPane boardScrollPane;
	private BoardView boardView;
	private ChartFrame chartFrameAgents, chartFrameHcbo, chartFrameVelocity;

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
		setSize(new Dimension(950, 700));
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
		//desktopPane.add(new ControlFrame(new Point(600, 0), new Dimension(300,
		//		300)));

		List<String> tmp;

		// chart: agents
		tmp = new ArrayList<String>();
		tmp.add("Uratowani");
		tmp.add("¯ywi");
		tmp.add("Martwi");
		chartFrameAgents = new ChartFrame("Statystyki agentów",
				"Liczba agentów", tmp, new Point(0, 300), new Dimension(300,
						300));
		desktopPane.add(chartFrameAgents);

		// chart: hcbo
		tmp = new ArrayList<String>();
		tmp.add("Wszyscy agenci");
		chartFrameHcbo = new ChartFrame("Statystyki hcbo", "Œrednie stê¿enie",
				tmp, new Point(300, 300), new Dimension(300, 300));
		desktopPane.add(chartFrameHcbo);

		// chart: velocity
		tmp = new ArrayList<String>();
		tmp.add("Wszyscy agenci");
		chartFrameVelocity = new ChartFrame("Statystyki prêdkoœci",
				"Œrednia prêdkoœæ", tmp, new Point(600, 300), new Dimension(
						300, 300));
		desktopPane.add(chartFrameVelocity);

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
				synchronized (stats) {
					List<StatFrame> frames = stats.getFrames();
					if (frames.isEmpty()) {
						chartFrameAgents.clearSeries();
						chartFrameHcbo.clearSeries();
						chartFrameVelocity.clearSeries();
					} else {
						StatFrame last = frames.get(frames.size() - 1);

						List<Double> tmp1 = new ArrayList<Double>();
						tmp1.add((double) last.getAgentsExited());
						tmp1.add((double) last.getAgentsAlive());
						tmp1.add((double) last.getAgentsDead());
						chartFrameAgents.addToSeries(last.getTime(), tmp1);

						List<Double> tmp2 = new ArrayList<Double>();
						tmp2.add(last.getHbcoAvg());
						chartFrameHcbo.addToSeries(last.getTime(), tmp2);

						List<Double> tmp3 = new ArrayList<Double>();
						tmp3.add(last.getVeloAvg());
						chartFrameVelocity.addToSeries(last.getTime(), tmp3);
					}
				}
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
		public ControlFrame(Point location, Dimension size) {
			super("Control", true, false, true, true);

			setLocation(location);
			setSize(size);
			setVisible(true);

			JLabel control = new JLabel("(kontrola)");
			control.setHorizontalAlignment(JLabel.CENTER);
			add(control);
		}
	}

	private class ChartFrame extends JInternalFrame {
		List<XYSeries> series;

		public ChartFrame(String name, String nameOY, List<String> seriesNames,
				Point location, Dimension size) {
			super(name, true, false, true, true);

			setLocation(location);
			setSize(size);
			setVisible(true);

			series = new ArrayList<XYSeries>();
			XYSeriesCollection dataset = new XYSeriesCollection();
			for (String seriesName : seriesNames) {
				XYSeries s = new XYSeries(seriesName);
				dataset.addSeries(s);
				series.add(s);
			}

			JFreeChart chart = ChartFactory.createXYLineChart(name,
					"Czas [s]", nameOY, dataset, PlotOrientation.VERTICAL,
					true, true, false);
			add(new ChartPanel(chart));
		}

		public void addToSeries(double time, List<Double> values) {
			if (values.size() != series.size()) {
				System.out.println("?! " + series.size() + " != " + values.size());
				return;
			}
			
			int i = 0;
			for (Double value : values) {
				series.get(i).add(time / 1000.0, value);
				i++;
			}
		}

		public void clearSeries() {
			for (XYSeries s : series) {
				s.clear();
			}
		}
	}

}
