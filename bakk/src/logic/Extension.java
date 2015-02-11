package logic;

import java.util.ArrayList;

public class Extension {

	private ArrayList<Argument> arguments; //arguments of extension

	/**
	 * new Extension with a starting node
	 * @param a
	 */
	public Extension(Argument a){
		this.arguments = new ArrayList<Argument>();
		this.arguments.add(a);
	}

	public Extension(ArrayList<Argument> arguments){
		this.arguments = arguments; //TODO watch out for problems regarding flat copy
	}

	public void addArgument(Argument a){
		// TODO change so it fits all extension types
	}

	public boolean isAcceptable(Argument a){
		// TODO check argument acceptability in regards to framework and arguments already in extension
		return true;
	}

	public boolean isConflictFree(){
		String attacks = getAttacks();
		String names = getArgumentNames();

		for(int i = 0; i < names.length(); i++){
			String tmp = "" + names.charAt(i);
			if(attacks.contains(tmp)){
				return false;
			}
		}

		return true;
	}

	public boolean isAdmissible(Framework framework){
		String attacks = getAttacks();
		String attackers = "";
		//TODO for each argument, check if all the attackers are in getAttacks

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

	public boolean isComplete(){
		// TODO needed?
		return true;
	}

	public boolean isPreferrable(){
		// TODO check if extension is preferrable
		return true;
	}

	public boolean isStable(Framework framework){
		if(getAttacks().length() == (framework.getArguments().size()-getArgumentNames().length())){
			return true;
		}
		return false;
	}

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

	public String getArgumentNames(){
		String names = "";

		for(Argument a: arguments){
			names += a.getName(); 
		}

		return names;
	}

	/*
	 * The characteristic function, denoted by F_AF, of an argumentation framework 
	 * AF = <AR,attacks> is defined as follows:
	 * F_AF: 2^AR -> 2^AR
	 * F_AF(S) = { A | A is acceptable wrt S }
	 */
	public int getFixedPoint(Framework framework){ //TODO int?
		//TODO compute fixed point F_AF
		return 0;
	}
}
