package logic;

import java.util.ArrayList;

import interactor.Interactor;

public class ExpansionEquivalency extends Equivalency {

	private Framework exp, fstExpanded, sndExpanded;
	
	public ExpansionEquivalency(Framework fst, Framework snd, Framework exp, Interactor interactor) {
		super(fst,snd, interactor);
		this.exp = exp;
		expandFrameworks();
	}

	private void expandFrameworks() {
		ArrayList<Argument> fstArgExp, sndArgExp;
		ArrayList<Attack> fstAttExp, sndAttExp;
		
		fstArgExp = new ArrayList<Argument>();
		sndArgExp = new ArrayList<Argument>();
		fstAttExp = new ArrayList<Attack>();
		sndAttExp = new ArrayList<Attack>();
		
		fstArgExp.addAll(fst.getArguments());
		sndArgExp.addAll(snd.getArguments());
		fstAttExp.addAll(fst.getAttacks());
		sndAttExp.addAll(snd.getAttacks());
		
		for(Argument a: exp.getArguments()){
			if(!fst.contains(a)){
				fstArgExp.add(a);
			}
			if(!snd.contains(a)){
				sndArgExp.add(a);
			}
		}
		
		for(Attack a: exp.getAttacks()){
			if(!fst.contains(a)){
				fstAttExp.add(a);
			}
			if(!snd.contains(a)){
				sndAttExp.add(a);
			}
		}
		
		fstExpanded = new Framework(fstArgExp,fstAttExp,interactor);
		sndExpanded = new Framework(sndArgExp,sndAttExp,interactor);
	}

	//we might want to change the "test"
	private void setExpansion(Framework exp){
		this.exp = exp;
	}
	
	public boolean areExpansionEquivalent(int selectedIndex, boolean selected) {
		// TODO check for both expanded frameworks whether they are standard equivalent! (in super)
		return false;
	}

}
