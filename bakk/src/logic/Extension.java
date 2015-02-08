package logic;

import java.util.ArrayList;

public class Extension {
	
	private ArrayList<Argument> arguments; //arguments of extension
	
	/**
	 * new Extension with a starting node
	 * @param a
	 */
	public Extension (Argument a){
		this.arguments = new ArrayList<Argument>();
		this.arguments.add(a);
	}
	
	public Extension (ArrayList<Argument> arguments){
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
	
	public boolean isAdmissible(){
		// TODO check if extension is admissible
		return true;
	}
	
	public boolean isComplete(){
		// TODO check if extension is complete
		return true;
	}
	
	public boolean isPreferrable(){
		// TODO check if extension is preferrable
		return true;
	}
	
	public boolean isStable(){
		// TODO check if extension is stable
		return true;
	}
	
	public boolean isGrounded(){
		// TODO check if extension is grounded
		return true;
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
}
