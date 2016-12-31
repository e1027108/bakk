package logic;

import interactor.Interactor;

public class ExpansionEquivalency extends Equivalency {

	private Framework exp;
	
	public ExpansionEquivalency(Framework fst, Framework snd, Framework exp, Interactor interactor) {
		super(fst,snd, interactor);
		this.exp = exp;
	}
	
	//we might want to change the "test"
	private void setExpansion(Framework exp){
		this.exp = exp;
	}
	
	public boolean areExpansionEquivalent(int selectedIndex, boolean selected) {
		// TODO create type methods, switch between them here
		return false;
	}

}
