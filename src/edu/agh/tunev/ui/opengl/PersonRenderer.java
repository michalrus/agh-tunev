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

	private static double relativeHeadSize = 0.3;

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
			drawCrawlingPerson(gl, person.height * (1.0 - relativeHeadSize),
					person.height * relativeHeadSize / 2);
			break;
		case SQUATTING:
			drawBasicPerson(gl, person.height * (0.5 - relativeHeadSize),
					person.height * relativeHeadSize / 2);
			break;
		case STANDING:
		default:
			drawBasicPerson(gl, person.height * (1.0 - relativeHeadSize),
					person.height * relativeHeadSize / 2);
			break;
		}

		gl.glPopMatrix();
	}

	private void drawBasicPerson(GL2 gl, double bodyHeight, double headRadius) {
		gl.glPushMatrix();

		drawBody(gl, bodyHeight);
		gl.glTranslated(0, bodyHeight + headRadius, 0);
		drawHead(gl, headRadius);

		gl.glPopMatrix();
	}

	private void drawCrawlingPerson(GL2 gl, double bodyHeight, double headRadius) {
		gl.glPushMatrix();

		final double height = bodyHeight + 2 * headRadius;
		gl.glTranslated(-PersonProfile.WIDTH / 2, 0, -height/2);
		Common.drawCuboid(gl, PersonProfile.WIDTH, PersonProfile.GIRTH, bodyHeight);
		gl.glTranslated(+PersonProfile.WIDTH / 2, PersonProfile.GIRTH / 2, bodyHeight+headRadius);
		drawHead(gl, headRadius);

		gl.glPopMatrix();
	}

	private void drawBody(GL2 gl, double height) {
		gl.glPushMatrix();
		gl.glTranslated(-PersonProfile.WIDTH / 2, 0, -PersonProfile.GIRTH / 2);
		Common.drawCuboid(gl, PersonProfile.WIDTH, height, PersonProfile.GIRTH);
		gl.glPopMatrix();
	}

	private void drawHead(GL2 gl, double radius) {
		GLUT glut = new GLUT();
		glut.glutSolidSphere(radius, 20, 20);
		gl.glColor4d(1, 0, 0, 1);
		glut.glutSolidCone(radius / 2, radius * 4, 20, 20);
	}
}
