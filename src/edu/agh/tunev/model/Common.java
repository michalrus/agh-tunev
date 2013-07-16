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

package edu.agh.tunev.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class Common {

	private final static int INTERSECTION_AREA_GRID_RESOLUTION = 1000;

	/**
	 * Normalizes any given angle in degrees to [0, 360).
	 * 
	 * @param angle
	 * @return
	 */
	public static double normalizeDeg(double angle) {
		return (360.0 + angle % 360.0) % 360.0;
	}
	
	public static double normalizeRad(double angle) {
		return Math.toRadians(normalizeDeg(Math.toDegrees(angle)));
	}

	/**
	 * Returns an angle between two given angles (on the narrower side), for
	 * which dist(ret,angle1) / dist(ret,angle2) == ratio.
	 * 
	 * Use ratio of 0.5 to bisect etc.
	 * 
	 * @param angle1
	 * @param angle2
	 * @param ratio
	 * @return
	 */
	public static double sectDeg(double angle1, double angle2, double ratio) {
		if (ratio < 0 || ratio > 1)
			throw new IllegalArgumentException("ratio must belong to [0;1]");

		final double a1 = normalizeDeg(angle1);
		final double a2 = normalizeDeg(angle2);

		final double min = Math.min(a1, a2);
		final double max = Math.max(a1, a2);

		final double diff = max - min;
		final double rdiff1 = normalizeDeg(diff);
		final double rdiff2 = normalizeDeg(-diff);

		if (rdiff1 < rdiff2)
			return normalizeDeg(min + rdiff1 * ratio);
		else
			return normalizeDeg(min - rdiff2 * ratio);
	}

	/**
	 * Creates an ellipse with given center point and rotation angle.
	 * 
	 * @param center
	 *            coordinates of the shape's center.
	 * @param a
	 *            original (pre-rotation) OX dimension.
	 * @param b
	 *            original (pre-rotation) OY dimension.
	 * @param deg
	 *            counter-clockwise rotation around the {@code center}.
	 * 
	 * @return Shape representing the ellipse.
	 */
	public static Shape createEllipse(Point2D.Double center, double width,
			double height, double deg) {
		return AffineTransform.getRotateInstance(deg * Math.PI / 180.0,
				center.x, center.y).createTransformedShape(
				new Ellipse2D.Double(center.x - width / 2, center.y - height
						/ 2, width, height));
	}

	/**
	 * Returns area of intersection of two Shapes.
	 */
	public static double intersectionArea(Shape s1, Shape s2) {
		// Below are the dimensions of a grid which the intersection of
		// s1 and s2 will be scaled to and drawn on.
		//
		// The more granular the grid, the longer it will take to calculate
		// the area.
		//
		// Complexity: O(w*h)
		
		final int w = INTERSECTION_AREA_GRID_RESOLUTION;
		final int h = INTERSECTION_AREA_GRID_RESOLUTION;
		
		// E.g. for grid of 5000x5000 and 2 identical ellipses 200x100:
		//
		// Shape e1 = ellipse(250, 250, 200, 100, 0);
		// Shape e2 = ellipse(250, 250, 200, 100, 0);
		//
		// This method returns:
		// 15711.904
		//
		// While analytical calculation:
		// 200/2*100/2*Math.PI == 15707.9632679489661923
		//
		// Relative error: -0.0251%
		//
		// Obviously 5000x5000 is a killer dimension, it takes 2 sec to
		// calculate. Not to mention used memory.
		//
		// Grid of 100x100 produces 0.66% error, still ok.
		//
		// 10x10 -> 10.87%

		final Area area = new Area(s1);
		area.intersect(new Area(s2));
		Rectangle2D bounds = area.getBounds2D();

		final double dx = bounds.getWidth() / w;
		final double dy = bounds.getHeight() / h;
		final double tx = -bounds.getMinX();
		final double ty = -bounds.getMinY();

		final AffineTransform at = AffineTransform.getScaleInstance(1.0 / dx,
				1.0 / dy);
		at.concatenate(AffineTransform.getTranslateInstance(tx, ty));

		java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
				w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);

		java.awt.Graphics2D g = image.createGraphics();
		g.setPaint(java.awt.Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.setPaint(java.awt.Color.BLACK);
		g.fill(at.createTransformedShape(area));

		int num = 0;

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if ((image.getRGB(i, j) & 0x00ffffff) == 0)
					num++;

		return dx * dy * num;
	}
	
	public static double ellipseArea(double width, double height){
		Shape ellipse = createEllipse(new Point2D.Double(0.0, 0.0), width, height, 0.0);
		return intersectionArea(ellipse, ellipse);
	}
	
	public static Point2D.Double getMiddlePointOfSegment(Point2D.Double start,
			Point2D.Double end){
		return new Point2D.Double((start.x + end.x)/2, (start.y + end.y)/2);
	}

	/**
	 * Finds the closest point on a line segment.
	 * 
	 * @param start
	 *            segment start point
	 * @param end
	 *            segment end point
	 * @param point
	 *            point to found the closest point on segment
	 * @return the closest point on a segment
	 */
	public static Point2D.Double getClosestPointOnSegment(Point2D.Double start,
			Point2D.Double end, Point2D.Double point) {
		double xDelta = end.x - start.x;
		double yDelta = end.y - start.y;

		if ((xDelta == 0) && (yDelta == 0)) {
			throw new IllegalArgumentException(
					"Segment start equals segment end");
		}

		double u = ((point.x - start.x) * xDelta + (point.y - start.y) * yDelta)
				/ (xDelta * xDelta + yDelta * yDelta);

		final Point2D.Double closestPoint;
		if (u < 0) {
			closestPoint = new Point2D.Double(start.x, start.y);
		} else if (u > 1) {
			closestPoint = new Point2D.Double(end.x, end.y);
		} else {
			closestPoint = new Point2D.Double(start.x + u * xDelta, start.y + u
					* yDelta);
		}

		return closestPoint;
	}

	/**
	 * Normal form of OXY line (as people see it).
	 * 
	 * See http://en.wikipedia.org/wiki/Line_(geometry)#Normal_form
	 */
	public static class LineNorm {
		/** Distance from (0,0) */
		public final double r;
		/** Angle between the line and OY (between r and OY) */
		public final double phi;

		public LineNorm(double r, double phi) {
			this.r = r;
			this.phi = phi;
		}

		/** Normal form of a line that contains p1 & p2 */
		public static LineNorm create(Point2D.Double p1, Point2D.Double p2) {
			final boolean vertical = equal(p1.x, p2.x);
			
			// Canonical form: Ax+By+C=0
			final double A = (vertical ? 1 : (p2.y - p1.y) / (p2.x - p1.x));
			final double B = (vertical ? 0 : -1);
			final double C = (vertical ? -p1.x : (p2.x*p1.y - p1.x*p2.y) / (p2.x - p1.x));
			
			// Normal form: x cosphi + y sinphi - r = 0
			final double r = Math.abs(C) / Math.sqrt(A * A + B * B);
			final double phi = (C < 0 ? Math.atan2(B, A) : Math.atan2(-B, -A));
			
			return new LineNorm(r, phi);
		}
		
		public boolean liesOn(LineNorm rhs, double rTolerance, double phiTolerance) {
			// same r?
			final boolean sameR = Math.abs(rhs.r - r) < rTolerance;
			
			if (!sameR)
				return false;
			
			// calculate normalized phi's of both lines
			final double phi1n = normalizeRad(phi);
			final double phi2n = normalizeRad(rhs.phi);

			// same phi?
			final double phiDiff = Math.abs(phi1n - phi2n);
			final boolean samePhi = phiDiff < phiTolerance;
			
			if (samePhi)
				return true;
			
			// if !samePhi and the lines both contain (0,0)... 
			if (equal(r, 0))
				// check if phiDiff is not 180*
				if (Math.abs(phiDiff - Math.PI) < phiTolerance)
					return true;
			
			return false;
		}
	}

	public static final double epsilon = 0.000001;
	/** Are two doubles equal with epsilon precision */
	public static boolean equal(double a, double b) {
		if (Math.abs(a - b) < epsilon)
			return true;
		return false;
	}
	
	public static boolean isValInRange(int b1, int b2, int val){
		int range = b1 - b2;
		int dist = b1 - val;
		double signRange = Math.signum(range);
		double signDist = Math.signum(dist);
		
		if(Math.abs(dist) < Math.abs(range) && signRange == signDist)
			return true;
		
		return false;
	}
	
	private Common() {
		// you shall not instantiate ^-^
	}

}
