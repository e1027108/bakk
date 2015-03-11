package logic;

import gui.Interactor;

import java.util.ArrayList;

/**
 * The Framework class is a representation of an abstract argumentation framework
 *  and includes methods to compute its extensions (together with the extension class)
 * @author Patrick Bellositz
 */
public class Framework {
	private ArrayList<Argument> arguments; //the set of arguments within the framework
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleSets; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private Interactor interactor; //interacts with the gui
	private String notification; //storage space for messages to be written to the interactor
	
	/**
	 * creates an abstract argument framework
	 * @param arguments the set of arguments that comprise the framework
	 */
	public Framework(ArrayList<Argument> arguments, Interactor interactor) throws IllegalArgumentException{
		this.arguments = arguments;
		this.interactor = interactor;
	}

	/**
	 * computes a set of arguments that are attacked by an argument
	 * @param argumentName the name of the argument that attacks
	 * @return the set of arguments being attacked by the specified argument
	 */
	private ArrayList<Argument> getAttacks(char argumentName){ //TODO use or delete
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
	 * @details computes the powerset of the set of arguments
	 * 			checks every set (within the powerset) if it is conflict-free
	 * @return the set of all conflict-free sets
	 */
	public ArrayList<Extension> getConflictFreeSets(){
		ArrayList<Extension> conflictFreeSets = new ArrayList<Extension>();
		ArrayList<ArrayList<Argument>> powerset;
		
		if(arguments == null){
			//should not be possible
			interactor.addToStoredMessages("No arguments found, error!");
			return null;
		}
		
		powerset = getAllSubsets(arguments);
		
		for(ArrayList<Argument> set: powerset){
			Extension tmp = new Extension(set, this);
			if(tmp.isConflictFree(true)){
				conflictFreeSets.add(tmp);
			}
		}
		
		interactor.addToStoredMessages("The conflict-free sets are: " + formatExtensions(conflictFreeSets));
		
		previousConflictFreeSets = new ArrayList<Extension>();
		previousConflictFreeSets.addAll(conflictFreeSets);
		
		return conflictFreeSets;
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
			notification = "Using previously computed conflict-free sets: ";
			
			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(invalidityCheck(cf, "conflict-free sets", "admissible extensions")){
			return null;
		}
		
		for(Extension e: cf){
			if(e.isAdmissible()){
				admissible.add(e);
			}
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
			
			notification = "Using previously computed admissible extensions: ";
			
			if(admissible.size() == 0){
				notification += "There are no admissible extensions!";
			}
			else{
				notification += formatExtensions(admissible);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(invalidityCheck(admissible, "admissible extensions", "complete extensions")){
			return null;
		}
		
		for(Extension e: admissible){
			boolean isComplete = true;
			String format = e.format();
			
			for(Argument a: arguments){
				if(defends(e, a) && !e.getArguments().contains(a)){
					interactor.addToStoredMessages("The extension " + format + " defends the argument " + a.getName() + " which it doesn't contain. "
							+ "Therefore it is not a complete extension.");
					isComplete = false;
					break;
				}
			}
			if(isComplete){
				interactor.addToStoredMessages("The extension " + format + " is a complete extension!");
				complete.add(e);
			}
		}
		
		interactor.addToStoredMessages("The complete extensions are: " + formatExtensions(complete));
		
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
	public boolean defends(Extension e, Argument a) {
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
			
			notification = "Using previously computed admissible extensions: ";
			
			if(admissible.size() == 0){
				notification += "There are no admissible extensions!";
			}
			else{
				notification += formatExtensions(admissible);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(invalidityCheck(admissible, "admissible extensions", "preferred extensions")){
			return null;
		}
		
		for(Extension e: admissible){
			if(e.isPreferred(admissible)){
				preferred.add(e);
			}
		}
		
		interactor.addToStoredMessages("The preferred extensions are: " + formatExtensions(preferred));
		
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
			
			notification = "Using previously computed conflict-free sets: ";
			
			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(invalidityCheck(cf, "conflict-free sets", "stable extensions")){
			return null;
		}

		for(Extension e: cf){
			if(e.isStable()){
				stable.add(e);
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
			notification = "Using previously computed complete extensions: ";
			
			if(complete == null || complete.size() == 0){
				notification += "There are no complete extensions!";
			}
			else{
				notification += formatExtensions(complete);
			}
			
			interactor.addToStoredMessages(notification);
		}
		
		if(invalidityCheck(complete, "complete extensions", "grounded extension")){
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
	 * formats an extensions to be in a user-friendly string form
	 * @param extensions the extensions to be formated
	 * @return a list (separated by ',') of the formatted extensions
	 */
	private String formatExtensions(ArrayList<Extension> extensions) {
		String formatted = "";
		
		for(Extension e: extensions){
			formatted += e.format() + ", ";
		}
		
		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}
		
		return formatted;
	}

	/**
	 * checks if the list can be used for further computation
	 * @details the set is checked if it is empty or even null
	 * 			making it unfit to be used in further computations
	 * @param list the list to be checked
	 * @param cause cause of the possible problem
	 * @param effect what is not computable because of a problem
	 * @return if there is a problem with the set for further computation
	 */
	public boolean invalidityCheck(ArrayList<Extension> list, String cause, String effect){
		if(list == null || list.isEmpty()){ //shouldn't be possible
			String are = ", there are no ";
			
			if(effect.contains("grounded")){
				are = are.replace("are", "is");
			}
			
			interactor.addToStoredMessages("Since there are no " + cause + are + effect + "!");
			return true;
		}
		return false;
	}
	
	/**
	 * stores a message at the end of the queue of the Interactor
	 * @param message message to be stored by Interactor
	 */
	public void addToInteractor(String message){
		interactor.addToStoredMessages(message);
	}
	
	/**
	 * @return the list of the arguments of the extension
	 */
	public ArrayList<Argument> getArguments() {
		return arguments;
	}
}
