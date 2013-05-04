package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;

final class PersonRenderer implements Renderable {

	private final PersonProfile person;
	private final AbstractModel model;

	public PersonRenderer(PersonProfile person, AbstractModel model) {
		this.person = person;
		this.model = model;
	}

	@Override
	public void render(GL2 gl, double t) {
		gl.glPushMatrix();

		PersonState state = model.getPersonState(person, t);

		gl.glTranslated(state.position.x, 0, state.position.y);
		gl.glRotated(state.orientation, 0, 1, 0);
		gl.glColor4d(1, 1, 1, 1);
		
		switch (state.movement) {
		case HIDDEN:
			break;
		case DEAD:
			gl.glColor4d(1, 0, 0, 1);
		case CRAWLING:
			drawCrawlingPerson(gl);
			break;
		case SQUATTING:
			drawSquattingPerson(gl);
			break;
		case STANDING:
		default:
			drawStandingPerson(gl);
			break;
		}

		gl.glPopMatrix();
	}

	private static double relativeHeadSize = 0.3;

	private void drawStandingPerson(GL2 gl) {
		gl.glPushMatrix();

		gl.glPushMatrix();
		gl.glTranslated(-PersonProfile.WIDTH / 2, 0, -PersonProfile.GIRTH / 2);
		Common.drawCuboid(gl, PersonProfile.WIDTH, person.height
				* (1.0 - relativeHeadSize), PersonProfile.GIRTH);
		gl.glPopMatrix();

		gl.glTranslated(0, person.height * (1.0 - relativeHeadSize / 2.0), 0);
		GLUT glut = new GLUT();
		final double radius = person.height * relativeHeadSize / 2;
		glut.glutSolidSphere(radius, 20, 20);
		gl.glColor4d(1, 0, 0, 1);
		glut.glutSolidCone(radius/2, radius * 4, 20, 20);

		gl.glPopMatrix();
	}

	private void drawCrawlingPerson(GL2 gl) {
		drawStandingPerson(gl);
	}

	private void drawSquattingPerson(GL2 gl) {
		drawStandingPerson(gl);
	}
}
