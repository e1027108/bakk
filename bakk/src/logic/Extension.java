package logic;

import java.util.ArrayList;

public class Extension {
	
	private ArrayList<Argument> arguments; //arguments of extension
	private Framework framework;
	
	public Extension (Framework framework){
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
	}
	
	/**
	 * new Extension with a starting node
	 * @param a
	 */
	public Extension (Argument a, Framework framework){
		this.arguments = new ArrayList<Argument>();
		this.arguments.add(a);
		this.framework = framework;
	}
	
	public void addArgument(Argument a){
		// TODO change so it fits all extension types
	}
	
	public boolean isAcceptable(Argument a){
		// TODO check argument acceptability in regards to framework and arguments already in extension
		return true;
	}
	
	public boolean isConflictFree(){
		// TODO check if Extension is conflict-free
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
}
