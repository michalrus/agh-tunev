package edu.agh.tunev.world;

import java.io.File;
import java.util.Vector;

abstract class AbstractDataSource {
	
	abstract double getDuration();
	
	abstract double getXDimension();
	abstract double getYDimension();
	
	abstract Vector<Exit> getExits();
	abstract Vector<Obstacle> getObstacles();
	abstract Physics getPhysicsAt(double t, double x, double y);
	
	abstract void readData(File from, World.ProgressCallback callback);

}
