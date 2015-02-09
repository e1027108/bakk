package logic;

import java.util.ArrayList;

public class Framework {
	private ArrayList<Argument> arguments;
	private ArrayList<Extension> previousConflictFreeSets; //TODO check for null everytime it's used

	public Framework(ArrayList<Argument> arguments){
		this.arguments = arguments;
	}

	private ArrayList<Argument> getAttacks(char argumentName){
		ArrayList<Argument> attacks = new ArrayList<Argument>();
		String attackString = getArgument(argumentName).getAttacks();

		for(int i = 0;i<attackString.length();i++){
			attacks.add(getArgument(attackString.charAt(i)));
		}

		return attacks;
	}

	public Argument getArgument(char argumentName){
		if(arguments != null){
			for(Argument a: arguments){
				if(a.getName() == argumentName){
					return a;
				}
			}
		}

		return null;
	}

	public ArrayList<Argument> getAttackers(char argumentName){
		ArrayList<Argument> attackers = new ArrayList<Argument>();

		if(arguments != null){
			for(Argument a: arguments){
				if(a.getAttacks().contains(String.valueOf(argumentName))){
					attackers.add(a);
				}
			}
			return attackers;
		}

		return null;
	}

	public ArrayList<Extension> getConflictFreeSets(){
		ArrayList<Extension> conflictFreeSets = new ArrayList<Extension>();

		//the list, but not the elements will be changed, so no deep copy needed
		ArrayList<Argument> workingSet = new ArrayList<Argument>();
		workingSet.addAll(arguments);

		if(arguments == null){
			return null;
		}
		
		for(Argument a: arguments){
			workingSet.remove(a); //doesn't need to contain the argument, because it is provided seperately
			ArrayList<Argument> nonConflictingSet = getNonConflicting(workingSet, a); //gets every argument a doesn't conflict with
			ArrayList<Extension> tmpConflictFreeSubSets = getConflictFreeSubSets(nonConflictingSet, a); //gets all conflict-free sets of the provided subsets containing a
			if(tmpConflictFreeSubSets != null && !tmpConflictFreeSubSets.isEmpty()){
				conflictFreeSets.addAll(tmpConflictFreeSubSets); //adds the conflict-free sets for an argument to the overall list
			}
		}

		conflictFreeSets.add(new Extension(new ArrayList<Argument>()));
		
		previousConflictFreeSets = new ArrayList<Extension>();
		previousConflictFreeSets.addAll(conflictFreeSets);
		
		return conflictFreeSets;
	}

	private ArrayList<Argument> getNonConflicting(ArrayList<Argument> workingSet, Argument arg) {
		ArrayList<Argument> nonConflicting = new ArrayList<Argument>();
		String attacks = arg.getAttacks();

		for(Argument a: workingSet){
			if(!a.getAttacks().contains(String.valueOf(arg.getName())) && !attacks.contains(String.valueOf(a.getName()))){
				nonConflicting.add(a);
			}
		}

		return nonConflicting;
	}

	private ArrayList<Extension> getConflictFreeSubSets(ArrayList<Argument> nonConflictingSet, Argument arg) {
		ArrayList<ArrayList<Argument>> subSets = getAllSubsets(nonConflictingSet);
		ArrayList<Extension> conflictFree = new ArrayList<Extension>();
		
		for(ArrayList<Argument> s: subSets){
			s.add(arg);
			Extension e = new Extension(s);
			if(e.isConflictFree()){
				conflictFree.add(e);
			}
		}
		
		return conflictFree;
	}

	private ArrayList<ArrayList<Argument>> getAllSubsets(ArrayList<Argument> set){
		ArrayList<ArrayList<Argument>> powerSet = new ArrayList<ArrayList<Argument>>();
		int elements = set.size();
		
		for(int i = 0; i < Math.pow(2, elements); i++){ //there are 2^n subsets
			ArrayList<Argument> subset = new ArrayList<Argument>();
			for(int j = 0; j < elements; j++){
				if(((i>>j) & 1) == 1){ //checking every bit (at point of checking least significant bit of shifting result)
					subset.add(set.get(j));
				}
			}
			powerSet.add(subset);
		}
		
		return powerSet;
	}

	public ArrayList<Extension> getCompleteExtensions(boolean usePrevious){
		// TODO compute all complete extensions		
		return null;
	}

	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious){
		// TODO compute all preferred extensions
		return null;
	}

	public ArrayList<Extension> getStableExtensions(boolean usePrevious){
		ArrayList<Extension> cf;
		ArrayList<Extension> stable = new ArrayList<Extension>();
		
		//TODO outsource null/empty check
		if(!usePrevious || (previousConflictFreeSets == null)){
			cf = getConflictFreeSets();
		}
		else{
			cf = previousConflictFreeSets;
		}
		
		if(cf.isEmpty()){
			return null;
		}

		for(Extension e: cf){
			if(e.isStable(this)){
				stable.add(e);
			}
		}
		
		return stable;
	}

	public Extension getGroundedExtension(boolean usePrevious){
		// TODO compute grounded extension
		return null;
	}

	//aka admissible extensions
	public ArrayList<Extension> getAdmissibleSets(boolean usePrevious){
		ArrayList<Extension> cf;
		ArrayList<Extension> admissible = new ArrayList<Extension>();
		
		//TODO outsource null/empty check
		if(!usePrevious || (previousConflictFreeSets == null)){
			cf = getConflictFreeSets();
		}
		else{
			cf = previousConflictFreeSets;
		}
		
		if(cf.isEmpty()){
			return null;
		}
		
		for(Extension e: cf){
			if(e.isAdmissible(this)){
				admissible.add(e);
			}
		}
		
		return admissible;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}
}
