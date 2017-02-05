package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.InvalidInputException;
import javafx.scene.paint.Color;

public class Framework {

	protected ArrayList<Argument> arguments;
	protected ArrayList<Attack> attacks;
	protected Interactor interactor;
	private ArrayList<Extension> previousConflictFreeSets; //a stored, previously computed set of conflict-free sets
	private ArrayList<Extension> previousAdmissibleExtensions; //a stored, previously computed set of admissible extensions
	private ArrayList<Extension> previousCompleteExtensions; //a stored, previously computed set of complete extensions
	private ArrayList<Extension> previousPreferredExtensions;
	private ArrayList<Extension> previousStableExtensions;
	private ArrayList<Extension> previousSemiStableExtensions;
	@SuppressWarnings("unused")
	private Extension previousGroundedExtension;
	private String notification;
	private int pane;
	
	public enum Type {cf,ad,co,pr,st,ss,gr,adstar,costar,grstar};
	private HashMap<Type,Kernel> kernel;
	
	/**
	 * An argumentation framework consists of arguments (denoted by letters) and attacks (denoted by pairs of arguments, denoted by letters)
	 * @param arguments a list of all arguments of the framework
	 * @param attacks a list of all attacks of the framework
	 * @param interactor the object that links the gui and logic computations
	 * @param pane which gui graph the interactor should interact with
	 */
	public Framework(ArrayList<Argument> arguments, ArrayList<Attack> attacks, Interactor interactor, int pane) {
		this.arguments = arguments;
		this.attacks = attacks;
		this.interactor = interactor;
		this.kernel = new HashMap<Type,Kernel>();
		this.pane = pane;
	}
	
	/**
	 * computes all conflict-free sets of the framework
	 * these are sets of arguments where no argument attacks another inside the set
	 * @return set of all conflict-free sets of the framework
	 */
	public ArrayList<Extension> getConflictFreeSets() {
		ArrayList<Extension> conflictFreeSets = new ArrayList<Extension>();
		ArrayList<ArrayList<Argument>> powerset;

		if(arguments == null){
			interactor.addToCommands(new Command("No arguments found, error!", null, pane));
			return null;
		}

		interactor.addToCommands(new Command("Computing conflict-free sets!", null, pane));

		powerset = getAllSubsets(arguments);

		for(ArrayList<Argument> set: powerset){
			Extension tmp = new Extension(set, this);
			if(tmp.isConflictFree(true)){
				conflictFreeSets.add(tmp);
			}
		}

		interactor.addToCommands(new Command("The conflict-free sets are: " + formatExtensions(conflictFreeSets), null, pane));

		previousConflictFreeSets = new ArrayList<Extension>();
		previousConflictFreeSets.addAll(conflictFreeSets);

		return conflictFreeSets;
	}
	
	/**
	 * computes the powerset of a given argument set
	 * uses a binary method over the amount of elements in the original set
	 * @param set the set we want the powerset from
	 * @return the powerset of the given set
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
	 * computes all admissible extensions (sets) of the framework
	 * these are all conflict-free sets, that defend themselves against all outside attacks
	 * @param usePrevious whether to use previous computation results or compute the needed sets anew
	 * @return a list of all admissible extensions of the framework
	 */
	public ArrayList<Extension> getAdmissibleExtensions(boolean usePrevious) {
		ArrayList<Extension> cf;
		ArrayList<Extension> admissible = new ArrayList<Extension>();

		if(!usePrevious || (previousConflictFreeSets == null)){
			interactor.addToCommands(new Command("Computing conflict-free sets to compute admissible extensions!", null, pane));
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

			interactor.addToCommands(new Command(notification, null, pane));
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
		interactor.addToCommands(new Command(notification, null, pane));

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
	private boolean invalidityCheck(ArrayList<Extension> list, String cause, String effect){
		if(list == null || list.isEmpty()){ //shouldn't be possible
			String are = ", there are no ";

			if(effect.contains("grounded")){
				are = are.replace("are", "is");
			}

			interactor.addToCommands(new Command("Since there are no " + cause + are + effect + "!", null, pane));
			return true;
		}
		return false;
	}
	
	/**
	 * computes all complete extensions of the framework
	 * these are all admissible extensions that contain all arguments they defend
	 * @param usePrevious whether or not to take previous computation results for this computation or re-compute those
	 * @return a list of all complete extensions of the framework
	 */
	public ArrayList<Extension> getCompleteExtensions(boolean usePrevious){
		ArrayList<Extension> adm;
		ArrayList<Extension> complete = new ArrayList<Extension>();

		if(!usePrevious || (previousAdmissibleExtensions == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute complete extensions!", null, pane));
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

			interactor.addToCommands(new Command(notification, null, pane));
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
		
		interactor.addToCommands(new Command(notification, null, pane));

		previousCompleteExtensions = new ArrayList<Extension>();
		previousCompleteExtensions.addAll(complete);

		return complete;
	}
	
	/**
	 * computes all preferred extensions of the framework
	 * these are all admissible extensions which defend themselves against outside attacks
	 * @param usePrevious whether or not to take previous computation results for this computation or re-compute those
	 * @return a list of all preferred extensions of the framework
	 */
	public ArrayList<Extension> getPreferredExtensions(boolean usePrevious) {
		ArrayList<Extension> adm;
		ArrayList<Extension> preferred = new ArrayList<Extension>();

		if(!usePrevious || (previousAdmissibleExtensions == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute preferred extensions!", null, pane));
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

			interactor.addToCommands(new Command(notification, null, pane));
		}

		if(invalidityCheck(adm, "admissible extensions", "preferred extensions")){
			return null;
		}

		for(Extension e: adm){
			if(e.isPreferred(adm)){
				preferred.add(e);
			}
		}

		interactor.addToCommands(new Command("The preferred extensions are: " + formatExtensions(preferred), null, pane));

		previousPreferredExtensions = new ArrayList<Extension>();
		previousPreferredExtensions.addAll(preferred);
		
		return preferred;
	}

	/**
	 * Computes the stable extensions of the framework, therefore we
	 * 	look at all conflict-free sets that attack all arguments they do not contain themselves
	 * @param usePrevious whether to use previously computed sets for this computation or to compute them anew
	 * @return the set of all stable extensions of the framework
	 */
	public ArrayList<Extension> getStableExtensions(boolean usePrevious) {
		ArrayList<Extension> cf;
		ArrayList<Extension> stable = new ArrayList<Extension>();

		if(!usePrevious || (previousConflictFreeSets == null)){
			interactor.addToCommands(new Command("Computing conflict-free sets to compute stable extensions!", null, pane));
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

			interactor.addToCommands(new Command(notification, null, pane));
		}

		if(invalidityCheck(cf, "conflict-free sets", "stable extensions")){
			return null;
		}

		for(Extension e: cf){
			if(e.isStable()){
				stable.add(e);
			}
		}

		interactor.addToCommands(new Command("The stable extensions are: " + formatExtensions(stable), null, pane));

		previousStableExtensions = new ArrayList<Extension>();
		previousStableExtensions.addAll(stable);
		
		return stable;
	}

	/**
	 * computes the unique grounded extension of the framework
	 * 	the maximal complete extension is searched, since it is the grounded extension
	 * @param usePrevious whether or not to use previous results
	 * @return the grounded Extension
	 */
	public Extension getGroundedExtension(boolean usePrevious){
		ArrayList<Extension> co;
		ArrayList<Argument> grounded = new ArrayList<Argument>();

		if(!usePrevious || (previousCompleteExtensions == null)){
			interactor.addToCommands(new Command("Computing complete extensions to compute the grounded extension!", null, pane));
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

			interactor.addToCommands(new Command(notification, null, pane));
		}

		if(invalidityCheck(co, "complete extensions", "grounded extension")){
			return null;
		}
		
		if(co.size() == 1){
			interactor.addToCommands(new Command("The only complete extension " + co.get(0).format() + " is the grounded extension.", co.get(0).toInstruction(Color.GREEN), pane));
			return co.get(0);
		}

		for(Extension e: co){
			if(e.getArguments().size() == 0){
				interactor.addToCommands(new Command("Since there is a complete extension {}, the grounded extension is {}", null, pane));
				return new Extension(grounded, this);
			}
		}

		for(Extension e: co){
			String eFormat = e.format();

			if(grounded.isEmpty()){ //if no elements in grounded, e is first extension
				grounded.addAll(e.getArguments());
				interactor.addToCommands(new Command("The extension " + eFormat + " is the first candidate for grounded extension.", e.toInstruction(Color.GREEN), pane));
			}
			else{ //else check for common elements in extension
				ArrayList<Argument> missing = new ArrayList<Argument>();
				String missingString = "";

				GraphInstruction highlight = new GraphInstruction(new ArrayList<SingleInstruction>(), new ArrayList<SingleInstruction>(), pane);

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
							". Therefore our new candidate is " + tmp.format(), highlight, pane));
				}
				else{
					interactor.addToCommands(new Command("Since " + eFormat + " contains all the arguments of " + tmp.format() + ", our candidate doesn't change.", highlight, pane));
				}

				if(grounded.isEmpty()){
					break;
				}
			}
		}

		Extension groundedExtension = new Extension(grounded,this);
		@SuppressWarnings("unused")
		Extension previousGroundedExtension = new Extension(grounded,this); //I want two objects, because the first one might be replaced/lost

		interactor.addToCommands(new Command("The grounded extension is: " + groundedExtension.format(), groundedExtension.toInstruction(Color.GREEN), pane));

		return groundedExtension;
	}

	/**
	 * gets all semi-stable extensions of the framework
	 * 	for that purpose we check for every admissible extension the following:
	 * 		we compute for the extension e, e+
	 * 			e+ contains all arguments in R that are attacked by an argument in e, and the whole of e
	 * 		we compute f+ for all other extensions f
	 * 		an extension e is semi-stable if for all f+, e+ is not a proper subset of f+
	 * @param usePrevious whether previous results are used (true) or re-computed (false)
	 * @return a list of all semi-stable extensions of the framework
	 */
	public ArrayList<Extension> getSemiStableExtensions(boolean usePrevious){
		ArrayList<Extension> admExt;
		
		if(!usePrevious || (previousAdmissibleExtensions == null)){
			interactor.addToCommands(new Command("Computing admissible extensions to compute semi-stable extensions!",null, pane));
			admExt = getAdmissibleExtensions(usePrevious);
		}
		else{
			admExt = previousAdmissibleExtensions;
			
			notification = "Using previously computed admissible extensions to compute semi-stable extensions: ";
			
			if(admExt.size() == 0){
				notification += "There are no admissible extensions.";
			}
			else{
				notification += formatExtensions(admExt);
			}
			
			interactor.addToCommands(new Command(notification,null, pane));
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
			
			Extension toInst = new Extension(admArgs,this);
			GraphInstruction unionInst = toInst.toInstruction(Color.GREEN); //everything is green, then attacked are blue

			for(Argument a: unionArgs){
				unionInst.getNodeInstructions().add(new SingleInstruction(a.getName(),Color.BLUE));
			}
			
			interactor.addToCommands(new Command("The admissible extension " + e.format() + " attacks the arguments " + (new Extension(unionArgs,this)).format() + " -> "
					+ "R+(" + e.format() + ") = " + toInst.format() + ".", unionInst, pane)); 
		}
		
		//R+(S) is exactly the R+(T) at the position we are at and corresponds to e
		for(int i = 0;i<unions.size();i++){
			semiStable.add(admExt.get(i));
			Extension iExt = new Extension(unions.get(i),this);
			
			boolean removed = false;
			
			for(int j = 0;j<unions.size();j++){
				if(i==j){
					continue;
				}
				else if(unions.get(j).containsAll(unions.get(i))){
					if(unions.get(j).equals(unions.get(i))){
						continue;
					}
					else{
						Extension cmpExt = new Extension(unions.get(j),this);
						
						GraphInstruction coloring = cmpExt.toInstruction(Color.BLUE);
						GraphInstruction overcoloring = iExt.toInstruction(Color.GREEN);
						coloring.getNodeInstructions().addAll(overcoloring.getNodeInstructions());
						
						interactor.addToCommands(new Command(admExt.get(i).format() + " is not a semi-stable extension, since R+(" + admExt.get(i).format() + 
								") = " + iExt.format() + " is a subset of R+(" + admExt.get(j).format() + ") = " + cmpExt.format() + ".", coloring, pane));
						
						semiStable.remove(admExt.get(i));
						removed = true;
						break;
					}
				}
			}
			
			if(!removed){
				//there are duplicate colorings that are just changed because of instruction order
				GraphInstruction coloring = iExt.toInstruction(Color.GREEN);
				
				interactor.addToCommands(new Command("The extension " + admExt.get(i).format() + " is semi-stable, since R+(" + admExt.get(i).format() + 
						") = " + iExt.format() + " is maximal.",coloring, pane));
			}
		}
		
		// at this point we have added all extensions, then removed the non-semi-stable ones
		interactor.addToCommands(new Command("The semi-stable extensions are: " + formatExtensions(semiStable) + ".", null, pane));

		previousSemiStableExtensions = new ArrayList<Extension>();
		previousSemiStableExtensions.addAll(semiStable);
		
		return semiStable;
	}
	
	/**
	 * returns the kernel wrt the type (semantic)
	 * @param type type of kernel (admissible, stable, etc.)
	 * @param usePrevious whether to show computations, that are already done, again
	 * @return the computed kernel
	 * @throws InvalidInputException if an invalid semantics was specified
	 */
	public Kernel getKernel(Type type, boolean usePrevious) throws InvalidInputException{
		Kernel ret = kernel.get(type);		
		if(ret != null && usePrevious){
			return ret;
		}
		else{
			computeKernel(type);
			return getKernel(type, true);
		}
	}

	/**
	 * computes a kernel wrt the given type	
	 * @param type type of kernel (admissible, stable, etc.)
	 * @throws InvalidInputException if no valid type was given (not all types have kernels)
	 */
	private void computeKernel(Type type) throws InvalidInputException {
		kernel.put(type, new Kernel(this,interactor,type, pane)); //Kernel computes itself in constructor
	}

	/**
	 * filters a formatted string of arguments and turns them into instructions to format them green
	 * @param item (formatted) string representation of argument list
	 * @return list of green node instructions
	 */
	public GraphInstruction getNodeInstructionsFromArgumentList(String item, Color color) {
		GraphInstruction instruction = new GraphInstruction(new ArrayList<SingleInstruction>(), null, pane);		
		item = item.replace("{", "");
		item = item.replace("}", "");

		for(int i = 0; i < item.length(); i++){
			instruction.getNodeInstructions().add(new SingleInstruction(String.valueOf(item.charAt(i)), color));
		}

		return instruction;
	}

	/**
	 * returns an argument by name
	 * @param name
	 * @return an argument
	 */
	public static Argument getArgument(ArrayList<Argument> list, char name) {
		if(list != null){
			for(Argument a: list){
				if(a.getName() == name){
					return a;
				}
			}
		}
		
		return null;
	}	
	
	/**
	 * for an argument returns a list of its attacks
	 * @param attacker name of the attacker
	 * @return list of attacks
	 */
	public ArrayList<Attack> getAttacks(char attacker) {
		ArrayList<Attack> argumentAttacks = new ArrayList<Attack>();
		
		for(Attack a: attacks){
			if(a.getAttacker().getName() == attacker){
				argumentAttacks.add(a);
			}
		}
		
		return argumentAttacks;
	}
	
	/**
	 * for an argument returns a list of arguments that it attacks
	 * @param attacker name of the attacker
	 * @return a list of attacked arguments
	 */
	public ArrayList<Argument> getAttackedBy(char attacker) {
		ArrayList<Argument> attacked = new ArrayList<Argument>();
		
		for(Attack att: getAttacks(attacker)){
			attacked.add(att.getAttacked());
		}
		
		return attacked;
	}

	/**
	 * combines two given frameworks and returns their union 
	 * @param framework the first framework to combine, acts as the base framework (its interactor and pane are used)
	 * @param expansion the second framework to combine, acts as an expansion to the base
	 * @return the union of the frameworks (using the bases interactor and pane information)
	 */
	public static Framework expandFramework(Framework framework, Framework expansion) {
		ArrayList<Argument> tmpArguments = new ArrayList<Argument>();
		ArrayList<Attack> tmpAttacks = new ArrayList<Attack>();

		tmpArguments.addAll(framework.getArguments());
		tmpAttacks.addAll(framework.getAttacks());

		boolean add;

		if(expansion != null && expansion.getArguments() != null && expansion.getAttacks() != null){
			for(Argument nar : expansion.getArguments()){
				add = true;
				for(Argument old : tmpArguments){
					if(nar.equals(old)){
						add = false;
						break;
					}
				}
				if(add){
					tmpArguments.add(nar);
				}
			}

			for(Attack nat : expansion.getAttacks()){
				add = true;
				for(Attack old : tmpAttacks){
					if(nat.equals(old)){
						add = false;
						break;
					}
				}
				if(add){
					tmpAttacks.add(nat);
				}
			}
		}
		
		return new Framework(tmpArguments, tmpAttacks, framework.getInteractor(), framework.getPane());
	}
	
	/**
	 * returns a graphinstruction for this framework, turning each argument and attack the specified color
	 * @param color a color to color the framework in
	 * @return a set of coloring instructions (called GraphInstruction)
	 */
	protected GraphInstruction toInstruction(Color color){
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: arguments){
			nodeInstructions.add(new SingleInstruction(a.getName(),color));
		}
		for(Attack a: attacks){
			edgeInstructions.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),color));
		}

		return new GraphInstruction(nodeInstructions,edgeInstructions,pane);
	}
	
	/**
	 * checks whether a given argument is also present in this framework
	 * @param b the given argument
	 * @return argument present
	 */
	public boolean contains(Argument b){
		for(Argument a: this.arguments){
			if(a.equals(b)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks whether a given attack is also present in this framework
	 * @param b the given attack
	 * @return attack present
	 */
	public boolean contains(Attack b){
		for(Attack a: this.attacks){
			if(a.equals(b)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * formats a string of argument names to be a readable list in a sentence
	 * @param input the string to be formatted
	 * @return the formatted string (now a list, separated by ',' and an 'and' between the last two elements)
	 */
	protected static String formatNameList(String input) {
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

	/**
	 * formats a given list of arguments to be more easily readable
	 * @param input
	 * @return
	 */
	public static String formatArgumentList(ArrayList<Argument> input) {
		String argNames = "";
		
		for(Argument a: input){
			argNames += a.getName();
		}
		
		return formatNameList(argNames);
	}
	
	/**
	 * formats a specified list of attackers or defenders as a list in string format	
	 * @param input attacks to list
	 * @param pos whether to format the list of attackers or defenders (1 attackers, 2 defenders)
	 * @return string represenation of attacker/defender list
	 */
	public static String formatAttackerList(ArrayList<Attack> input,int pos) {
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
	 * formats a given list of attacks to be easily readable
	 * @param attacklist the list of attacks to be formatted
	 * @return a formatted string of the given attacks
	 */
	protected static String formatAttackList(ArrayList<Attack> attacklist) {
		String formatted = "{";
		
		for(Attack a: attacklist){
			formatted += "(" + a.getAttacker().getName()+a.getAttacked().getName() + "),";
		}
		
		formatted += "}";
		formatted = formatted.replace(",}", "}");
		
		return formatted;
	}
	
	/**
	 * returns a comma separated string for a list of extensions (which are formatted themselves)
	 * @param extensions a list of extensions
	 * @return a string list representation of extensions
	 */
	protected static String formatExtensions(ArrayList<Extension> extensions) {
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
	 * checks whether ther are no arguments and no attacks within this framework
	 * @return true if no argument and no attacks, else false
	 */
	public boolean isEmpty(){
		if(attacks.size() == 0 && arguments.size() == 0){
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
	 * for an attack creates a coloring instruction
	 * @param item the attack as a string AB
	 * @param color the color the attack should be shown
	 * @return the instruction coloring the attack
	 */
	public SingleInstruction getEdgeInstructionFromAttack(String item,Color color){
		return new SingleInstruction(item,color);
	}
	
	public ArrayList<Argument> getArguments() {
		return arguments;
	}

	public ArrayList<Attack> getAttacks(){
		return attacks;
	}
	
	public int getPane() {
		return pane;
	}
	
	public Interactor getInteractor(){
		return interactor;
	}
}
