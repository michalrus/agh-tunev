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
import java.util.Vector;

import javax.media.opengl.GL2;

import edu.agh.tunev.model.Common.LineNorm;
import edu.agh.tunev.world.Exit;
import edu.agh.tunev.world.World;

final class WallsRenderer implements Renderable {

	private Vector<Wall> walls = new Vector<Wall>();

	private class Wall {
		public final LineNorm line;
		public final double length;
		public final Point2D.Double p1, p2;

		public Wall(Point2D.Double p1, Point2D.Double p2) {
			this.line = LineNorm.create(p1, p2);
			this.p1 = p1;
			this.p2 = p2;
			length = p1.distance(p2);
		}

		/**
		 * Creates new Wall as a segment of an old one. 
		 * 
		 * @param init old Wall
		 * @param start start position
		 * @param end end position
		 */
		public Wall(Wall init, double start, double end) {
			this.line = init.line;
			this.p1 = new Point2D.Double(init.p1.x + start/init.length * (init.p2.x - init.p1.x),
					init.p1.y + start/init.length * (init.p2.y - init.p1.y));
			this.p2 = new Point2D.Double(init.p1.x + end/init.length * (init.p2.x - init.p1.x),
					init.p1.y + end/init.length * (init.p2.y - init.p1.y));
			this.length = Math.abs(end - start);
		}

		public Vector<Wall> cutout(Exit exit) {
			Vector<Wall> walls = new Vector<Wall>();

			if (!LineNorm.create(exit.p1, exit.p2).liesOn(line, 0.01, 0.01)) {
				// if the exit and the line are not on the same line
				walls.add(this);
				return walls;
			}

			// translate exit by -wall.p1
			// and rotate by -(wall.phi-90*)
			final double angle = -(line.phi - Math.toRadians(90));
			// the exit will lie on OX then			
			final Point2D.Double tep1 = new Point2D.Double(
					exit.p1.x - p1.x, exit.p1.y - p1.y);
			final Point2D.Double rep1 = Common.rotate(tep1, angle);
			final Point2D.Double tep2 = new Point2D.Double(
					exit.p2.x - p1.x, exit.p2.y - p1.y);
			final Point2D.Double rep2 = Common.rotate(tep2, angle);
			
			// do the same thing with the wall
			//final Point2D.Double twp1 = new Point2D.Double(
			//		0, 0);
			//final Point2D.Double rwp1 = Common.rotate(twp1, angle);
			final Point2D.Double twp2 = new Point2D.Double(
					p2.x - p1.x, p2.y - p1.y);
			final Point2D.Double rwp2 = Common.rotate(twp2, angle);

			final double tmpx1 = (rwp2.x > 0 ? rep1.x : -rep1.x);
			final double tmpx2 = (rwp2.x > 0 ? rep2.x : -rep2.x);
			final double ex1 = Math.min(tmpx1, tmpx2);
			final double ex2 = Math.max(tmpx1, tmpx2);

			// left and right ends of Wall (only convention)
			final double lx = 0 + Common.epsilon;
			final double rx = Math.abs(rwp2.x) - Common.epsilon;

			if (ex1 < lx) { // 1. first exit point before wall
				if (ex2 < lx) // 1.a. second exit point before wall
					walls.add(this);
				else if (ex2 < rx) // 1.b. second exit point inside wall
					walls.add(new Wall(this, ex2, length));
				else // 1.c. second after wall (total destruction)
					;
			}
			else if (ex1 < rx) { // 2. first exit point inside wall
				if (ex2 < rx) { // 2.a. second inside wall
					walls.add(new Wall(this, 0, ex1));
					walls.add(new Wall(this, ex2, length));
				}
				else // 2.b. second after wall
					walls.add(new Wall(this, 0, ex1));
			}
			else // 3. both after wall
				walls.add(this);

			return walls;
		}
	}

	public WallsRenderer(World world) {
		final Point2D.Double dim = world.getDimension();

		// initial walls
		walls.add(new Wall(new Point2D.Double(0, 0), new Point2D.Double(0,
				dim.y)));
		walls.add(new Wall(new Point2D.Double(0, dim.y), new Point2D.Double(
				dim.x, dim.y)));
		walls.add(new Wall(new Point2D.Double(dim.x, dim.y),
				new Point2D.Double(dim.x, 0)));
		walls.add(new Wall(new Point2D.Double(dim.x, 0), new Point2D.Double(0,
				0)));

		// cut the exits out
		for (Exit exit : world.getExits()) {
			Vector<Wall> newWalls = new Vector<Wall>();
			for (Wall wall : walls)
				newWalls.addAll(wall.cutout(exit));
			walls = newWalls;
		}
	}

	private static final double thickness = 0.3;
	private static final double height = 2.0;
	/** Wall-floor overlap (relative to Wall thickness) */
	private static final double overlap = 0.1;

	@Override
	public void render(GL2 gl, double t) {
		gl.glPushMatrix();
		gl.glColor4d(1, 1, 1, 1);

		for (Wall wall : walls)
			drawWall(gl, wall.p1, wall.p2);

		gl.glPopMatrix();
	}

	/** draws Wall between two World points (x,y) */
	private void drawWall(GL2 gl, Point2D.Double a, Point2D.Double b) {
		gl.glPushMatrix();

		gl.glTranslated(a.x, 0, a.y);
		final Point2D.Double r = new Point2D.Double(b.x - a.x, b.y - a.y);
		gl.glRotated(Math.toDegrees(Math.atan2(r.x, r.y)), 0, 1, 0);
		gl.glTranslated(-thickness * (1 - overlap), 0, 0);
		Common.drawCuboid(gl, thickness, height, r.distance(0, 0));

		gl.glPopMatrix();
	}

}
