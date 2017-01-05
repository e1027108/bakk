package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;

public class ExpansionEquivalency extends Equivalency {

	private Framework exp, fstExpanded, sndExpanded;
	
	public ExpansionEquivalency(Framework fst, Framework snd, Framework exp, Interactor interactor) {
		super(fst,snd,interactor);
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

	//we might want to change the expansion while computing (user), therefore needed
	private void setExpansion(Framework exp){
		this.exp = exp;
		expandFrameworks();
	}
	
	//TODO interact, distinguish expansiontypes
	public boolean areExpansionEquivalent(int extensionType, int expansionType, boolean usePrevious) throws InvalidInputException {
		ArrayList<Extension> fstExpExt, sndExpExt;
		String extName = "";
		//strong 1, normal 2, weak 3 for extensionType
		
		//this is for standard expansion equivalency TODO strong/weak expansion equivalencies --> outsource
		switch(extensionType){
		case 1:
			extName = "conflict-free sets";
			fstExpExt = fst.getConflictFreeSets();
			sndExpExt = snd.getConflictFreeSets();
			break;
		case 2:
			extName = "admissible extensions";
			fstExpExt = fst.getAdmissibleExtensions(usePrevious);
			sndExpExt = snd.getAdmissibleExtensions(usePrevious);
			break;
		case 3:
			extName = "complete extensions";
			fstExpExt = fst.getCompleteExtensions(usePrevious);
			sndExpExt = snd.getCompleteExtensions(usePrevious);
			break;
		case 4:
			extName = "preferred extensions";
			fstExpExt = fst.getPreferredExtensions(usePrevious);
			sndExpExt = snd.getPreferredExtensions(usePrevious);
			break;
		case 5:
			extName = "stable extensions";
			fstExpExt = fst.getStableExtensions(usePrevious);
			sndExpExt = snd.getStableExtensions(usePrevious);
			break;
		case 6:
			extName = "grounded extensions";
			fstExpExt = new ArrayList<Extension>();
			fstExpExt.add(fst.getGroundedExtension(usePrevious));
			sndExpExt = new ArrayList<Extension>();
			sndExpExt.add(snd.getGroundedExtension(usePrevious));
			break;
		case 7:
			extName = "semi-stable extensions";
			fstExpExt = fst.getSemiStableExtensions(usePrevious);
			sndExpExt = snd.getSemiStableExtensions(usePrevious);
			break;
		default:
			throw new InvalidInputException("No sematics for comparison chosen!");
		}
		
		return false;
	}

}
