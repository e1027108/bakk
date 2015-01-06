package logic;

import java.util.ArrayList;

public class Framework {
	private ArrayList<Argument> arguments;

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
		ArrayList<Extension> subSets = getAllSubsets(nonConflictingSet);
		ArrayList<Extension> conflictFree = new ArrayList<Extension>();
		
		for(Extension e: subSets){
			e.addArgument(arg);
			if(e.isConflictFree()){
				conflictFree.add(e);
			}
		}
		
		return conflictFree;
	}

	private ArrayList<Extension> getAllSubsets(ArrayList<Argument> set){
		// TODO return all subSets of the given set (including the empty set)
		return null;
	}

	public ArrayList<Extension> getCompleteExtensions(){
		// TODO compute all complete extensions		
		return null;
	}

	public ArrayList<Extension> getPreferredExtensions(){
		// TODO compute all preferred extensions
		return null;
	}

	public ArrayList<Extension> getStableExtensions(){
		// TODO compute all stable extensions
		return null;
	}

	public Extension getGroundedExtension(){
		// TODO compute grounded extension
		return null;
	}

	public ArrayList<Extension> getAdmissibleSets(){
		// TODO compute all admissible sets
		return null;
	}
}
