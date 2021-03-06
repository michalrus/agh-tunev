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

package edu.agh.tunev.world;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;

abstract class AbstractDataSource {
	
	abstract double getDuration();
	
	abstract Point2D.Double getDimension();
	
	abstract Vector<Exit> getExits();
	abstract Vector<Obstacle> getObstacles();
	abstract Vector<FireSource> getFireSources();
	
	abstract Physics getPhysicsAt(double t, Point2D.Double p);
	abstract Point2D.Double getPhysicsGranularity();
	
	abstract void readData(File from, World.ProgressCallback callback);

}
