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
		if (obstacle.isFireSource)
			gl.glColor4d(0.5, 0, 0, 1);
		else
			gl.glColor4d(1, 1, 1, 1);
		Common.drawCuboid(gl, xmax - xmin, obstacle.height, ymax - ymin);
		gl.glPopMatrix();
	}

}
