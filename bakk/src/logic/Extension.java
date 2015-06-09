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
	
	public Extension(ArrayList<Argument> arguments, Framework framework) {
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		this.arguments.addAll(arguments);
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


}
