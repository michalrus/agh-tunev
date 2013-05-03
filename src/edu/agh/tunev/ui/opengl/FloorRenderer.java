package edu.agh.tunev.ui.opengl;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import edu.agh.tunev.world.World;

final class FloorRenderer implements Renderable {

	private final World world;

	public FloorRenderer(World world) {
		this.world = world;
	}

	@Override
	public void render(GL2 gl, double t) {
		Point2D.Double dim = world.getDimension();
		
		gl.glPushMatrix();
		
		gl.glColor4d(1, 1, 1, 1);
		
		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3d(0, 1, 0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, dim.y);
		gl.glVertex3d(dim.x, 0, dim.y);
		gl.glVertex3d(dim.x, 0, 0);
		gl.glEnd();
		
		gl.glPopMatrix();
	}

}
