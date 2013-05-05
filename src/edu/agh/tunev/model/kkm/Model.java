package edu.agh.tunev.model.kkm;

import java.util.Vector;

import edu.agh.tunev.model.AbstractModel;
import edu.agh.tunev.model.PersonProfile;
import edu.agh.tunev.statistics.Statistics.AddCallback;
import edu.agh.tunev.world.World;
import edu.agh.tunev.world.World.ProgressCallback;

public final class Model extends AbstractModel {

	public final static String MODEL_NAME = "Autorski (\u00a9 2012 Jakub Rakoczy)";

	public Model(World world) {
		super(world);
	}

	@Override
	public void simulate(double duration, Vector<PersonProfile> profiles,
			ProgressCallback progressCallback, AddCallback addCallback) {
		// TODO Auto-generated method stub

	}

}
