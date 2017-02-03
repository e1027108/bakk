package logic;

import java.util.ArrayList;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;
import javafx.scene.paint.Color;
import logic.Framework.Type;

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
	
	//TODO return local, weak, strong, normal expansion (as string)
	/**
	 * computes the type of expansion the expanding framework is wrt the base framework
	 * @return the name of the expansion type (or null, if not an expansion at all)
	 */
	public String determineExpansionType(){
		if(expansion.isEmpty()){
			interactor.addToCommands(new Command("This framework doesn't contain any arguments or attacks, it therefore is not an expansion.",null,pane));
			return null;
		}
		else{
			interactor.addToCommands(new Command("This framework does contain arguments or attacks, it may be an expansion.",expansion.toInstruction(Color.GREEN),pane));
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
				attins.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.RED));
			}
			
			interactor.addToCommands(new Command("The expanding framework does not constitute an expansion, since the arguments " + Framework.formatArgumentList(existingArgs) + " and the attacks " +
					Framework.formatAttackList(existingAtts) + " already exist in the base framework.",new GraphInstruction(argins,attins,pane),pane));
			
			return null;
		}
		else if(existingArgs.isEmpty()){
			Extension tmp = new Extension(expansion.getArguments(),expansion);
			interactor.addToCommands(new Command("The expanding framework does only contain new arguments and therefore may be an expansion.",tmp.toInstruction(Color.GREEN),pane));
		}
		else{
			attins = new ArrayList<SingleInstruction>();
			for(Attack a: expansion.getAttacks()){
				attins.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.GREEN));
			}
			interactor.addToCommands(new Command("The expanding framework does only contain new attacks and therefore may be an expansion",new GraphInstruction(null,attins,pane),pane));
		}
		
		interactor.addToCommands(new Command("We now check whether the given framework is a normal expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkNormalExpansion()){ //interactor commands in method
			return "normal";
		}
		
		interactor.addToCommands(new Command("We now check whether the given framework is a strong expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkStrongExpansion()){ //interactor commands in method
			return "strong";
		}
		
		interactor.addToCommands(new Command("We now check whether the given framework is a weak expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkWeakExpansion()){ //interactor commands in method
			return "weak";
		}
		
		interactor.addToCommands(new Command("We now check whether the given framework is a local expansion",expansion.toInstruction(Color.BLUE),pane));
		
		if(checkLocalExpansion()){ //interactor commands in method
			return "local";
		}
		
		interactor.addToCommands(new Command("The expanding framework does not fit any expansion type and is therefore not an expansion",expansion.toInstruction(Color.RED),pane));
		return null;
	}

	/**
	 * checks if the expanding framework constitutes a normal expansion to the base framework
	 * for that purpose we check the following:
	 * 	the exp does need non-empty arguments
	 * 	each of exp's attacks needs to either start or end within the exp
	 * @return whether the expanding framework is a normal expansion
	 */
	private boolean checkNormalExpansion() {
		if(expansion.getArguments().isEmpty()){
			interactor.addToCommands(new Command("The expanding framework is not a normal expansion since it does not contain any arguments.",null,pane));
			return false;
		}
		
		ArrayList<SingleInstruction> edgeins;
		for(Attack a: expansion.getAttacks()){
			edgeins = new ArrayList<SingleInstruction>();
			if(expansion.getArgument(a.getAttacker().getName()) == null && expansion.getArgument(a.getAttacked().getName()) == null){
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker()+a.getAttacker(),Color.RED));
				
				interactor.addToCommands(new Command("The expanding framework is not a normal expansion since its attack (" + a.getAttacker() + "," + a.getAttacked() + ") does not interact with arguments within it.",
						new GraphInstruction(null,edgeins,pane),pane));
				return false;
			}
			else{ //TODO maybe highlight the relevant node too?
				edgeins.add(expansion.getEdgeInstructionFromAttack(""+a.getAttacker()+a.getAttacker(),Color.GREEN));
				interactor.addToCommands(new Command("The attack (" + a.getAttacker() + "," + a.getAttacked() + ") interacts with the expanding framework.",new GraphInstruction(null,edgeins,pane),pane));
			}
		}
		
		interactor.addToCommands(new Command("All its attacks interact with the expanding framework, therefore it is a normal expansion.",expansion.toInstruction(Color.GREEN),pane));
		return true;
	}

	private boolean checkStrongExpansion() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkWeakExpansion() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkLocalExpansion() {
		// TODO Auto-generated method stub
		return false;
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
