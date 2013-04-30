package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

interface Renderable {

	abstract public void render(GL2 gl, double t);

}
