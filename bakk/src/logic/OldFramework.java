package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import javafx.scene.paint.Color;

/**
 * The Framework class is a representation of an abstract argumentation framework
 *  and includes methods to compute its extensions (together with the extension class)
 * @author Patrick Bellositz
 */
@Deprecated
public class OldFramework {
	private ArrayList<OldArgument> arguments; //the set of arguments within the framework
	private ArrayList<OldExtension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<OldExtension> previousAdmissibleSets; //a stored, previously computed set of admissible extensions
	private ArrayList<OldExtension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private Interactor interactor; //interacts with the gui
	private String notification; //storage space for messages to be written to the interactor

	/**
	 * creates an abstract argument framework
	 * @param arguments the set of arguments that comprise the framework
	 * @param interactor the Interactor which manages commands for visualization
	 */
	public OldFramework(ArrayList<OldArgument> arguments, Interactor interactor){
		this.arguments = arguments;
		this.interactor = interactor;
	}

	/**
	 * returns the argument of the specified name
	 * @param argumentName the name of an argument
	 * @return the argument of the specified name
	 */
	public OldArgument getArgument(char argumentName){
		if(arguments != null){
			for(OldArgument a: arguments){
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
	public ArrayList<OldArgument> getAttackers(char argumentName){
		ArrayList<OldArgument> attackers = new ArrayList<OldArgument>();

		if(arguments != null){
			for(OldArgument a: arguments){
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
	public ArrayList<OldExtension> getConflictFreeSets(){
		ArrayList<OldExtension> conflictFreeSets = new ArrayList<OldExtension>();
		ArrayList<ArrayList<OldArgument>> powerset;

		if(arguments == null){
			//should not be possible
			interactor.addToCommands(new Command("No arguments found, error!", null));
			return null;
		}

		interactor.addToCommands(new Command("Computing conflict-free sets!", null));

		powerset = getAllSubsets(arguments);

		for(ArrayList<OldArgument> set: powerset){
			OldExtension tmp = new OldExtension(set, this);
			if(tmp.isConflictFree(true)){
				conflictFreeSets.add(tmp);
			}
		}

		interactor.addToCommands(new Command("The conflict-free sets are: " + formatExtensions(conflictFreeSets), null));

		previousConflictFreeSets = new ArrayList<OldExtension>();
		previousConflictFreeSets.addAll(conflictFreeSets);

		return conflictFreeSets;
	}

	/**
	 * computes the powerset of a given set
	 * @details a binary mask is laid over the specified set to compute each subset possible
	 * @param set is the specified set of which to compute the powerset
	 * @return the powerset of the set in question
	 */
	private ArrayList<ArrayList<OldArgument>> getAllSubsets(ArrayList<OldArgument> set){
		ArrayList<ArrayList<OldArgument>> powerSet = new ArrayList<ArrayList<OldArgument>>();
		int elements = set.size();

		for(int i = 0; i < Math.pow(2, elements); i++){ //there are 2^n subsets
			ArrayList<OldArgument> subset = new ArrayList<OldArgument>();
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
	public ArrayList<OldExtension> getAdmissibleSets(boolean usePrevious){
		ArrayList<OldExtension> cf;
		ArrayList<OldExtension> admissible = new ArrayList<OldExtension>();

		if(!usePrevious || (previousConflictFreeSets == null)){
			interactor.addToCommands(new Command("Computing conflict-free sets to compute admissible extensions!", null));
			cf = getConflictFreeSets();
		}
		else{
			cf = previousConflictFreeSets;
			notification = "Using previously computed conflict-free sets to compute admissible extensions: ";

			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(cf, "conflict-free sets", "admissible extensions")){
			return null;
		}

		for(OldExtension e: cf){
			if(e.isAdmissible(true)){
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
		interactor.addToCommands(new Command(notification, null));

		previousAdmissibleSets = new ArrayList<OldExtension>();
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
	 * @throws InvalidInputException if algorithm uses invalid arguments
	 */
	public ArrayList<OldExtension> getCompleteExtensions(boolean usePrevious) throws InvalidInputException{
		ArrayList<OldExtension> admissible;
		ArrayList<OldExtension> complete = new ArrayList<OldExtension>();

		if(!usePrevious || (previousAdmissibleSets == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute complete extensions!",null));
			admissible = getAdmissibleSets(usePrevious);
		}
		else{
			admissible = previousAdmissibleSets;

			notification = "Using previously computed admissible extensions to compute complete extensions: ";

			if(admissible.size() == 0){
				notification += "There are no admissible extensions!";
			}
			else{
				notification += formatExtensions(admissible);
			}

			interactor.addToCommands(new Command(notification,null));
		}

		if(invalidityCheck(admissible, "admissible extensions", "complete extensions")){
			return null;
		}

		for(OldExtension e: admissible){
			String format = e.format();
			ArrayList<OldArgument> outside = new ArrayList<OldArgument>();

			outside.addAll(arguments);
			outside.removeAll(e.getArguments());

			ArrayList<OldArgument> defended = new ArrayList<OldArgument>();
			String defString = "";

			for(OldArgument a: outside){ //create an extension for every argument
				ArrayList<OldArgument> tmpArgs = new ArrayList<OldArgument>(e.getArguments());
				OldExtension tmp = new OldExtension(tmpArgs,this);
				tmp.addArgument(a);

				/*if(tmp.isAdmissible(false)){ //if it is admissible, the argument was acceptable and the original extension is not complete
					acceptable.add(a);
					accString += a.getName();
				}*/
				
				ArrayList<OldArgument> attackers = getAttackers(a.getName());
				
				if(attackers.isEmpty()){
					defended.add(a);
					defString += a.getName();
					continue;
				}

				int defences = 0;
				for(OldArgument att: attackers){
					if(e.getAttacks().contains(String.valueOf(att.getName()))){
						defences++;
					}
				}
				
				if(defences == attackers.size()){
					defended.add(a);
					defString += a.getName();
				}
			}

			GraphInstruction highlight = e.toInstruction(Color.GREEN);
			ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();
			ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();

			if(defended.size()>0){
				defString = formatNameList(defString);

				for(OldArgument acc: defended){
					ArrayList<OldArgument> attackers = getAttackers(acc.getName());

					for(int i = 0; i<attackers.size(); i++){
						String attackerName = String.valueOf(attackers.get(0).getName());
						nodeInstructions.add(new SingleInstruction(attackerName,Color.RED));
						edgeInstructions.add(new SingleInstruction(attackerName+acc.getName(),Color.RED));

						for(OldArgument in: e.getArguments()){
							if(in.getAttacks().contains(attackerName)){
								edgeInstructions.add(new SingleInstruction(in.getName()+attackerName,Color.GREEN));
							}
						}
						if(acc.getAttacks().contains(attackerName)){
							edgeInstructions.add(new SingleInstruction(acc.getName()+attackerName,Color.GREEN));
						}
					}
				}

				for(OldArgument acc: defended){
					nodeInstructions.add(new SingleInstruction(""+acc.getName(),Color.BLUE)); //blue color is more important than red
				}

				highlight.getNodeInstructions().addAll(nodeInstructions);
				highlight.setEdgeInstructions(edgeInstructions);

				interactor.addToCommands(new Command(format + " defends the argument(s) " + defString + ", which it does not contain. " + format + " is not a complete extension.", highlight));
			}
			else{
				complete.add(e);
				ArrayList<OldArgument> allAttackers = new ArrayList<OldArgument>();

				for(OldArgument in: e.getArguments()){
					ArrayList<OldArgument> attackers = getAttackers(in.getName());
					allAttackers.addAll(attackers);
					
					for(OldArgument att: attackers){
						if(att.getAttacks().contains(""+in.getName())){
							nodeInstructions.add(new SingleInstruction(""+att.getName(),Color.RED));
							edgeInstructions.add(new SingleInstruction(""+att.getName()+in.getName(),Color.RED));
						}
					}
				}
				for(OldArgument att: allAttackers){
					for(OldArgument def: e.getArguments()){
						if(def.getAttacks().contains(""+att.getName())){
							edgeInstructions.add(new SingleInstruction(""+def.getName()+att.getName(),Color.GREEN));
						}
					}
				}
				
				highlight.getNodeInstructions().addAll(nodeInstructions);
				highlight.setEdgeInstructions(edgeInstructions);

				interactor.addToCommands(new Command(format + " contains all the arguments it defends and therefore is a complete extension.", highlight)); 
			}
		}

		previousCompleteExtensions = new ArrayList<OldExtension>();
		previousCompleteExtensions.addAll(complete);

		interactor.addToCommands(new Command("The complete extensions are: " + formatExtensions(complete), null));

		return complete;
	}

	/**
	 * checks which arguments of the given extension defend the specified argument
	 * @param e the extension that might defend the argument
	 * @param a the argument that might be defended
	 * @return a string representing the edge from defender to attacker;
	 * 		in case a argument doesn't get attacked the string representation is this arguments name twice
	 */
	public ArrayList<String> getDefences(OldExtension e, OldArgument a) {
		ArrayList<OldArgument> attackArgument = getAttackers(a.getName());
		ArrayList<String> defences = new ArrayList<String>();
		String argName = String.valueOf(a.getName());

		if(e.getAttacks().contains(argName) || a.getAttacks().contains(argName)){ //if it gets attacked by the extension itself or it attacks itself
			return defences;
		}

		if(attackArgument.isEmpty()){ //if it doesn't get attacked
			defences.add(argName+argName);
		}
		else{
			for(OldArgument attacker: attackArgument){
				String attName = String.valueOf(attacker.getName());

				if(a.getAttacks().contains(attName)){ //or defends itself
					defences.add(argName+attName);
				}

				for(OldArgument eArg: e.getArguments()){
					if(eArg.getAttacks().contains(attName)){ //if it gets defended
						defences.add(eArg.getName()+attName);
					}
				}
			}
		}

		return defences;
	}

	/**
	 * computes all preferred extensions
	 * @details each admissible set is checked if it is preferred within the framework
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of preferred extensions of the framework
	 */
	public ArrayList<OldExtension> getPreferredExtensions(boolean usePrevious){
		ArrayList<OldExtension> admissible;
		ArrayList<OldExtension> preferred = new ArrayList<OldExtension>();

		if(!usePrevious || (previousAdmissibleSets == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute preferred extensions!", null));
			admissible = getAdmissibleSets(usePrevious);
		}
		else{
			admissible = previousAdmissibleSets;

			notification = "Using previously computed admissible extensions to compute preferred extensions: ";

			if(admissible.size() == 0){
				notification += "There are no admissible extensions!";
			}
			else{
				notification += formatExtensions(admissible);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(admissible, "admissible extensions", "preferred extensions")){
			return null;
		}

		for(OldExtension e: admissible){
			if(e.isPreferred(admissible)){
				preferred.add(e);
			}
		}

		interactor.addToCommands(new Command("The preferred extensions are: " + formatExtensions(preferred), null));

		return preferred;
	}

	/**
	 * computes all stable extensions of the framework
	 * @details each conflict-free set is checked if it is a stable extension (meaning it attacks all other arguments)
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the set of stable extensions of the framework
	 */
	public ArrayList<OldExtension> getStableExtensions(boolean usePrevious){
		ArrayList<OldExtension> cf;
		ArrayList<OldExtension> stable = new ArrayList<OldExtension>();

		if(!usePrevious || (previousConflictFreeSets == null)){
			interactor.addToCommands(new Command("Computing conflict-free sets to compute stable extensions!", null));
			cf = getConflictFreeSets();
		}
		else{
			cf = previousConflictFreeSets;

			notification = "Using previously computed conflict-free sets to compute stable extensions: ";

			if(cf.size() == 0){
				notification += "There are no conflict-free sets!";
			}
			else{
				notification += formatExtensions(cf);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(cf, "conflict-free sets", "stable extensions")){
			return null;
		}

		for(OldExtension e: cf){
			if(e.isStable()){
				stable.add(e);
			}
		}

		if(stable.size()>0){
			interactor.addToCommands(new Command("The stable extensions are: " + formatExtensions(stable), null));
		}
		else{
			interactor.addToCommands(new Command("There are no stable extensions!", null));
		}

		return stable;
	}

	/**
	 * find the grounded extension of the framework
	 * @details the complete extensions are checked for common elements, the least element being the grounded extension
	 * @param usePrevious specifies if previously computed sets for the extensions should be used (true) or computed anew (false)
	 * @return the grounded extension of the framework
	 * @throws InvalidInputException if complete extensions could not be computed due to invalid arguments
	 */
	public OldExtension getGroundedExtension(boolean usePrevious) throws InvalidInputException{
		ArrayList<OldExtension> complete;
		ArrayList<OldArgument> grounded = new ArrayList<OldArgument>();

		if(!usePrevious || (previousCompleteExtensions == null)){
			interactor.addToCommands(new Command("Computing complete extensions to compute the grounded extension!", null));
			complete = getCompleteExtensions(usePrevious);
		}
		else{
			complete = previousCompleteExtensions;
			notification = "Using previously computed complete extensions to compute the grounded extension: ";

			if(complete == null || complete.size() == 0){
				notification += "There are no complete extensions!";
			}
			else{
				notification += formatExtensions(complete);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(complete, "complete extensions", "grounded extension")){
			return null;
		}

		if(complete.size() == 1){
			interactor.addToCommands(new Command("The only complete extension " + complete.get(0).format() + " is the grounded extension.", complete.get(0).toInstruction(Color.GREEN)));
			return complete.get(0);
		}

		for(OldExtension e: complete){
			if(e.getArgumentNames().length() == 0){
				interactor.addToCommands(new Command("Since there is a complete extension {}, the grounded extension is {}", null));
				return new OldExtension(grounded, this);
			}
		}

		for(OldExtension e: complete){
			String eFormat = e.format();

			if(grounded.isEmpty()){ //if no elements in grounded, e is first extension
				grounded.addAll(e.getArguments());
				interactor.addToCommands(new Command("The extension " + eFormat + " is the first candidate for grounded extension.", e.toInstruction(Color.GREEN)));
			}
			else{ //else check for common elements in extension
				ArrayList<OldArgument> missing = new ArrayList<OldArgument>();
				String missingString = "";

				GraphInstruction highlight = new GraphInstruction(new ArrayList<SingleInstruction>(), new ArrayList<SingleInstruction>());

				for(OldArgument a: grounded){
					String aName = String.valueOf(a.getName());
					if(!e.getArguments().contains(a)){
						missingString += aName;
						missing.add(a);
						highlight.getNodeInstructions().add(new SingleInstruction(aName,Color.BLUE));
					}
				}

				grounded.retainAll(e.getArguments());

				OldExtension tmp = new OldExtension(grounded, this);
				highlight.getNodeInstructions().addAll(tmp.toInstruction(Color.GREEN).getNodeInstructions());

				if(missing.size() > 0){
					interactor.addToCommands(new Command(eFormat + " doesn't contain the argument(s) " + formatNameList(missingString) + 
							". Therefore our new candidate is " + tmp.format(), highlight));
				}
				else{
					interactor.addToCommands(new Command("Since " + eFormat + " contains all the arguments of " + tmp.format() + " our candidate doesn't change.", highlight));
				}

				if(grounded.isEmpty()){
					break;
				}
			}
		}

		OldExtension groundedExtension = new OldExtension(grounded,this);

		interactor.addToCommands(new Command("The grounded extension is: " + groundedExtension.format(), groundedExtension.toInstruction(Color.GREEN)));

		return groundedExtension;
	}

	/**
	 * formats an extensions to be in a user-friendly string form
	 * @param extensions the extensions to be formated
	 * @return a list (separated by ',') of the formatted extensions
	 */
	private String formatExtensions(ArrayList<OldExtension> extensions) {
		String formatted = "";

		for(OldExtension e: extensions){
			formatted += e.format() + ", ";
		}

		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}

		return formatted;
	}

	/**
	 * takes a string representation of a set and turns it into a standard (green) graph instruction
	 * @param item the string representation of the set "{a,b,..}"
	 * @return a GraphInstruction turning all the elements of the set green
	 */
	public GraphInstruction getInstructionFromString(String item) {
		GraphInstruction instruction = new GraphInstruction(new ArrayList<SingleInstruction>(), null);		
		item = item.replace("{", "");
		item = item.replace("}", "");

		for(int i = 0; i < item.length(); i++){
			instruction.getNodeInstructions().add(new SingleInstruction(String.valueOf(item.charAt(i)), Color.GREEN));
		}

		return instruction;
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
	public boolean invalidityCheck(ArrayList<OldExtension> list, String cause, String effect){
		if(list == null || list.isEmpty()){ //shouldn't be possible
			String are = ", there are no ";

			if(effect.contains("grounded")){
				are = are.replace("are", "is");
			}

			interactor.addToCommands(new Command("Since there are no " + cause + are + effect + "!", null));
			return true;
		}
		return false;
	}

	/**
	 * stores a message at the end of the queue of the Interactor
	 * @param message message to be stored by Interactor
	 */
	public void addToInteractor(Command command){
		interactor.addToCommands(command);
	}

	/**
	 * @return the list of the arguments of the extension
	 */
	public ArrayList<OldArgument> getArguments() {
		return arguments;
	}

	/**
	 * formats a string of argument names to be a readable list in a sentence
	 * @param input the string to be formatted
	 * @return the formatted string (now a list, separated by ',' and an 'and' between the last two elements)
	 */
	public String formatNameList(String input) {
		String output = "";

		if(input.length() < 2){
			return input;
		}

		for(int i = 0; i < input.length(); i++){
			output += input.charAt(i) + ", ";
		}

		output = output.substring(0,output.length()-2);
		String last = "" + output.charAt(output.length()-1);
		output = output.replace(", " + last, " and " + last);

		return output;
	}
}
