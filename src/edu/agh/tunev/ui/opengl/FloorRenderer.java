/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package edu.agh.tunev.ui.opengl;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import edu.agh.tunev.ui.opengl.Scene.SceneGetter;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.Physics;

final class FloorRenderer implements Renderable {

	private final World world;
	private final SceneGetter sceneGetter;
	private boolean paintTemp;

	public FloorRenderer(World world, SceneGetter sceneGetter) {
		this.world = world;
		this.sceneGetter = sceneGetter;
	}

	private void colorVertex (GL2 gl, double t, double x, double y) {
		if (paintTemp) {
			final double p = world.getPhysicsAt(t,
					new Point2D.Double(x, y)).get(
					Physics.Type.TEMPERATURE);
			if (!Double.isNaN(p)) {
				final Common.Color c = Common.temp2Color(p);
				gl.glColor4d(c.r, c.g, c.b, 1);
			}
			else
				gl.glColor4d(1, 1, 1, 1);
		}
		else
			gl.glColor4d(1, 1, 1, 1);
		gl.glVertex3d(x, 0, y);
	}

	@Override
	public void render(GL2 gl, double t) {
		gl.glPushMatrix();
		
		paintTemp = sceneGetter.getPaintTemp();
		
		Point2D.Double dim = world.getDimension();
		Point2D.Double d = world.getPhysicsGranularity();
		final int nx = (int) Math.round(Math.ceil(dim.x / d.x));
		final int ny = (int) Math.round(Math.ceil(dim.y / d.y));

		for (int ix = 0; ix < nx; ix++)
			for (int iy = 0; iy < ny; iy++) {
				final double x1 = d.x * ix;
				final double y1 = d.y * iy;
				final double x2 = (ix < nx - 1 ? x1 + d.x : dim.x);
				final double y2 = (iy < ny - 1 ? y1 + d.y : dim.y);

				gl.glBegin(GL2.GL_QUADS);
				gl.glNormal3d(0, 1, 0);

				colorVertex(gl, t, x1, y1);
				colorVertex(gl, t, x1, y2);
				colorVertex(gl, t, x2, y2);
				colorVertex(gl, t, x2, y1);

				gl.glEnd();
			}
		
		// Draw the grid (1x1 m)
		if (sceneGetter.getPaintGrid())
			drawGrid(gl, dim, 1.0, 1.0);

		gl.glPopMatrix();
	}

	/** rysuje siatkę na podłodze o zadanej dyskretyzacji */
	private void drawGrid (GL2 gl, Point2D.Double dim, double dx, double dy) {
		final int nx = (int) Math.round(Math.ceil(dim.x / dx));
		final int ny = (int) Math.round(Math.ceil(dim.y / dy));

		for (int ix = 0; ix < nx; ix++)
			for (int iy = 0; iy < ny; iy++) {
				final double x1 = dx * ix;
				final double y1 = dy * iy;
				final double x2 = (ix < nx - 1 ? x1 + dx : dim.x);
				final double y2 = (iy < ny - 1 ? y1 + dy : dim.y);

				gl.glColor4d(0, 0, 0, 1);
				gl.glBegin(GL2.GL_LINES);
				
				gl.glVertex3d(x1, 0.01, y1);
				gl.glVertex3d(x1, 0.01, y2);

				gl.glVertex3d(x1, 0.01, y2);
				gl.glVertex3d(x2, 0.01, y2);
				
				gl.glVertex3d(x2, 0.01, y2);
				gl.glVertex3d(x2, 0.01, y1);
				
				gl.glVertex3d(x2, 0.01, y1);
				gl.glVertex3d(x1, 0.01, y1);
				
				gl.glEnd();
			}
	}

}
