package edu.agh.tunev.ui.opengl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.world.Obstacle;
import edu.agh.tunev.world.World;

public class Scene implements GLEventListener {

	public interface SceneGetter {
		public double getTime();

		public double getRho();

		public double getPhi();

		public double getTheta();

		public Point2D.Double getAnchor();
		
		public boolean getPaintTemp();
	}

	private final World world;
	private final SceneGetter sceneGetter;
	private final List<Renderable> renderers;
	private final AbstractModel model;
	private final Vector<PersonProfile> people;

	public Scene(World world, AbstractModel model,
			Vector<PersonProfile> people, SceneGetter timeGetter) {
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
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// set camera
		setCamera(gl, rho, phi, theta, anchor);

		// render all
		for (Renderable r : renderers)
			r.render(gl, t);
	}

	private final static float[] lightAmbient = { 0.2f, 0.2f, 0.2f, 1.0f };
	private final static float[] lightDiffuse = { 0.8f, 0.8f, 0.8f, 1.0f };
	private final static float[] lightSpecular = { 0.5f, 0.5f, 0.5f, 1.0f };

	private final static double lightThetaOffset = Math.toRadians(0);
	private final static double lightPhiOffset = Math.toRadians(30);

	private void setCamera(GL2 gl, double rho, double phi, double theta,
			Point2D.Double anchor) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		final double camX = rho * Math.cos(phi) * Math.cos(theta);
		final double camY = rho * Math.sin(phi);
		final double camZ = rho * Math.cos(phi) * Math.sin(theta);

		GLU glu = GLU.createGLU(gl);
		glu.gluLookAt(camX + anchor.x, camY, camZ + anchor.y, anchor.x, 0,
				anchor.y, 0, 1, 0);

		final double liX = rho * Math.cos(phi + lightPhiOffset)
				* Math.cos(theta + lightThetaOffset);
		final double liY = rho * Math.sin(phi + lightPhiOffset);
		final double liZ = rho * Math.cos(phi + lightPhiOffset)
				* Math.sin(theta + lightThetaOffset);

		final float[] liXYZ = { (float) (liX + anchor.x), (float) liY,
				(float) (liZ + anchor.y), 1.0f };
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
				liXYZ, 0);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// init all renderers
		// renderers.add(new TestRenderer());

		renderers.add(new FloorRenderer(world, sceneGetter));
		renderers.add(new WallsRenderer(world));

		for (Obstacle o : world.getObstacles())
			renderers.add(new ObstacleRenderer(o));
		
		for (PersonProfile p : people)
			renderers.add(new PersonRenderer(p, model));
		
		// init GL
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0.5f);
		gl.glClearDepth(1.0);

		//gl.glEnable(GL2.GL_LINE_SMOOTH);
		//gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_LINE_SMOOTH, GL2.GL_NICEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// lighting
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT,
				lightAmbient, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
				lightDiffuse, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
				lightSpecular, 0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		if (height == 0)
			height = 1;

		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(x, y, width, height);

		GLU glu = GLU.createGLU(gl);
		glu.gluPerspective(45, (float) width / height, 1, 100);
	}

}
