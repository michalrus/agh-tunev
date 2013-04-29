package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

interface AbstractRenderer {

	abstract public void render(GL2 gl, double t);

}
