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

	@Override
	public void display(GLAutoDrawable drawable) {
		double t = timeGetter.get();

		GL2 gl = drawable.getGL().getGL2();

		// clear buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// draw a triangle filling the window
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(1, 0, 0);
		gl.glVertex2f(-1, -1);
		gl.glColor3f(0, 1, 0);
		gl.glVertex2f(0, 1);
		gl.glColor3f(0, 0, 1);
		gl.glVertex2f(1, -1);
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
