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

		if(fstExpanded == null || sndExpanded == null){
			throw new InvalidInputException("No expanded framework was created!");
		}

		//TODO figure out if/how to deal with general form expansion equivalency (E-Mail?)
		//refactor start

		/**
		switch(expansionType){
		case 1:
			areStrongExpansionEquivalent(extensionType,usePrevious);
			break;
		case 2:
			areNormalExpansionEquivalent(extensionType,usePrevious);
			break;
		case 3:
			areWeakExpansionEquivalent(extensionType,usePrevious);
			break;
		default:
			throw new InvalidInputException("No expansion type chosen!");
		}
		**/

		//refactor end

		//this is for standard expansion equivalency TODO strong/weak expansion equivalencies --> outsource
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

}
