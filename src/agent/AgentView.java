package agent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import board.BoardView;
import board.Point;

/**
 * Klasa reprezentuj¹ca wygl¹d Agenta na planszy.
 * 
 * Jeœli chcesz mieæ dostêp do prywatnych pól/metod Agenta, nie pisz przy nich
 * ¿adnego modyfikatora dostêpu: ani private, ani public, ani protected. Wtedy
 * pole/metoda bêd¹ "package-private", czyli prywatne dla ka¿dego z zewn¹trz,
 * ale publiczne w danym package (ka¿da klasa w agent.* widzi pola bez
 * modyfikatora w ka¿dej innej klasie w agent.*). -- m.
 */
public class AgentView {

	Agent agent;

	public AgentView(Agent agent) {
		this.agent = agent;
	}

	/** Rysowanko! */
	public void paint(BoardView bv, Graphics2D g2, double scale) {
		// push()
		AffineTransform at = g2.getTransform();

		Point p = agent.getPosition();

		int w = (int) Math.round(Agent.BROADNESS * scale);
		int h = (int) Math.round(Agent.THICKNESS * scale);

		// move & rotate LCS (local coordinate system)
		g2.translate(bv.translateX(p), bv.translateY(p));
		g2.rotate(Math.toRadians(90 - agent.getOrientation()));

		// oval
		g2.setColor(Color.CYAN);
		g2.fillOval(-w / 2, -h / 2, w, h);
		g2.setColor(Color.BLUE);
		g2.drawOval(-w / 2, -h / 2, w, h);

		// orientation
		BoardView.drawVector(g2, 0, 0,
				(int) Math.round(Agent.ORIENTATION_VECTOR * scale));

		// pop()
		g2.setTransform(at);
	}

}
