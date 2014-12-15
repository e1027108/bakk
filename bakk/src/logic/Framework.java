package logic;

import java.util.ArrayList;

public class Framework {
	private ArrayList<Argument> arguments;

	public Framework(ArrayList<Argument> arguments){
		this.arguments = arguments;
	}

	public ArrayList<Argument> getAttacks(char argumentName){
		ArrayList<Argument> attacks = new ArrayList<Argument>();
		String attackString = getArgument(argumentName).getAttacks();
		
		for(int i = 0;i<attackString.length();i++){
			attacks.add(getArgument(attackString.charAt(i)));
		}
		
		return attacks;
	}

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
}
