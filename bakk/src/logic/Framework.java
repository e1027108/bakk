package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Framework {

	private ArrayList<Argument> arguments;
	private ArrayList<Attack> attacks;
	private Interactor interactor;
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleSets; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private String notification;
	
	public Framework(ArrayList<Argument> arguments, ArrayList<Attack> attacks, Interactor interactor) {
		this.arguments = arguments;
		this.attacks = attacks;
		this.interactor = interactor;
	}

	public ArrayList<Extension> getConflictFreeSets() {
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

	public ArrayList<Extension> getAdmissibleSets(boolean usePrevious) {
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

	public ArrayList<Extension> getCompleteExtensions(boolean usePrevious) {
		
		return null;
	}

	public ArrayList<Extension> getStableExtensions(boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

	public Extension getGroundedExtension(boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Extension> getPreferredExtensions(boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GraphInstruction getInstructionFromString(String item) {
		GraphInstruction instruction = new GraphInstruction(new ArrayList<SingleInstruction>(), null);		
		item = item.replace("{", "");
		item = item.replace("}", "");

		for(int i = 0; i < item.length(); i++){
			instruction.getNodeInstructions().add(new SingleInstruction(String.valueOf(item.charAt(i)), Color.GREEN));
		}

		return instruction;
	}

	public Argument getArgument(char name) {
		if(arguments != null){
			for(Argument a: arguments){
				if(a.getName() == name){
					return a;
				}
			}
		}
		
		return null;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}

	public ArrayList<Attack> getAttacks(){
		return attacks;
	}
	
	public ArrayList<Attack> getAttacks(char attacker) {
		ArrayList<Attack> argumentAttacks = new ArrayList<Attack>();
		
		for(Attack a: attacks){
			if(a.getAttacker().getName() == attacker){
				argumentAttacks.add(a);
			}
		}
		
		return argumentAttacks;
	}
	
	public ArrayList<Argument> getAttackedBy(char attacker) {
		ArrayList<Argument> attacked = new ArrayList<Argument>();
		
		for(Attack att: getAttacks(attacker)){
			attacked.add(att.getAttacked());
		}
		
		return attacked;
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

	public String formatArgumentList(ArrayList<Argument> input) {
		String argNames = "";
		
		for(Argument a: input){
			argNames += a.getName();
		}
		
		return formatNameList(argNames);
	}
	
	public String formatAttackList(ArrayList<Attack> input,int pos) {
		String argNames = "";

		for(Attack a: input){
			if(pos == 1){
				argNames += a.getAttacker().getName();
			}
			else if(pos == 2){
				argNames += a.getAttacked().getName();
			}
			//TODO other numbers not defined, add to doc
		}
		
		return formatNameList(argNames);
	}
	
	/**
	 * stores a message at the end of the queue of the Interactor
	 * @param message message to be stored by Interactor
	 */
	public void addToInteractor(Command command){
		interactor.addToCommands(command);
	}


}
