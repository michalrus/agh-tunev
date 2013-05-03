package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.model.PersonState;

final class PersonRenderer implements Renderable {
	
	private final PersonProfile person;
	private final AbstractModel model;
	
	public PersonRenderer(PersonProfile person, AbstractModel model) {
		this.person = person;
		this.model = model;
	}

	@Override
	public void render(GL2 gl, double t) {
		// TODO Auto-generated method stub
		PersonState state = model.getPersonState(person, t);
		
		if (state == null)
			return;
		
		state.position.getX();
	}

}
