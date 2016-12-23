package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import javafx.scene.paint.Color;

public class Framework {

	private ArrayList<Argument> arguments;
	private ArrayList<Attack> attacks;
	private Interactor interactor;
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleExtensions; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	//TODO add more previous lists for equivalency checks
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

	public ArrayList<Extension> getAdmissibleExtensions(boolean usePrevious) {
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

		previousAdmissibleExtensions = new ArrayList<Extension>();
		previousAdmissibleExtensions.addAll(admissible);

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
	
	public ArrayList<Extension> getCompleteExtensions(boolean usePrevious) throws InvalidInputException {
		ArrayList<Extension> adm;
		ArrayList<Extension> complete = new ArrayList<Extension>();

		if(!usePrevious || (previousAdmissibleExtensions == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute complete extensions!", null));
			adm = getAdmissibleExtensions(usePrevious);
		}
		else{
			adm = previousAdmissibleExtensions;
			notification = "Using previously computed admissible extensions to compute complete extensions: ";

			if(adm.size() == 0){
				notification += "There are no admissible sets!";
			}
			else{
				notification += formatExtensions(adm);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(adm, "admissible extensions", "complete extensions")){
			return null;
		}

		for(Extension e: adm){
			if(e.isComplete(true)){
				complete.add(e);
			}
		}

		if(complete.size() > 0){
			notification = "The complete extensions are: ";
			notification += formatExtensions(complete);
		}
		else{
			notification = "There are no complete extensions!";
		}
		
		interactor.addToCommands(new Command(notification, null));

		previousCompleteExtensions = new ArrayList<Extension>();
		previousCompleteExtensions.addAll(complete);

		return complete;
	}
	
	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious) {
		ArrayList<Extension> adm;
		ArrayList<Extension> preferred = new ArrayList<Extension>();

		if(!usePrevious || (previousAdmissibleExtensions == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute preferred extensions!", null));
			adm = getAdmissibleExtensions(usePrevious);
		}
		else{
			adm = previousAdmissibleExtensions;

			notification = "Using previously computed admissible extensions to compute preferred extensions: ";

			if(adm.size() == 0){
				notification += "There are no admissible extensions!";
			}
			else{
				notification += formatExtensions(adm);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(adm, "admissible extensions", "preferred extensions")){
			return null;
		}

		for(Extension e: adm){
			if(e.isPreferred(adm)){
				preferred.add(e);
			}
		}

		interactor.addToCommands(new Command("The preferred extensions are: " + formatExtensions(preferred), null));

		return preferred;
	}

	public ArrayList<Extension> getStableExtensions(boolean usePrevious) {
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

		interactor.addToCommands(new Command("The stable extensions are: " + formatExtensions(stable), null));

		return stable;
	}

	public Extension getGroundedExtension(boolean usePrevious) throws InvalidInputException{
		ArrayList<Extension> co;
		ArrayList<Argument> grounded = new ArrayList<Argument>();

		if(!usePrevious || (previousCompleteExtensions == null)){
			interactor.addToCommands(new Command("Computing complete extensions to compute the grounded extension!", null));
			co = getCompleteExtensions(usePrevious);
		}
		else{
			co = previousCompleteExtensions;

			notification = "Using previously computed complete extensions to compute the grounded extension: ";

			if(co.size() == 0){
				notification += "There are no complete extensions!";
			}
			else{
				notification += formatExtensions(co);
			}

			interactor.addToCommands(new Command(notification, null));
		}

		if(invalidityCheck(co, "complete extensions", "grounded extension")){
			return null;
		}
		
		if(co.size() == 1){
			interactor.addToCommands(new Command("The only complete extension " + co.get(0).format() + " is the grounded extension.", co.get(0).toInstruction(Color.GREEN)));
			return co.get(0);
		}

		for(Extension e: co){
			if(e.getArguments().size() == 0){
				interactor.addToCommands(new Command("Since there is a complete extension {}, the grounded extension is {}", null));
				return new Extension(grounded, this);
			}
		}

		for(Extension e: co){
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
					interactor.addToCommands(new Command("Since " + eFormat + " contains all the arguments of " + tmp.format() + ", our candidate doesn't change.", highlight));
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
	
	//TODO add interactor commands
	public ArrayList<Extension> getSemiStableExtensions(boolean usePrevious){
		ArrayList<Extension> admExt;
		
		if(!usePrevious || (previousAdmissibleExtensions == null)){
			admExt = getAdmissibleExtensions(usePrevious);
		}
		else{
			admExt = previousAdmissibleExtensions;
		}

		ArrayList<Extension> semiStable = new ArrayList<Extension>();
		ArrayList<ArrayList<Argument>> unions = new ArrayList<ArrayList<Argument>>();
		
		//generating R+(T)s
		for(Extension e: admExt){
			ArrayList<Argument> admArgs = new ArrayList<Argument>();
			admArgs.addAll(e.getArguments());
			ArrayList<Argument> unionArgs = new ArrayList<Argument>();
			for(Argument a: admArgs){
				for(Argument d: getAttackedBy(a.getName())){
					if(!admArgs.contains(d) && !unionArgs.contains(d)){
						unionArgs.add(d);
					}
				}
			}
			
			admArgs.addAll(unionArgs); //this should have R+(T) for one T now
			unions.add(admArgs); //this should have added one R+(T) now
		}
		
		//R+(S) is exactly the R+(T) at the position we are at and corresponds to e
		for(int i = 0;i<unions.size();i++){
			semiStable.add(admExt.get(i));
			//System.out.println("added: " + admExt.get(i).getArguments());
			for(int j = 0;j<unions.size();j++){
				if(i==j){
					continue;
				}
				else if(unions.get(j).containsAll(unions.get(i))){
					if(unions.get(j).equals(unions.get(i))){
						continue;
					}
					else{
						semiStable.remove(admExt.get(i));
						//System.out.println("removed: " + admExt.get(i).getArguments());
						break;
					}
				}
			}
		}
		
		// at this point we have added all extensions, then removed the non-semi-stable ones
		return semiStable;
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
			else{
				continue;
			}
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
