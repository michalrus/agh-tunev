package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

final class TestRenderer implements AbstractRenderer {

	private double theta = 0;
	
	@Override
	public void render(GL2 gl, double t) {
		theta += 0.005;
		double s = Math.sin(theta);
		double c = Math.cos(theta);

		// draw an animating triangle
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(1, 0, 0);
		gl.glVertex2d(-c, -c);
		gl.glColor3f(0, 1, 0);
		gl.glVertex2d(0, c);
		gl.glColor3f(0, 0, 1);
		gl.glVertex2d(s, -s);
		gl.glEnd();
	}

}
