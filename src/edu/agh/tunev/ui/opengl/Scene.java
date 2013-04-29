package edu.agh.tunev.ui.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import edu.agh.tunev.interpolation.Interpolator;
import edu.agh.tunev.world.World;

public class Scene implements GLEventListener {

	public interface TimeGetter {
		public double get();
	}

	// private final World world;
	// private final Interpolator interpolator;
	private final TimeGetter timeGetter;
	private final List<AbstractRenderer> renderers;

	public Scene(World world, Interpolator interpolator, TimeGetter timeGetter) {
		// this.world = world;
		// this.interpolator = interpolator;
		this.timeGetter = timeGetter;
		renderers = new ArrayList<AbstractRenderer>();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		double t = timeGetter.get();

		GL2 gl = drawable.getGL().getGL2();

		// clear buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// render all
		for (AbstractRenderer r : renderers)
			r.render(gl, t);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// init all renderers
		renderers.add(new TestRenderer());

		// turn on antialiasing
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
