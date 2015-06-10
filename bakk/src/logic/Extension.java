package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.SingleInstruction;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import edu.uci.ics.jung.graph.util.Pair;

public class Extension {

	private ArrayList<Argument> arguments; //arguments of extension
	private Framework framework; //framework from which the extension is derived
	private ArrayList<Attack> attacks;
	
	public Extension(ArrayList<Argument> arguments, Framework framework) {
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		this.arguments.addAll(arguments);
		this.attacks = framework.getAttacks();
	}

	public boolean isConflictFree(boolean write) {
		ArrayList<Attack> violatingAttacks = new ArrayList<Attack>();
		
		for(Attack a: framework.getAttacks()){
			if(arguments.contains(a.getAttacker()) && arguments.contains(a.getAttacked())){
				violatingAttacks.add(a);
			}
		}
		
		if(!violatingAttacks.isEmpty()){
			if(write){
				String tmp = "";
				GraphInstruction instruction = toInstruction(Color.GREEN);
				ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

				for(Attack a: violatingAttacks){
					String attacked = "" + a.getAttacked().getName();
					if(!tmp.contains(attacked)){ //multiple arguments could attack the same one
						tmp += attacked;
					}
					edgeInstructions.add(new SingleInstruction((a.getAttacker().getName()+attacked),Color.RED));
				}

				tmp = framework.formatNameList(tmp);
				instruction.setEdgeInstructions(edgeInstructions);

				framework.addToInteractor(new Command(this.format() + " attacks the arguments " + tmp + "; thus it is not a conflict-free set!", instruction));
			}
			return false;
		}
		
		if(write){
			framework.addToInteractor(new Command(this.format() + " is a conflict-free set, because it does not attack its own arguments", toInstruction(Color.GREEN)));
		}
		return true;
	}

	private GraphInstruction toInstruction(Color color) {
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: arguments){
			String argName = String.valueOf(a.getName());
			nodeInstructions.add(new SingleInstruction(argName, color));
		}

		return new GraphInstruction(nodeInstructions, null);
	}

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

	/**
	 * checks if a set is admissible
	 * @param whether the results are to be handed to the Interactor
	 * @return if all the arguments are defended
	 */ //TODO is it better to if(write) everything to do with the interactor?
	public boolean isAdmissible(boolean write){
		if(!isConflictFree(false)){
			if(write){
				framework.addToInteractor(new Command(format() + " is not a conflict-free set, so it is not an admissible extension.", toInstruction(Color.RED)));
			}
			return false;
		}
		else{
			GraphInstruction highlight = toInstruction(Color.GREEN);
			highlight.setEdgeInstructions(new ArrayList<SingleInstruction>());
			ArrayList<SingleInstruction> relevantDefenses = new ArrayList<SingleInstruction>();
			ArrayList<SingleInstruction> relevantAttackers = new ArrayList<SingleInstruction>();
			
			ArrayList<Argument> attackers = new ArrayList<Argument>();
			ArrayList<Argument> undefended = new ArrayList<Argument>();
			
			for(Attack att: attacks){
				if(arguments.contains(att.getAttacked())){
					attackers.add(att.getAttacker());
				}
			}
			
			undefended.addAll(attackers);
			
			for(Attack att: attacks){
				if(attackers.contains(att.getAttacked()) && arguments.contains(att.getAttacker())){
					undefended.remove(att.getAttacked());
					relevantDefenses.add(new SingleInstruction(""+att.getAttacker().getName()+att.getAttacked().getName(), Color.GREEN));
					relevantAttackers.add(new SingleInstruction(""+att.getAttacked().getName(),Color.RED));
				}
				/*else if(attackers.contains(att.getAttacker()) && arguments.contains(att.getAttacked())){
					String attName = "" + att.getAttacker().getName();
					highlight.getNodeInstructions().add(new SingleInstruction(attName, Color.RED));			
					highlight.getEdgeInstructions().add(new SingleInstruction(attName+att.getAttacked().getName(), Color.RED));
				}*/ //TODO uncomment if attacks are always to be shown, comment for-loop below then
			}
			
			for(Attack att: attacks){
				if(undefended.contains(att.getAttacker())){
					String attName = "" + att.getAttacker().getName();
					highlight.getNodeInstructions().add(new SingleInstruction(attName, Color.RED));					
					highlight.getEdgeInstructions().add(new SingleInstruction(attName+att.getAttacked().getName(), Color.RED));
				}
			}
			
			if(undefended.isEmpty()){
				highlight.getEdgeInstructions().addAll(relevantDefenses);
				highlight.getNodeInstructions().addAll(relevantAttackers);
				if(write){
					framework.addToInteractor(new Command(format() + " defends all its arguments, so it is an admissible extension.", highlight));
				}
				return true;
			}
			else{
				if(write){
					framework.addToInteractor(new Command(format() + " does not defend against " + framework.formatArgumentList(undefended) + ", so it is not an admissible extension.", highlight));
				}
				return false;
			}
		}
	}

}
