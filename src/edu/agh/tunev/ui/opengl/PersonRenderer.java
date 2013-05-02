package edu.agh.tunev.ui.opengl;

import javax.media.opengl.GL2;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.AbstractModel.PersonState;
import edu.agh.tunev.model.AbstractPerson;

final class PersonRenderer implements Renderable {
	
	private final AbstractPerson person;
	private final AbstractModel<? extends AbstractPerson> model;
	
	public PersonRenderer(AbstractPerson person, AbstractModel<? extends AbstractPerson> model) {
		this.person = person;
		this.model = model;
	}

	@Override
	public void render(GL2 gl, double t) {
		// TODO Auto-generated method stub
		PersonState state = model.getPersonState(person, t);
		state.position.getX();
	}

}
