package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.uci.ics.jung.graph.util.Pair;
import javafx.scene.paint.Color;

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
			interactor.addToCommands(new Command("No arguments found, error!", null));
			return null;
		}

		interactor.addToCommands(new Command("Computing conflict-free sets!", null));

		powerset = getAllSubsets(arguments);

		for(ArrayList<Argument> set: powerset){
			Extension tmp = new Extension(set, this);
			if(tmp.isConflictFree(true)){
				conflictFreeSets.add(tmp);
			}
		}

		interactor.addToCommands(new Command("The conflict-free sets are: " + formatExtensions(conflictFreeSets), null));

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

		for(Extension e: cf){
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

		for(Extension e: admissible){
			String format = e.format();
			ArrayList<Argument> outside = new ArrayList<Argument>();

			outside.addAll(arguments);
			outside.removeAll(e.getArguments());

			ArrayList<Argument> acceptable = new ArrayList<Argument>();
			String accString = "";

			for(Argument a: outside){ //create an extension for every argument
				ArrayList<Argument> tmpArgs = new ArrayList<Argument>(e.getArguments());
				Extension tmp = new Extension(tmpArgs,this);
				tmp.addArgument(a);

				if(tmp.isAdmissible(false)){ //if it is admissible, the argument was acceptable and the original extension is not complete
					acceptable.add(a);
					accString += a.getName();
				}
			}

			GraphInstruction highlight = e.toInstruction(Color.GREEN);
			ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();
			ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();

			if(acceptable.size()>0){
				accString = formatNameList(accString);

				for(Argument acc: acceptable){
					ArrayList<Argument> attackers = getAttackers(acc.getName());

					for(int i = 0; i<attackers.size(); i++){
						String attackerName = String.valueOf(attackers.get(0).getName());
						nodeInstructions.add(new SingleInstruction(attackerName,Color.RED));
						edgeInstructions.add(new SingleInstruction(attackerName+acc.getName(),Color.RED));

						for(Argument in: e.getArguments()){
							if(in.getAttacks().contains(attackerName)){
								edgeInstructions.add(new SingleInstruction(in.getName()+attackerName,Color.GREEN));
							}
						}
						if(acc.getAttacks().contains(attackerName)){
							edgeInstructions.add(new SingleInstruction(acc.getName()+attackerName,Color.GREEN));
						}
					}
				}

				for(Argument acc: acceptable){
					nodeInstructions.add(new SingleInstruction(""+acc.getName(),Color.BLUE)); //blue color is more important than red
				}

				highlight.getNodeInstructions().addAll(nodeInstructions);
				highlight.setEdgeInstructions(edgeInstructions);

				interactor.addToCommands(new Command(format + " defends the argument(s) " + accString + ", which it does not contain. " + format + " is not a complete extension.", highlight));
			}
			else{
				complete.add(e);
				ArrayList<Argument> allAttackers = new ArrayList<Argument>();

				for(Argument in: e.getArguments()){
					ArrayList<Argument> attackers = getAttackers(in.getName());
					allAttackers.addAll(attackers);
					
					for(Argument att: attackers){
						if(att.getAttacks().contains(""+in.getName())){
							nodeInstructions.add(new SingleInstruction(""+att.getName(),Color.RED));
							edgeInstructions.add(new SingleInstruction(""+att.getName()+in.getName(),Color.RED));
						}
					}
				}
				for(Argument att: allAttackers){
					for(Argument def: e.getArguments()){
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

		previousCompleteExtensions = new ArrayList<Extension>();
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
	 *///TODO remove?
	public ArrayList<String> getDefences(Extension e, Argument a) {
		ArrayList<Argument> attackArgument = getAttackers(a.getName());
		ArrayList<String> defences = new ArrayList<String>();
		String argName = String.valueOf(a.getName());

		if(e.getAttacks().contains(argName) || a.getAttacks().contains(argName)){ //if it gets attacked by the extension itself or it attacks itself
			return defences;
		}

		if(attackArgument.isEmpty()){ //if it doesn't get attacked
			defences.add(argName+argName);
		}
		else{
			for(Argument attacker: attackArgument){
				String attName = String.valueOf(attacker.getName());

				if(a.getAttacks().contains(attName)){ //or defends itself
					defences.add(argName+attName);
				}

				for(Argument eArg: e.getArguments()){
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
	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious){
		ArrayList<Extension> admissible;
		ArrayList<Extension> preferred = new ArrayList<Extension>();

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

		for(Extension e: admissible){
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
	public ArrayList<Extension> getStableExtensions(boolean usePrevious){
		ArrayList<Extension> cf;
		ArrayList<Extension> stable = new ArrayList<Extension>();

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

		for(Extension e: cf){
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
	 */
	public Extension getGroundedExtension(boolean usePrevious){
		ArrayList<Extension> complete;
		ArrayList<Argument> grounded = new ArrayList<Argument>();

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

		for(Extension e: complete){
			if(e.getArgumentNames().length() == 0){
				interactor.addToCommands(new Command("Since there is a complete extension {}, the grounded extension is {}", null));
				return new Extension(grounded, this);
			}
		}

		for(Extension e: complete){
			String eFormat = e.format();

			if(grounded.isEmpty()){ //if no elements in grounded, e is first extension
				grounded.addAll(e.getArguments());
				interactor.addToCommands(new Command("The extension " + eFormat + " is the first candidate for grounded extension.", e.toInstruction(Color.GREEN)));
			}
			else{ //else check for common elements in extension
				ArrayList<Argument> missing = new ArrayList<Argument>();
				String missingString = "";

				GraphInstruction highlight = new GraphInstruction(new ArrayList<SingleInstruction>(), new ArrayList<SingleInstruction>());

				for(Argument a: grounded){
					String aName = String.valueOf(a.getName());
					if(!e.getArguments().contains(a)){
						missingString += aName;
						missing.add(a);
						highlight.getNodeInstructions().add(new SingleInstruction(aName,Color.BLUE));
					}
				}

				grounded.retainAll(e.getArguments());

				Extension tmp = new Extension(grounded, this);
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

		Extension groundedExtension = new Extension(grounded,this);

		interactor.addToCommands(new Command("The grounded extension is: " + groundedExtension.format(), groundedExtension.toInstruction(Color.GREEN)));

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
	public boolean invalidityCheck(ArrayList<Extension> list, String cause, String effect){
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
	public ArrayList<Argument> getArguments() {
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
