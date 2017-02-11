package logic;

import java.util.ArrayList;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;
import javafx.scene.paint.Color;

public class Expansion {

	private Framework framework, expansion;
	private Interactor interactor;
	private int pane;
	
	public Expansion(Framework framework, Framework expansion){
		this.framework = framework;
		this.expansion = expansion;
		this.interactor = framework.getInteractor();
		this.pane = framework.getPane();
	}
	
	/**
	 * computes the type of expansion the expanding framework is wrt the base framework
	 * @param framename name of the base framework
	 * @param expname name of the expansion
	 * @return the name of the expansion type (or null, if not an expansion at all)
	 */
	public String determineExpansionType(String framename, String expname){
		if(expansion == null || expansion.isEmpty()){
			interactor.addToCommands(new Command(expname + " doesn't contain any arguments or attacks, it therefore is not an expansion.",null,pane));
			return null;
		}
		else{
			interactor.addToCommands(new Command(expname + " does contain arguments (" + Framework.formatArgumentList(expansion.getArguments()) + ") or attacks (" 
					+ Framework.formatAttackList(expansion.getAttacks()) + "), it may be an expansion.",expansion.toInstruction(Color.GREEN),pane));
		}
		
		ArrayList<Argument> existingArgs = getExistingArguments();
		ArrayList<Attack> existingAtts = getExistingAttacks();
		ArrayList<SingleInstruction> argins, attins;
		
		if(!existingArgs.isEmpty() && !existingAtts.isEmpty()){
			attins = new ArrayList<SingleInstruction>();
			argins = new ArrayList<SingleInstruction>();
			for(Argument a: existingArgs){
				argins.add(new SingleInstruction(a.getName(),Color.RED));
			}
			for(Attack a: existingAtts){
				attins.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
			}
			
			interactor.addToCommands(new Command(expname + " does not constitute an expansion, since the arguments " + Framework.formatArgumentList(existingArgs) + " and the attacks " +
					Framework.formatAttackList(existingAtts) + " already exist in " + framename + ".",new GraphInstruction(argins,attins,pane),pane));
			
			return null;
		}
		else if(existingArgs.isEmpty()){
			Extension tmp = new Extension(expansion.getArguments(),expansion);
			interactor.addToCommands(new Command(expname + " does only contain new arguments and therefore may be an expansion.",tmp.toInstruction(Color.GREEN),pane));
		}
		else{
			attins = new ArrayList<SingleInstruction>();
			for(Attack a: expansion.getAttacks()){
				attins.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.GREEN));
			}
			interactor.addToCommands(new Command(expname + " does only contain new attacks and therefore may be an expansion",new GraphInstruction(null,attins,pane),pane));
		}
		
		interactor.addToCommands(new Command("We now check whether " + expname + " is a strong expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkStrongExpansion(framename,expname)){ //interactor commands in method
			return "strong";
		}
		
		interactor.addToCommands(new Command("We now check whether " + expname + " is a weak expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkWeakExpansion(framename,expname)){ //interactor commands in method
			return "weak";
		}
		
		interactor.addToCommands(new Command("We now check whether " + expname + " is a normal expansion.",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkNormalExpansion(expname)){ //interactor commands in method
			return "normal";
		}
		
		interactor.addToCommands(new Command("We now check whether " + expname + " is a local expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkLocalExpansion(expname)){ //interactor commands in method
			return "local";
		}
		
		interactor.addToCommands(new Command(expname + " does not fit any expansion type and is therefore not of any expansion type.",expansion.toInstruction(Color.RED),pane));
		return null;
	}

	/**
	 * checks if the expanding framework constitutes a normal expansion to the base framework
	 * for that purpose we check the following:
	 * 	the exp does need non-empty arguments
	 * 	each of exp's attacks needs to either start or end within the exp
	 * @param expname the name of the expanding framework
	 * @return whether the expanding framework is a normal expansion
	 */
	private boolean checkNormalExpansion(String expname) {
		if(expansion.getArguments().isEmpty()){
			interactor.addToCommands(new Command(expname + " is not a normal expansion since it does not contain any arguments.",null,pane));
			return false;
		}
		
		ArrayList<SingleInstruction> nodeins, edgeins;
		for(Attack a: expansion.getAttacks()){
			edgeins = new ArrayList<SingleInstruction>();
			nodeins = new ArrayList<SingleInstruction>();
			if(Framework.getArgument(expansion.getArguments(),a.getAttacker().getName()) == null && Framework.getArgument(expansion.getArguments(),a.getAttacked().getName()) == null){
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.RED));
				
				interactor.addToCommands(new Command(expname + " is not a normal expansion since its attack (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") does not interact with arguments within it.",
						new GraphInstruction(nodeins,edgeins,pane),pane));
				return false;
			}
			else{
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.GREEN));
				
				interactor.addToCommands(new Command("The attack (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") interacts with " + expname + ".",new GraphInstruction(nodeins,edgeins,pane),pane));
			}
		}
		
		interactor.addToCommands(new Command("All its attacks interact with " + expname + ", therefore it is a normal expansion.",expansion.toInstruction(Color.GREEN),pane));
		return true;
	}

	/**
	 * checks if the expanding framework constitutes a strong expansion to the base framework
	 * this is true if the arguments of the expanding framework are a normal expansion to the arguments of the base framework and
	 * all the attacks of the expanding framework are not attacks from the base framework's arguments onto expanding framework arguments
	 * @param expname the name of the expanding framework
	 * @return whether or not the expanding framework is a strong expansion to the base framework
	 */
	private boolean checkStrongExpansion(String framename, String expname) {
		Framework fArgs = new Framework(framework.getArguments(),new ArrayList<Attack>(),interactor,framename,pane);
		Framework eArgs = new Framework(expansion.getArguments(),new ArrayList<Attack>(),interactor,expname,pane);
		
		GraphInstruction fArgIns = fArgs.toInstruction(Color.GREEN);
		GraphInstruction eArgIns = eArgs.toInstruction(Color.BLUE);
		fArgIns.getNodeInstructions().addAll(eArgIns.getNodeInstructions());
		
		interactor.addToCommands(new Command("We check whether the arguments of " + expname + " are a normal expansion to the arguments of " + framename + ".",fArgIns,pane));
		
		if(!checkNormalExpansion(expname + " (arguments only)")){
			interactor.addToCommands(new Command("Since the arguments of " + expname + " are not a normal expansion of the argument of " + framename + ", " + expname + " is not a strong expansion.",
					expansion.toInstruction(Color.RED),pane));
			return false;
		}
		
		interactor.addToCommands(new Command("We check now, if all attacks of " + expname + " are not attacks from " + framename + " onto " + expname + ".",null,pane));
		
		ArrayList<SingleInstruction> edgeins,nodeins;
		for(Attack a: expansion.getAttacks()){
			edgeins = new ArrayList<SingleInstruction>();
			nodeins = new ArrayList<SingleInstruction>();
			if(!(framework.contains(a.getAttacker()) && expansion.contains(a.getAttacked()))){
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.GREEN));
				
				interactor.addToCommands(new Command("The attack (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") does not attack from " + framename + " into " + expname + ".",
						new GraphInstruction(nodeins,edgeins,pane),pane));
			}
			else{
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.RED));
				
				interactor.addToCommands(new Command(expname + " is not a strong expansion, since (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") attacks from " + framename + " into " + expname + ".",
						new GraphInstruction(nodeins,edgeins,pane),pane));
				return false;
			}
		}
		
		interactor.addToCommands(new Command(expname + " is a strong expansion, since none of the attacks onto it come from " + framename + ".",expansion.toInstruction(Color.GREEN),pane));
		return true;
	}

	/**
	 * checks whether the expanding framework is a weak expansion of the base framework
	 * this is true if the arguments of the expanding framework are a normal expansion to the arguments of the base framework and
	 * all the attacks of the expanding framework are not attacks from the expanding framework's arguments onto base framework arguments 
	 * @param framename the name of the base framework
	 * @param expname the name of the expanding framework
	 * @return whether the expanding framework is a weak expansion of the base framework
	 */
	private boolean checkWeakExpansion(String framename, String expname) {
		Framework fArgs = new Framework(framework.getArguments(),new ArrayList<Attack>(),interactor,framename,pane);
		Framework eArgs = new Framework(expansion.getArguments(),new ArrayList<Attack>(),interactor,expname,pane);
		
		GraphInstruction fArgIns = fArgs.toInstruction(Color.GREEN);
		GraphInstruction eArgIns = eArgs.toInstruction(Color.BLUE);
		fArgIns.getNodeInstructions().addAll(eArgIns.getNodeInstructions());
		
		interactor.addToCommands(new Command("We check whether the arguments of " + expname + " are a normal expansion to the arguments of " + framename + ".",fArgIns,pane));
		
		if(!checkNormalExpansion(expname + " (arguments only)")){
			interactor.addToCommands(new Command("Since the arguments of " + expname + " are not a normal expansion of the argument of " + framename + ", " + expname + " is not a strong expansion.",
					expansion.toInstruction(Color.RED),pane));
			return false;
		}
		
		interactor.addToCommands(new Command("We check now, if all attacks of " + expname + " are not attacks from " + expname + " onto " + framename + ".",null,pane));
		
		ArrayList<SingleInstruction> edgeins,nodeins;
		for(Attack a: expansion.getAttacks()){
			edgeins = new ArrayList<SingleInstruction>();
			nodeins = new ArrayList<SingleInstruction>();
			if(!(framework.contains(a.getAttacked()) && expansion.contains(a.getAttacker()))){
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.GREEN));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.GREEN));
				
				interactor.addToCommands(new Command("The attack (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") does not attack from" + expname + " onto " + framename + ".",
						new GraphInstruction(nodeins,edgeins,pane),pane));
			}
			else{
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacker().getName(),Color.RED));
				nodeins.add(new SingleInstruction(a.getAttacked().getName(),Color.RED));
				
				interactor.addToCommands(new Command(expname + " is not a weak expansion, since (" + a.getAttacker().getName() + "," + a.getAttacked().getName() + ") attacks from " + expname + " onto " + framename + ".",
						new GraphInstruction(nodeins,edgeins,pane),pane));
				return false;
			}
		}
		
		interactor.addToCommands(new Command(expname + " is a weak expansion, since none of its attacks from its arguments attack " + framename + ".",expansion.toInstruction(Color.GREEN),pane));
		return true;
	}

	/**
	 * checks whether the expanding framework is a local expansion of the base framework
	 * which it only is if it does not contain any arguments.
	 * @param name the name of the expanding framework
	 * @return whether the expanding framework is a local expansion of the base framework
	 */
	private boolean checkLocalExpansion(String name) {
		if(expansion.getArguments().isEmpty()){
			interactor.addToCommands(new Command(name + " is a local expansion, since it does not contain any arguments.",expansion.toInstruction(Color.GREEN),pane));
			return true;
		}
		else{
			Extension tmp = new Extension(expansion.getArguments(),expansion);
			
			interactor.addToCommands(new Command(name + " is not a local expansion, since it contains arguments: " + tmp.format() + ".",tmp.toInstruction(Color.RED),pane));
			return false;
		}
	}

	/**
	 * finds all of the expansions' arguments that the framework already has
	 * @return a list of arguments
	 */
	private ArrayList<Argument> getExistingArguments(){
		ArrayList<Argument> existing = new ArrayList<Argument>();
		
		for(Argument a: expansion.getArguments()){
			if(framework.contains(a)){
				existing.add(a);
			}
		}
		
		return existing;
	}
	
	/**
	 * finds all of the expansions' attacks that the framework already has
	 * @return a list of attacks
	 */
	private ArrayList<Attack> getExistingAttacks() {
		ArrayList<Attack> existing = new ArrayList<Attack>();
		
		for(Attack a: expansion.getAttacks()){
			if(framework.contains(a)){
				existing.add(a);
			}
		}
		
		return existing;
	}
}
