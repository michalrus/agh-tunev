package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

import edu.agh.tunev.world.Obstacle;

final class ObstacleRenderer implements Renderable {

	final Obstacle obstacle;

	public ObstacleRenderer(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	@Override
	public void render(GL2 gl, double t) {
		gl.glPushMatrix();
		final double xmin = Math.min(obstacle.p1.x, obstacle.p2.x);
		final double xmax = Math.max(obstacle.p1.x, obstacle.p2.x);
		final double ymin = Math.min(obstacle.p1.y, obstacle.p2.y);
		final double ymax = Math.max(obstacle.p1.y, obstacle.p2.y);
		gl.glTranslated(xmin, 0, ymin);
		Common.drawCuboid(gl, xmax - xmin, obstacle.height, ymax - ymin);
		gl.glPopMatrix();
	}

}
