package edu.agh.tunev.ui.opengl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.AbstractPerson;
import edu.agh.tunev.world.World;

public class Scene implements GLEventListener {

	public interface SceneGetter {
		public double getTime();

		public double getRho();

		public double getPhi();

		public double getTheta();

		public Point2D.Double getAnchor();
	}

	private final World world;
	private final SceneGetter sceneGetter;
	private final List<Renderable> renderers;
	private final AbstractModel<? extends AbstractPerson> model;
	private final Vector<AbstractPerson> people;

	public Scene(World world, AbstractModel<? extends AbstractPerson> model,
			Vector<AbstractPerson> people, SceneGetter timeGetter) {
		this.world = world;
		this.model = model;
		this.people = people;
		this.sceneGetter = timeGetter;
		renderers = new ArrayList<Renderable>();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final double t = sceneGetter.getTime();
		final double rho = sceneGetter.getRho();
		final double phi = sceneGetter.getPhi();
		final double theta = sceneGetter.getTheta();
		final Point2D.Double anchor = sceneGetter.getAnchor();

		GL2 gl = drawable.getGL().getGL2();

		// clear buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// set camera
		setCamera(gl, rho, phi, theta, anchor);

		// render all
		for (Renderable r : renderers)
			r.render(gl, t);
	}

	private void setCamera(GL2 gl, double rho, double phi, double theta,
			Point2D.Double anchor) {

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// init all renderers
		renderers.add(new TestRenderer());

		renderers.add(new FloorRenderer(world));
		renderers.add(new WallsRenderer(world));

		for (AbstractPerson p : people)
			renderers.add(new PersonRenderer(p, model));

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
