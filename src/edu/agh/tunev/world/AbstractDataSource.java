package edu.agh.tunev.world;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;

abstract class AbstractDataSource {
	
	abstract double getDuration();
	
	abstract Point2D.Double getDimension();
	
	abstract Vector<Exit> getExits();
	abstract Vector<Obstacle> getObstacles();
	
	abstract Physics getPhysicsAt(double t, Point2D.Double p);
	abstract Point2D.Double getPhysicsGranularity();
	
	abstract void readData(File from, World.ProgressCallback callback);

}
