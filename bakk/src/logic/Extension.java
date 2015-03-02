package logic;

import java.util.ArrayList;

public class Extension {

	private ArrayList<Argument> arguments; //arguments of extension
	private Framework framework; //framework from which the extension is derived

	/**
	 * creates a new Extension with a starting node
	 * @param framework is the framework from which the extension is derived
	 * @param a is the starting argument
	 */
	public Extension(Argument a, Framework framework){
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		addArgument(a);
	}

	/**
	 * creates a complete Extension
	 * @param framework is the framework from which the extension is derived
	 * @param arguments is a set of Arguments
	 */
	public Extension(ArrayList<Argument> arguments, Framework framework){
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		this.arguments.addAll(arguments);
	}

	/**
	 * adds an argument to the Extension
	 * @param a is the argument to be added
	 */
	public void addArgument(Argument a){
		if(!framework.getArguments().contains(a)){
			throw new IllegalArgumentException("Argument is not in framework!"); //TODO handle
		}
		
		if(!arguments.contains(a)){
			arguments.add(a);
		}
		else{
			System.out.println("Already in there!"); //TODO maybe replace
		}
	}
	
	/**
	 * checks if an extension is conflict-free
	 * @details checks if no argument in the extension attacks another
	 * @return if the extension is conflict-free
	 */
	public boolean isConflictFree(){
		String attacks = getAttacks();
		String names = getArgumentNames();

		for(int i = 0; i < names.length(); i++){
			String tmp = "" + names.charAt(i);
			if(attacks.contains(tmp)){
				framework.addToInteractor(this.format() + " attacks the argument " + tmp + ", thus it is not a conflict-free set!");
				return false;
			}
		}

		framework.addToInteractor(this.format() + " is a conflict-free set, because it does not attack its own arguments");
		return true;
	}

	/**
	 * checks if a conflict-free set is admissible
	 * @details checks if all attackers are included in the attacks
	 * @param framework is the framework from which the extension is derived
	 * @return if all the arguments are defended
	 */
	public boolean isCFAdmissible(){
		String attacks = getAttacks();
		String attackers = "";
		
		for(Argument a: arguments){
			String tmp = "";
			for(Argument a2: framework.getAttackers(a.getName())){
				tmp += a2.getName();
			}

			attackers += tmp;
		}

		while(attackers.length() > 0){
			String attacker = String.valueOf(attackers.charAt(attackers.length()-1));

			if(attacks.contains(attacker)){
				attackers = attackers.replaceAll(attacker, "");
			}
			else{
				return false;
			}
		}

		return true;
	}

	/**
	 * checks if the extension is preferred of the given ones
	 * @param admissible the set of admissible extensions of which the extension may be preferred
	 * @return if the extension is a true subset of another
	 */
	public boolean isPreferred(ArrayList<Extension> admissible){
		for(Extension e: admissible){
			if(e.equals(this)){
				continue;
			}
			else if(isSubsetOf(e)){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * checks if the extension is a sub-extension of the given one
	 * @param e the given extension
	 * @return if all elements of the extension are also in the given extension
	 */
	private boolean isSubsetOf(Extension e) {
		ArrayList<Argument> extArg = e.getArguments();
		
		for(Argument a: arguments){
			if(!extArg.contains(a)){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * checks if the extension in stable
	 * @return if every argument outside the extension is attacked
	 */
	public boolean isStable(){
		if(getAttacks().length() == (framework.getArguments().size()-getArgumentNames().length())){
			return true;
		}
		return false;
	}

	/**
	 * computes all attacks of the extension
	 * @return a String list of arguments that the extension attacks
	 */
	public String getAttacks(){
		String tmp = "";
		String attacks = "";

		for(Argument a: arguments){
			tmp += a.getAttacks();
		}

		for(int i = 0; i<tmp.length(); i++){
			if(!attacks.contains(String.valueOf(tmp.charAt(i)))){
				attacks += tmp.charAt(i);
			}
		}

		return attacks;
	}

	/**
	 * computes a list of all arguments
	 * @return a String list of arguments in the extension
	 */
	public String getArgumentNames(){
		String names = "";

		for(Argument a: arguments){
			names += a.getName(); 
		}

		return names;
	}
	
	/**
	 * formats the extension to a user-friendly string format
	 * @return a list of argument names (separated by ',' between '{' and '}')
	 */
	public String format() {
		String formatted = "{";
		
		for(Argument a: arguments){
			formatted += a.getName() + ", ";
		}
		
		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}
		
		formatted += "}";
		
		return formatted;
	}
	
	public ArrayList<Argument> getArguments(){
		return arguments;
	}
}
