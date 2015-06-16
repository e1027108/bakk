package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.SingleInstruction;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.paint.Color;
import edu.uci.ics.jung.graph.util.Pair;

public class Extension {

	private ArrayList<Argument> arguments; //arguments of extension
	private Framework framework; //framework from which the extension is derived
	private ArrayList<Attack> outgoingAttacks;
	private ArrayList<Argument> extensionAttacks;
	private ArrayList<Attack> incomingAttacks;
	
	public Extension(ArrayList<Argument> arguments, Framework framework) {
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		this.arguments.addAll(arguments);
		
		outgoingAttacks = new ArrayList<Attack>();
		extensionAttacks = new ArrayList<Argument>();
		incomingAttacks = new ArrayList<Attack>();
		readAttacks(framework.getAttacks());
	}

	private void readAttacks(ArrayList<Attack> attacks) {
		for(Attack a: attacks){
			Argument attacker = a.getAttacker();
			Argument attacked = a.getAttacked();
			
			if(arguments.contains(a.getAttacked())){
				incomingAttacks.add(a);
			}
			if(arguments.contains(attacker)){
				outgoingAttacks.add(a);
				if(!extensionAttacks.contains(attacker)){
					extensionAttacks.add(attacked);
				}
			}
		}
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
	
	public boolean isAdmissible(boolean write) {
		ArrayList<Attack> defeated = new ArrayList<Attack>();
		ArrayList<Attack> undefeated = new ArrayList<Attack>();
		GraphInstruction highlight = toInstruction(Color.GREEN);
		//ArrayList<SingleInstruction> defenceInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> attackerInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> undefeatedInstructions = new ArrayList<SingleInstruction>();
		
		for(Attack inc: incomingAttacks){			
			if(extensionAttacks.contains(inc.getAttacker())){
				defeated.add(inc);
				/*for(Attack out: outgoingAttacks){
					if(inc.equals(out.getAttacked())){
						defenceInstructions.add(new SingleInstruction(""+out.getAttacker().getName()+
								out.getAttacked().getName(),Color.GREEN));
					}
				}*/
			}
			else{
				undefeated.add(inc);
				attackerInstructions.add(new SingleInstruction(inc.getAttacker().getName(),Color.RED));
				undefeatedInstructions.add(new SingleInstruction("" + inc.getAttacker().getName() +
						inc.getAttacked().getName(),Color.RED));
			}
		}
		
		highlight.setEdgeInstructions(undefeatedInstructions);
		highlight.getNodeInstructions().addAll(attackerInstructions);
		
		if(undefeated.isEmpty()){
			if(write){
				framework.addToInteractor(new Command(format() + " defends all its arguments, so it is an admissible extension.", highlight));
			}
			return true;
		}
		else{
			if(write){
				framework.addToInteractor(new Command(format() + " does not defend against " + framework.formatAttackList(undefeated,1) + ", so it is not an admissible extension.", highlight));
			}
			return false;
		}
	}

	public boolean isPreferred(ArrayList<Extension> admissible) {
		for(Extension e: admissible){
			if(e.equals(this)){
				continue;
			}
			else if(isSubsetOf(e)){
				String format = format();
				GraphInstruction instruction = e.toInstruction(Color.GREEN);

				for(Argument a: e.getArguments()){
					if(!getArguments().contains(a)){
						instruction.getNodeInstructions().add(new SingleInstruction(""+a.getName(),Color.BLUE));
					}
				}

				framework.addToInteractor(new Command("Since " + format + " is a subset of " + e.format() + ", " + format + " is not preferred.", instruction));
				return false;
			}
		}

		framework.addToInteractor(new Command(format() + " is not the subset of another admissible extension, so it is a preferred extension.", toInstruction(Color.GREEN)));
		return true;
	}
	
	private boolean isSubsetOf(Extension e) {
		ArrayList<Argument> extArg = e.getArguments();

		for(Argument a: arguments){
			if(!extArg.contains(a)){
				return false;
			}
		}

		return true;
	}

	public GraphInstruction toInstruction(Color color) {
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
	
	public ArrayList<Argument> getArguments() {
		return arguments;
	}

}
