package logic;

import gui.Interactor;

import java.util.ArrayList;

public class Framework {
	private ArrayList<Argument> arguments; //the set of arguments within the framework
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleSets; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private Interactor interactor; //interacts with the gui
	//TODO write user-friendly messages to gui using the interactor
	
	/**
	 * creates an abstract argument framework
	 * @param arguments the set of arguments that comprise the framework
	 */
	public Framework(ArrayList<Argument> arguments, Interactor interactor){
		this.arguments = arguments;
		this.interactor = interactor;
	}

	/**
	 * computes a set of arguments that are attacked by an argument
	 * @param argumentName the name of the argument that attacks
	 * @return the set of arguments being attacked by the specified argument
	 */
	private ArrayList<Argument> getAttacks(char argumentName){
		ArrayList<Argument> attacks = new ArrayList<Argument>();
		String attackString = getArgument(argumentName).getAttacks();

		for(int i = 0;i<attackString.length();i++){
			attacks.add(getArgument(attackString.charAt(i)));
		}

		return attacks;
	}

	/**
	 * returns the argument of the specified name
	 * @param argumentName the name of an argument
	 * @return the argument of the specified name
	 */
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

	/**
	 * computes a set of arguments attacking an argument
	 * @param argumentName the name of the argument being attacked
	 * @return the set of arguments attacking the specified argument
	 */
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

	/**
	 * computes all conflict-free sets of the framework
	 * @details for every argument a set is created containing it and all arguments that don't attack and it doesn't attack
	 * 			from these conflict-free sets are extracted
	 * 			duplicates don't get created, because an already checked argument doesn't become part of the sets of the next
	 * @return the set of all conflict-free sets
	 */
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

		conflictFreeSets.add(new Extension(new ArrayList<Argument>(), this));
		
		previousConflictFreeSets = new ArrayList<Extension>();
		previousConflictFreeSets.addAll(conflictFreeSets);
		
		return conflictFreeSets;
	}

	/**
	 * computes a set of arguments not attacking or being attacked by a specific one
	 * @param workingSet is the set of eligible arguments to be checked
	 * @param arg is the argument that shouldn't have be in conflict with the checked ones
	 * @return a set of arguments not attacking or being attacked by the specified argument
	 */
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

	/**
	 * checks for all subsets of a set if they are conflict-free
	 * @details the specified argument is added to every set to be checked in turn and checked for conflict-freeness
	 * @param nonConflictingSet a set of argument that all individually don't conflict with a specified argument
	 * @param arg the specified argument not to conflict with
	 * @return the set of all conflict-free subsets regarding the specified argument and sets
	 */
	private ArrayList<Extension> getConflictFreeSubSets(ArrayList<Argument> nonConflictingSet, Argument arg) {
		ArrayList<ArrayList<Argument>> subSets = getAllSubsets(nonConflictingSet);
		ArrayList<Extension> conflictFree = new ArrayList<Extension>();
		
		for(ArrayList<Argument> s: subSets){
			s.add(arg);
			Extension e = new Extension(s, this);
			if(e.isConflictFree()){
				conflictFree.add(e);
			}
		}
		
		return conflictFree;
	}

	/**
	 * computes the powerset of a given set
	 * @details a binary mask is laid over the specified set to compute each subset possible
	 * @param set is the specified set of which to compute the powerset
	 * @return the powerset of the set in question
	 */
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

	/**
	 * computes all complete extensions of the framework
	 * @details for each extension it is checked if it is possible to add another argument from the framework and still receive an admissible set
	 * 			if it is, the extension in question is not a complete extension
	 * 			all other extensions are complete extensions
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of all complete extensions of the framework
	 */
	public ArrayList<Extension> getCompleteExtensions(boolean usePrevious){
		ArrayList<Extension> admissible;
		ArrayList<Extension> complete = new ArrayList<Extension>();
		
		if(!usePrevious || (previousConflictFreeSets == null)){
			admissible = getAdmissibleSets(usePrevious);
		}
		else{
			admissible = previousAdmissibleSets;
		}
		
		if(admissible.isEmpty()){
			return null;
		}
		
		boolean add;
		for(Extension e: admissible){
			add = true;
			for(Argument a: arguments){
				if(!e.getArguments().contains(a)){
					ArrayList<Argument> tmparg = new ArrayList<Argument>();
					tmparg.addAll(e.getArguments());
					Extension tmp = new Extension(tmparg, this);
					tmp.addArgument(a);
					if(tmp.isConflictFree() && tmp.isCFAdmissible()){
						//System.out.println("{" + tmp.getArgumentNames() + "} is admissible, so {" + e.getArgumentNames() + "} is not complete");
						add = false;
						break;
					}
				}
			}
			if(add){
				complete.add(e);
			}
		}
		
		previousCompleteExtensions = new ArrayList<Extension>();
		previousCompleteExtensions.addAll(complete);
		
		return complete;
	}

	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious){
		// TODO compute all preferred extensions
		return null;
	}

	/**
	 * computes all stable extensions of the framework
	 * @details each conflict-free set is checked if it is a stable extension (meaning it attacks all other arguments)
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of stable extensions of the framework
	 */
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
			if(e.isStable()){
				stable.add(e);
			}
		}
		
		return stable;
	}

	/*
	 * The grounded extension of an argumentation framework AF, denoted by GE_AF, is
	 * the least fixed point of F_AF
	 *
	 * The characteristic function, denoted by F_AF, of an argumentation framework 
	 * AF = <AR,attacks> is defined as follows:
	 * F_AF: 2^AR -> 2^AR
	 * F_AF(S) = { A | A is acceptable (=defended) wrt S }
	 */
	public Extension getGroundedExtension(boolean usePrevious){
		ArrayList<Extension> complete;
		ArrayList<Argument> grounded = new ArrayList<Argument>();
		
		//TODO outsource null/empty check
		if(!usePrevious || (previousConflictFreeSets == null)){
			complete = getCompleteExtensions(usePrevious);
		}
		else{
			complete = previousCompleteExtensions;
		}
		
		if(complete.isEmpty()){
			return null;
		}

		for(Extension e: complete){
			//TODO find smallest common element in complete extensions
			if(grounded.isEmpty()){
				grounded.addAll(e.getArguments());
			}
			else{
				for(int i = grounded.size()-1;i>=0;i--){
					if(!e.getArguments().contains(grounded.get(i))){
						grounded.remove(grounded.get(i));
					}
				}
			}
		}
		
		return new Extension(grounded, this);
	}

	/**
	 * computes all admissible sets (= admissible extensions) of the framework
	 * @details every conflict-free set is checked if it's admissible
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of admissible sets of the framework
	 */
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
			if(e.isCFAdmissible()){
				admissible.add(e);
			}
		}
		
		previousAdmissibleSets = new ArrayList<Extension>();
		previousAdmissibleSets.addAll(admissible);
		
		return admissible;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}
}
