package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Command;
import interactor.Interactor;
import javafx.scene.paint.Color;
import logic.Framework.Type;

public class Equivalency {
	protected Framework fst, snd, exp, fstExpanded, sndExpanded;; //first and second frameworks (for comparison) and expansion options
	protected Interactor interactor;

	public Equivalency(Framework fst, Framework snd, Interactor interactor){
		this.fst = fst;
		this.snd = snd;
		this.interactor = interactor;
	}

	//one might want to replace this to not create another equivalency all the time
	public void setSecondFramework(Framework snd){
		this.snd = snd;
		if(fstExpanded != null || sndExpanded != null){ //re-expand new
			expandFrameworks(this.exp);
		}
	}

	//based on baumann 3.1 p8 "standard equivalence"
	public ArrayList<Extension> areStandardEquivalent(int type, boolean usePrevious) throws InvalidInputException{
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

		String framedesc = "Framework";

		switch(type){
		case 1:
			extName = "conflict-free sets";
			fstExt = fstUsed.getConflictFreeSets();
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getConflictFreeSets();
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 2:
			extName = "admissible extensions";
			fstExt = fstUsed.getAdmissibleExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getAdmissibleExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 3:
			extName = "complete extensions";
			fstExt = fstUsed.getCompleteExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getCompleteExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 4:
			extName = "preferred extensions";
			fstExt = fstUsed.getPreferredExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getPreferredExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 5:
			extName = "stable extensions";
			fstExt = fstUsed.getStableExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getStableExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 6:
			extName = "grounded extensions";
			fstExt = new ArrayList<Extension>();
			fstExt.add(fstUsed.getGroundedExtension(usePrevious));
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = new ArrayList<Extension>();
			sndExt.add(sndUsed.getGroundedExtension(usePrevious));
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		case 7:
			extName = "semi-stable extensions";
			fstExt = fstUsed.getSemiStableExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " A has the following " + extName + ": " + Framework.formatExtensions(fstExt) + ".",null,1));
			sndExt = sndUsed.getSemiStableExtensions(usePrevious);
			interactor.addToCommands(new Command(framedesc + " B has the following " + extName + ": " + Framework.formatExtensions(sndExt) + ".",null,2));
			break;
		default:
			throw new InvalidInputException("No sematics for comparison chosen!");
		}

		if(areExtensionListsEqual(fstExt, sndExt)){
			interactor.addToCommands(new Command("Both " + framedesc.toLowerCase() + "s have equal " + extName + ". They are standard equivalent!",null,0));
			return fstExt; //is the same as sndExt anyway
		}
		else{
			return null;
		}
	}

	protected boolean areExtensionListsEqual(ArrayList<Extension> first, ArrayList<Extension> second){
		ArrayList<Extension> fstExt = new ArrayList<Extension>();
		ArrayList<Extension> sndExt = new ArrayList<Extension>();
		boolean found;

		String framedesc = "Framework";

		fstExt.addAll(first);
		sndExt.addAll(second);

		if(fstExt.size() == 0 && sndExt.size() == 0){
			interactor.addToCommands(new Command("Both " + framedesc.toLowerCase() + "s do not have extensions for this semantics.",null,0));
			return true;
		}
		else if(fstExt.size() != sndExt.size()){
			interactor.addToCommands(new Command("The " + framedesc.toLowerCase() + "s do not have the same amounts of extensions (" + fstExt.size() + " and " + sndExt.size() + "extensions). "
					+ "They can not be equivalent.", null, 0));
			return false;
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
				interactor.addToCommands(new Command("Since " + framedesc + " A contains the extensions " + fstExt.get(f).format() + " and " + framedesc + " B does not, the " + framedesc +
						"s are not equivalent!",fstExt.get(f).toInstruction(Color.RED), 1));
				return false;
			}

		}

		if(sndExt.size() > 0){
			interactor.addToCommands(new Command("Since " + framedesc + " B contains the extensions " + Framework.formatExtensions(sndExt) + " which " + framedesc + " A does not contain, the " + 
					framedesc + "s are not equivalent!",null,0));
			return false;
		}

		return true;
	}

	protected void printAll(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println(e.format());
		}
	}

	//TODO add weak and normal? (irrelevant?), add local?
	public boolean areExpansionEquivalent(Type type, boolean usePrevious) throws InvalidInputException {
		Framework fstUsed, sndUsed;
		Kernel k1, k2;
		boolean kernelEquality;
		String equalSem;

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
			equalSem = "semi-stable";
			interactor.addToCommands(new Command("We compare whether the admissible kernels are equal to see if the frameworks are equivalent wrt semi-stable semantics.",null,0));
			//return new Equivalency(fstUsed.getKernel(Type.ad, usePrevious),sndUsed.getKernel(Type.ad, usePrevious),interactor).areStandardEquivalent(7, usePrevious);
			k1 = fstUsed.getKernel(Type.ad, usePrevious);
			k2 = sndUsed.getKernel(Type.ad, usePrevious);

			interactor.addToCommands(new Command("Kernel A contains arguments " + Framework.formatArgumentList(k1.getArguments()) + " and attacks " + Framework.formatAttackList(k1.getAttacks()), 
					k1.toInstruction(Color.GREEN),1));
			interactor.addToCommands(new Command("Kernel B contains arguments " + Framework.formatArgumentList(k2.getArguments()) + " and attacks " + Framework.formatAttackList(k2.getAttacks()), 
					k2.toInstruction(Color.GREEN),2));

			kernelEquality = checkKernelEquality(k1,k2);
		}
		else if(type == Type.st){ //stable needs equivalent stable kernels (5)
			equalSem = "stable";
			interactor.addToCommands(new Command("We compare whether the stable kernels are equal to see if the frameworks are equivalent wrt stable semantics.",null,0));
			//return new Equivalency(fstUsed.getKernel(type, usePrevious),sndUsed.getKernel(type, usePrevious),interactor).areStandardEquivalent(5, usePrevious);
			k1 = fstUsed.getKernel(Type.st, usePrevious);
			k2 = sndUsed.getKernel(Type.st, usePrevious);

			interactor.addToCommands(new Command("Kernel A contains arguments " + Framework.formatArgumentList(k1.getArguments()) + " and attacks " + Framework.formatAttackList(k1.getAttacks()), 
					k1.toInstruction(Color.GREEN),1));
			interactor.addToCommands(new Command("Kernel B contains arguments " + Framework.formatArgumentList(k2.getArguments()) + " and attacks " + Framework.formatAttackList(k2.getAttacks()), 
					k2.toInstruction(Color.GREEN),2));

			kernelEquality = checkKernelEquality(k1,k2);
		}
		else if(type == Type.ad || type == Type.pr){ //admissible and preferred need adstar
			//int num;
			if(type == Type.ad){
				equalSem = "admissible";
				interactor.addToCommands(new Command("We compare whether the admissible-* kernels are equal to see if the frameworks are equivalent wrt admissible semantics.",null,0));
				//num = 2;
			}
			else{
				equalSem = "preferred";
				interactor.addToCommands(new Command("We compare whether the admissible-* kernels are equal to see if the frameworks are equivalent wrt preferred semantics.",null,0));
				//num = 4;
			}
			//return new Equivalency(fstUsed.getKernel(Type.adstar, usePrevious),sndUsed.getKernel(Type.adstar, usePrevious),interactor).areStandardEquivalent(num, usePrevious);
			k1 = fstUsed.getKernel(Type.adstar, usePrevious);
			k2 = sndUsed.getKernel(Type.adstar, usePrevious);

			interactor.addToCommands(new Command("Kernel A contains arguments " + Framework.formatArgumentList(k1.getArguments()) + " and attacks " + Framework.formatAttackList(k1.getAttacks()), 
					k1.toInstruction(Color.GREEN),1));
			interactor.addToCommands(new Command("Kernel B contains arguments " + Framework.formatArgumentList(k2.getArguments()) + " and attacks " + Framework.formatAttackList(k2.getAttacks()), 
					k2.toInstruction(Color.GREEN),2));

			kernelEquality = checkKernelEquality(k1,k2);
		}
		else if(type == Type.gr){
			equalSem = "grounded";
			interactor.addToCommands(new Command("We compare whether the grounded-* kernels are equal to see if the frameworks are equivalent wrt grounded semantics.",null,0));
			//return new Equivalency(fstUsed.getKernel(Type.grstar, usePrevious),sndUsed.getKernel(Type.grstar, usePrevious),interactor).areStandardEquivalent(6, usePrevious);
			k1 = fstUsed.getKernel(Type.grstar, usePrevious);
			k2 = sndUsed.getKernel(Type.grstar, usePrevious);

			interactor.addToCommands(new Command("Kernel A contains arguments " + Framework.formatArgumentList(k1.getArguments()) + " and attacks " + Framework.formatAttackList(k1.getAttacks()), 
					k1.toInstruction(Color.GREEN),1));
			interactor.addToCommands(new Command("Kernel B contains arguments " + Framework.formatArgumentList(k2.getArguments()) + " and attacks " + Framework.formatAttackList(k2.getAttacks()), 
					k2.toInstruction(Color.GREEN),2));

			kernelEquality = checkKernelEquality(k1,k2);
		}
		else if(type == Type.co){
			equalSem = "complete";
			interactor.addToCommands(new Command("We compare whether the complete-* kernels are equal to see if the frameworks are equivalent wrt complete semantics.",null,0));
			//return new Equivalency(fstUsed.getKernel(Type.costar, usePrevious),sndUsed.getKernel(Type.costar, usePrevious),interactor).areStandardEquivalent(3, usePrevious);
			k1 = fstUsed.getKernel(Type.costar, usePrevious);
			k2 = sndUsed.getKernel(Type.costar, usePrevious);

			interactor.addToCommands(new Command("Kernel A contains arguments " + Framework.formatArgumentList(k1.getArguments()) + " and attacks " + Framework.formatAttackList(k1.getAttacks()), 
					k1.toInstruction(Color.GREEN),1));
			interactor.addToCommands(new Command("Kernel B contains arguments " + Framework.formatArgumentList(k2.getArguments()) + " and attacks " + Framework.formatAttackList(k2.getAttacks()), 
					k2.toInstruction(Color.GREEN),2));

			kernelEquality = checkKernelEquality(k1,k2);
		}
		else{
			throw new InvalidInputException("There is no strong equivalency relation defined for this semantics.");
		}
		
		if(kernelEquality){
			interactor.addToCommands(new Command("The frameworks are equivalent wrt " + equalSem + " semantics.",null,0));
		}
		else{
			interactor.addToCommands(new Command("The frameworks are not equivalent wrt " + equalSem + " semantics.",null,0));
		}
		
		return kernelEquality;
	}

	public void expandFrameworks(Framework exp) {
		fstExpanded = Framework.expandFramework(fst,exp);
		sndExpanded = Framework.expandFramework(snd,exp);
	}

	private boolean checkKernelEquality(Kernel k1, Kernel k2){
		if(k1.getArguments().size() != k2.getArguments().size()){
			interactor.addToCommands(new Command("Since the kernels do not contain the same amount of arguments, they are not equal",k1.toInstruction(Color.BLACK),1));
			return false;
		}
		else if(k1.getAttacks().size() != k2.getAttacks().size()){
			interactor.addToCommands(new Command("Since the kernels do not contain the same amount of attacks, they are not equal",k1.toInstruction(Color.BLACK),1));
			return false;
		}
		else{
			ArrayList<Argument> argdiff = allArgumentsContained(k1.getArguments(),k2.getArguments());
			ArrayList<Attack> attdiff = allAttacksContained(k1.getAttacks(),k2.getAttacks());
			
			if(!argdiff.isEmpty()){
				interactor.addToCommands(new Command("Since the kernel B does not contain the arguments " + Framework.formatArgumentList(argdiff) + ", the kernels are not equal.",k1.toInstruction(Color.BLACK),1));
				return false;
			}
			else if(!attdiff.isEmpty()){
				interactor.addToCommands(new Command("Since the kernel B does not contain the attacks " + Framework.formatAttackList(attdiff) + ", the kernels are not equal.",k1.toInstruction(Color.BLACK),1));
				return false;
			}
			else{
				interactor.addToCommands(new Command("The kernels are equal.",null,0));
				return true;
			}
		}
	}

	private ArrayList<Argument> allArgumentsContained(ArrayList<Argument> list1, ArrayList<Argument> list2){
		ArrayList<Argument> missing = new ArrayList<Argument>();
		
		for(Argument a: list1){
			boolean cont = false;
			for(Argument b: list2){
				if(a.equals(b)){
					cont = true;
				}
			}
			if(!cont){
				missing.add(a);
			}
		}
		
		return missing;
	}

	private ArrayList<Attack> allAttacksContained(ArrayList<Attack> list1, ArrayList<Attack> list2){
		ArrayList<Attack> missing = new ArrayList<Attack>();
		
		for(Attack a: list1){
			boolean cont = false;
			for(Attack b: list2){
				if(a.equals(b)){
					cont = true;
				}
			}
			if(!cont){
				missing.add(a);
			}
		}
		
		return missing;
	}
}
