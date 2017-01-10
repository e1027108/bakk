package logic;

import java.util.ArrayList;

import interactor.Interactor;

public class Kernel extends Framework {

	private Type type;
	
	public Kernel(ArrayList<Argument> arguments, ArrayList<Attack> attacks, Interactor interactor, Type type) {
		super(arguments, attacks, interactor);
		this.type = type;
	}

}
