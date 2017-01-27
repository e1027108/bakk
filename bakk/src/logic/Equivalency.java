package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;
import logic.Framework.Type;

public class Equivalency {
	protected Framework fst, snd, exp, fstExpanded, sndExpanded;; //first and second frameworks (for comparison) and expansion options
	protected Interactor interactor;
	
	public Equivalency(Framework fst, Framework snd, Interactor interactor){
		this.fst = fst;
		this.snd = snd;
		this.interactor = interactor;
		//TODO change interactor behaviour (subclass?)
	}
	
	//one might want to replace this to not create another equivalency all the time
	public void setSecondFramework(Framework snd){
		this.snd = snd;
		if(fstExpanded != null || sndExpanded != null){ //re-expand new
			expandFrameworks(this.exp);
		}
	}
	
	//based on baumann 3.1 p8 "standard equivalence"
	//TODO use interactor & return boolean
	public boolean areStandardEquivalent(int type, boolean usePrevious) throws InvalidInputException{
		ArrayList<Extension> fstExt, sndExt;
		Framework fstUsed, sndUsed;
		
		if(fstExpanded != null && sndExpanded != null){
			fstUsed = fstExpanded;
			sndUsed = sndExpanded;
		}
		else{
			fstUsed = fst;
			sndUsed = snd;
		}
		
		String extName = "";
		
		switch(type){
		case 1:
			extName = "conflict-free sets";
			fstExt = fstUsed.getConflictFreeSets();
			sndExt = sndUsed.getConflictFreeSets();
			break;
		case 2:
			extName = "admissible extensions";
			fstExt = fstUsed.getAdmissibleExtensions(usePrevious);
			sndExt = sndUsed.getAdmissibleExtensions(usePrevious);
			break;
		case 3:
			extName = "complete extensions";
			fstExt = fstUsed.getCompleteExtensions(usePrevious);
			sndExt = sndUsed.getCompleteExtensions(usePrevious);
			break;
		case 4:
			extName = "preferred extensions";
			fstExt = fstUsed.getPreferredExtensions(usePrevious);
			sndExt = sndUsed.getPreferredExtensions(usePrevious);
			break;
		case 5:
			extName = "stable extensions";
			fstExt = fstUsed.getStableExtensions(usePrevious);
			sndExt = sndUsed.getStableExtensions(usePrevious);
			break;
		case 6:
			extName = "grounded extensions";
			fstExt = new ArrayList<Extension>();
			fstExt.add(fstUsed.getGroundedExtension(usePrevious));
			sndExt = new ArrayList<Extension>();
			sndExt.add(sndUsed.getGroundedExtension(usePrevious));
			break;
		case 7:
			extName = "semi-stable extensions";
			fstExt = fstUsed.getSemiStableExtensions(usePrevious);
			sndExt = sndUsed.getSemiStableExtensions(usePrevious);
			break;
		default:
			throw new InvalidInputException("No sematics for comparison chosen!");
		}
		
		if(areExtensionListsEqual(fstExt, sndExt)){
			//TODO interactor writes they have the same extensions, nicely formatted
			return true;
		}
		else{
			return false;
		}
	}
	
	//TODO let interactor get some
	protected boolean areExtensionListsEqual(ArrayList<Extension> first, ArrayList<Extension> second){
		ArrayList<Extension> fstExt = new ArrayList<Extension>();
		ArrayList<Extension> sndExt = new ArrayList<Extension>();
		boolean found;
		
		fstExt.addAll(first);
		sndExt.addAll(second);
		
		if(fstExt.size() == 0 && sndExt.size() == 0){
			return true;
		}
		else if(fstExt.size() != sndExt.size()){
			return false; //TODO special message
		}
		
		for(int f = fstExt.size() - 1; f >= 0; f--){
			found = false;
			
			for(int s = sndExt.size()-1; s >= 0; s--){
				if(fstExt.get(f).equals(sndExt.get(s))){
					fstExt.remove(f);
					sndExt.remove(s);
					found = true;
					break;
				}
			}
			
			if(!found){
				return false;
			}
			
		}
		
		if(sndExt.size() > 0){
			return false;
		}
		
		return true;
	}
	
	protected void printAll(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println(e.format());
		}
	}

	//TODO test, TODO add weak and normal? (irrelevant?), add local?
	public boolean areExpansionEquivalent(Type type, boolean usePrevious) throws InvalidInputException {
		Framework fstUsed, sndUsed;
		
		//ensures expansion is used if exists
		if(fstExpanded != null && sndExpanded != null){
			fstUsed = fstExpanded;
			sndUsed = sndExpanded;
		}
		else{
			fstUsed = fst;
			sndUsed = snd;
		}
		
		if(type == Type.ss){ //semi-stable needs equivalent admissible kernels
			return new Equivalency(fstUsed.getKernel(Type.ad),sndUsed.getKernel(Type.ad),interactor).areStandardEquivalent(7, usePrevious);
		}
		else if(type == Type.st){ //stable needs equivalent stable kernels (5)
			return new Equivalency(fstUsed.getKernel(type),sndUsed.getKernel(type),interactor).areStandardEquivalent(5, usePrevious);
		}
		else if(type == Type.ad || type == Type.pr){ //admissible and preferred need adstar (?2?)
			int num;
			if(type == Type.ad){
				num = 2;
			}
			else{
				num = 4;
			}
			return new Equivalency(fstUsed.getKernel(Type.adstar),sndUsed.getKernel(Type.adstar),interactor).areStandardEquivalent(num, usePrevious);
		}
		else if(type == type.gr){
			return new Equivalency(fstUsed.getKernel(Type.grstar),sndUsed.getKernel(Type.grstar),interactor).areStandardEquivalent(6, usePrevious);
		}
		else if(type == type.co){
			return new Equivalency(fstUsed.getKernel(Type.costar),sndUsed.getKernel(Type.costar),interactor).areStandardEquivalent(3, usePrevious);
		}
		else{
			throw new InvalidInputException("There is no strong equivalency relation defined for this semantics.");
		}
	}
	
	public void expandFrameworks(Framework exp) {
		this.exp = exp;
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
}
