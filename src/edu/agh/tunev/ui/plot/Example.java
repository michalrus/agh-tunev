package edu.agh.tunev.ui.plot;

public final class Example extends AbstractPlot {

	public static final String PLOT_NAME = "Przykładowy wykres";

	@Override
	public String getName() {
		return PLOT_NAME;
	}

	public Example(int modelNumber, String modelName, int plotNumber) {
		super(modelNumber, modelName, plotNumber, Example.class);
	}

	private static final long serialVersionUID = 1L;

}
