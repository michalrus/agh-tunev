package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;

import edu.agh.tunev.interpolation.Interpolator;
import edu.agh.tunev.world.World;

public class Scene implements GLEventListener {

	public interface TimeGetter {
		public double get();
	}

	private final World world;
	private final Interpolator interpolator;
	private final TimeGetter timeGetter;

	public Scene(World world, Interpolator interpolator, TimeGetter timeGetter) {
		this.world = world;
		this.interpolator = interpolator;
		this.timeGetter = timeGetter;
	}
	
	private double theta = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		double t = timeGetter.get();
		
		theta += 0.005;
		double s = Math.sin(theta);
		double c = Math.cos(theta);

		GL2 gl = drawable.getGL().getGL2();

		// clear buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

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

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// antialiasing
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}

}
