package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;
import logic.Framework.Type;

public class ExpandedEquivalency extends Equivalency {

	private Framework exp, fstExpanded, sndExpanded;

	//TODO re-integrate class into Equivalency?
	
	public ExpandedEquivalency(Framework fst, Framework snd, Framework exp, Interactor interactor) {
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

	//this is for standard expansion equivalency on expanded frameworks, not for general expansion equivalency
	public boolean areExpandedEquivalent(int extensionType, boolean usePrevious) throws InvalidInputException {
		ArrayList<Extension> fstExpExt, sndExpExt;
		String extName = "";
		//strong 1, normal 2, weak 3 for extensionType

		if(fstExpanded == null || sndExpanded == null){
			throw new InvalidInputException("No expanded framework was created!");
		}

		switch(extensionType){
		case 1:
			extName = "conflict-free sets";
			fstExpExt = fstExpanded.getConflictFreeSets();
			sndExpExt = sndExpanded.getConflictFreeSets();
			break;
		case 2:
			extName = "admissible extensions";
			fstExpExt = fstExpanded.getAdmissibleExtensions(usePrevious);
			sndExpExt = sndExpanded.getAdmissibleExtensions(usePrevious);
			break;
		case 3:
			extName = "complete extensions";
			fstExpExt = fstExpanded.getCompleteExtensions(usePrevious);
			sndExpExt = sndExpanded.getCompleteExtensions(usePrevious);
			break;
		case 4:
			extName = "preferred extensions";
			fstExpExt = fstExpanded.getPreferredExtensions(usePrevious);
			sndExpExt = sndExpanded.getPreferredExtensions(usePrevious);
			break;
		case 5:
			extName = "stable extensions";
			fstExpExt = fstExpanded.getStableExtensions(usePrevious);
			sndExpExt = sndExpanded.getStableExtensions(usePrevious);
			break;
		case 6:
			extName = "grounded extensions";
			fstExpExt = new ArrayList<Extension>();
			fstExpExt.add(fstExpanded.getGroundedExtension(usePrevious));
			sndExpExt = new ArrayList<Extension>();
			sndExpExt.add(sndExpanded.getGroundedExtension(usePrevious));
			break;
		case 7:
			extName = "semi-stable extensions";
			fstExpExt = fstExpanded.getSemiStableExtensions(usePrevious);
			sndExpExt = sndExpanded.getSemiStableExtensions(usePrevious);
			break;
		default:
			throw new InvalidInputException("No sematics for comparison chosen!");
		}

		if (areExtensionListsEqual(fstExpExt,sndExpExt)){
			//TODO interact here
			return true;
		}

		return false;
	}
	
	public boolean checkStrongExpansionEquivalency(Type type, boolean usePrevious) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkNormalExpansionEquivalency(Type type, boolean usePrevious) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkWeakExpansionEquivalency(Type type, boolean usePrevious) {
		// TODO Auto-generated method stub
		return false;
	}

}
