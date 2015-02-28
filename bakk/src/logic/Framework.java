package logic;

import gui.Interactor;

import java.util.ArrayList;

public class Framework {
	private ArrayList<Argument> arguments; //the set of arguments within the framework
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleSets; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private Interactor interactor; //interacts with the gui
	private String notification;
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
		
		for(Extension e: admissible){
			boolean isComplete = true;
			for(Argument a: arguments){
				if(defends(e, a) && !e.getArguments().contains(a)){
					isComplete = false;
					break;
				}
			}
			if(isComplete){
				complete.add(e);
			}
		}
		
		previousCompleteExtensions = new ArrayList<Extension>();
		previousCompleteExtensions.addAll(complete);
		
		return complete;
	}

	/**
	 * checks if the given extension defends the given argument
	 * @param e the extension that might defend the argument
	 * @param a the argument that might be defended
	 * @return whether the given extension defends the given argument
	 */
	private boolean defends(Extension e, Argument a) {
		String extensionAttacks = e.getAttacks();
		ArrayList<Argument> attackArgument = getAttackers(a.getName());
		
		for(Argument attacker: attackArgument){
			if(!extensionAttacks.contains(String.valueOf(attacker.getName()))){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * computes all preferred extensions
	 * @details each admissible set is checked if it is preferred within the framework
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of preferred extensions of the framework
	 */
	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious){
		ArrayList<Extension> admissible;
		ArrayList<Extension> preferred = new ArrayList<Extension>();
		
		//TODO outsource null/empty check
		if(!usePrevious || (previousAdmissibleSets == null)){
			admissible = getAdmissibleSets(usePrevious);
		}
		else{
			admissible = previousAdmissibleSets;
		}
		
		if(admissible.isEmpty()){
			return null;
		}
		
		for(Extension e: admissible){
			if(e.isPreferred(admissible)){
				preferred.add(e);
			}
		}
		
		return preferred;
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
			
			notification = "using previously computed conflict-free sets: ";
			
			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(cf.isEmpty()){
			//shouldn't be possible
			interactor.addToStoredMessages("Since there are no conflict-free sets, there are no stable extensions!");
			return null;
		}

		for(Extension e: cf){
			if(e.isStable()){
				stable.add(e);
				interactor.addToStoredMessages(e.format() + " is a stable extension."); 
			}
			else{
				interactor.addToStoredMessages(e.format() + " is not a stable extension.");
			}
		}
		
		interactor.addToStoredMessages("The stable extensions are: " + formatExtensions(stable));
		
		return stable;
	}

	/**
	 * find the grounded extension of the framework
	 * @details the complete extensions are checked for common elements, the least element being the grounded extension
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the grounded extension of the framework
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
			notification = "using previously computed complete extensions: ";
			
			if(complete.size() == 0){
				notification += "There are no complete extensions!";
			}
			else{
				notification += formatExtensions(complete);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(complete.isEmpty()){
			//this shouldn't be possible to happen
			interactor.addToStoredMessages("Since there are no complete extensions there is no grounded extension!");
			return null;
		}

		for(Extension e: complete){
			if(e.getArgumentNames().length() == 0){
				interactor.addToStoredMessages("Since there is a complete extension {}, the grounded extension is {}");
				return new Extension(grounded, this);
			}
		}
		
		for(Extension e: complete){
			String eFormat = e.format();
			if(grounded.isEmpty()){
				grounded.addAll(e.getArguments());
				interactor.addToStoredMessages("The complete extension " + eFormat + " is our first candidate as grounded extension.");
			}
			else{
				int errors = 0;
				for(int i = grounded.size()-1;i>=0;i--){
					if(!e.getArguments().contains(grounded.get(i))){
						interactor.addToStoredMessages("The complete extension " + eFormat + " does not contain the argument " + grounded.get(i).getName() + ", therefore it is removed from our candidate");
						grounded.remove(grounded.get(i));
						errors++;
					}
				}
				if(errors == 0){
					interactor.addToStoredMessages("The complete extension " + eFormat + " contains all arguments of our candidate.");
				}
				else{
					interactor.addToStoredMessages("Our new candidate is " + new Extension(grounded,this).format());
				}
			}
		}
		
		Extension groundedExtension = new Extension(grounded,this);
		
		interactor.addToStoredMessages("The grounded extension is: " + groundedExtension.format());
		
		return groundedExtension;
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
			notification = "using previously computed conflict-free sets: ";
			
			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(cf.isEmpty()){
			interactor.addToStoredMessages("Since there are no conflict-free sets there are no admissible extensions!");
			return null;
		}
		
		for(Extension e: cf){
			if(e.isCFAdmissible()){
				admissible.add(e);
				
				notification = e.format() + " is an admissible extension!";
			}
			else{
				notification = e.format() + " is not an admissible extension!";
			}
			interactor.addToStoredMessages(notification);
		}
		
		if(admissible.size() > 0){
			notification = "The admissible extensions are: "; 
			notification += formatExtensions(admissible);
		}
		else{
			notification = "There are no admissible extensions!";
		}
		interactor.addToStoredMessages(notification);
		//TODO outsource message generation?
		
		previousAdmissibleSets = new ArrayList<Extension>();
		previousAdmissibleSets.addAll(admissible);
		
		return admissible;
	}

	private String formatExtensions(ArrayList<Extension> cf) {
		String formatted = "";
		
		for(Extension e: cf){
			formatted += e.format() + ", ";
		}
		
		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}
		
		return formatted;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}
}
