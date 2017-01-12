package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;

public class Kernel extends Framework {

	private Type type;
	private Framework parent;
	
	//TODO interactor shall say something about kernel computation
	
	public Kernel(Framework parent, Interactor interactor, Type type) throws InvalidInputException {
		super(parent.getArguments(), parent.getAttacks(), interactor);
		this.parent = parent;
		this.type = type;
		computeKernel(this.type);
	}

	//all arguments stay in the kernel
	private void computeKernel(Type type) throws InvalidInputException {
		switch(type){
		case st:
			computeStableKernel();
			break;
		case ad:
			computeAdmissibleKernel();
			break;
		case gr:
			computeGroundedKernel();
			break;
		case co:
			computeCompleteKernel();
			break;
		default:
			throw new InvalidInputException("No kernel possible for the chosen semantic!"); //TODO check whether this is actually true for the others
		}
	}
	
	private void computeStableKernel() {
		// TODO all attacks except the attacks from self-attacking arguments (the self-attack stays)
		
	}
	
	private void computeAdmissibleKernel() {
		// TODO all attacks except the attacks from self-attacking arguments (the self-attack stays),
		// but only if the attacked doesn't attack back or doesn't also attack itself
		
	}
	
	private void computeGroundedKernel() {
		// TODO all attacks except the attacks on self-attacking arguments (a self-attack stays),
		// but only if the attacker doesn't attack itself or gets attacked back
		
	}
	
	private void computeCompleteKernel() {
		// TODO all attacks except the attacks of self-attacking arguments (the self-attack stays)
		// or the attacks on self-attacking arguments (self-attack stays here too)
		
	}

}
