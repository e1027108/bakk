package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;

public class Equivalency {
	protected Framework fst, snd; //first and second frameworks (for comparison)
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
	}
	
	//based on baumann 3.1 p8 "standard equivalence"
	//TODO use interactor & return boolean
	public boolean areStandardEquivalent(int type, boolean usePrevious) throws InvalidInputException{
		ArrayList<Extension> fstExt, sndExt;
		String extName = "";
		
		switch(type){
		case 1:
			extName = "conflict-free sets";
			fstExt = fst.getConflictFreeSets();
			sndExt = snd.getConflictFreeSets();
			break;
		case 2:
			extName = "admissible extensions";
			fstExt = fst.getAdmissibleExtensions(usePrevious);
			sndExt = snd.getAdmissibleExtensions(usePrevious);
			break;
		case 3:
			extName = "complete extensions";
			fstExt = fst.getCompleteExtensions(usePrevious);
			sndExt = snd.getCompleteExtensions(usePrevious);
			break;
		case 4:
			extName = "preferred extensions";
			fstExt = fst.getPreferredExtensions(usePrevious);
			sndExt = snd.getPreferredExtensions(usePrevious);
			break;
		case 5:
			extName = "stable extensions";
			fstExt = fst.getStableExtensions(usePrevious);
			sndExt = snd.getStableExtensions(usePrevious);
			break;
		case 6:
			extName = "grounded extensions";
			fstExt = new ArrayList<Extension>();
			fstExt.add(fst.getGroundedExtension(usePrevious));
			sndExt = new ArrayList<Extension>();
			sndExt.add(snd.getGroundedExtension(usePrevious));
			break;
		case 7:
			extName = "semi-stable extensions";
			fstExt = fst.getSemiStableExtensions(usePrevious);
			sndExt = snd.getSemiStableExtensions(usePrevious);
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
}
